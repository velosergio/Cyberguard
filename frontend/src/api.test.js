import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import {
  ApiError,
  api,
  borrarSesion,
  guardarSesion,
  leerSesion,
  registrarAlExpirar,
} from './api.js';

describe('api', () => {
  beforeEach(() => {
    sessionStorage.clear();
    vi.stubGlobal('fetch', vi.fn());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
    registrarAlExpirar(() => {});
  });

  it('guarda y lee la sesión', () => {
    const sesion = { token: 'abc', expiraEn: '2099-01-01T00:00:00Z', usuario: { rol: 'CONSULTA' } };
    guardarSesion(sesion);
    expect(leerSesion()).toEqual(sesion);
    borrarSesion();
    expect(leerSesion()).toBeNull();
  });

  it('omite parámetros vacíos en la URL', async () => {
    fetch.mockResolvedValue({
      ok: true,
      json: async () => [{ id: 1 }],
    });

    await api.get('/api/eventos', { ip: '10.0.0.1', tipo: '', desde: null });

    expect(fetch).toHaveBeenCalledWith('/api/eventos?ip=10.0.0.1', expect.any(Object));
  });

  it('añade el Bearer cuando hay sesión', async () => {
    guardarSesion({ token: 'jwt-demo', expiraEn: '2099-01-01T00:00:00Z', usuario: { rol: 'ANALISTA' } });
    fetch.mockResolvedValue({
      ok: true,
      json: async () => ({ ok: true }),
    });

    await api.get('/api/panel');

    expect(fetch).toHaveBeenCalledWith(
      '/api/panel',
      expect.objectContaining({
        headers: expect.objectContaining({ Authorization: 'Bearer jwt-demo' }),
      }),
    );
  });

  it('traduce un 403 sin cuerpo', async () => {
    fetch.mockResolvedValue({
      ok: false,
      status: 403,
      json: async () => {
        throw new Error('sin cuerpo');
      },
    });

    await expect(api.get('/api/reglas')).rejects.toMatchObject({
      status: 403,
      message: 'No tienes permiso para esta acción.',
    });
  });

  it('cierra la sesión ante un 401 si había token', async () => {
    const alExpirar = vi.fn();
    registrarAlExpirar(alExpirar);
    guardarSesion({ token: 'vencido', expiraEn: '2099-01-01T00:00:00Z', usuario: { rol: 'CONSULTA' } });
    fetch.mockResolvedValue({
      ok: false,
      status: 401,
      json: async () => ({}),
    });

    await expect(api.get('/api/panel')).rejects.toBeInstanceOf(ApiError);
    expect(alExpirar).toHaveBeenCalledOnce();
  });

  it('falla con mensaje claro si no hay red', async () => {
    fetch.mockRejectedValue(new TypeError('Failed to fetch'));

    await expect(api.get('/api/panel')).rejects.toMatchObject({
      status: 0,
      message: 'No se pudo conectar con el servidor.',
    });
  });
});
