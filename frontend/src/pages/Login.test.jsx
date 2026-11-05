import { Route, Routes } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import Login from './Login.jsx';
import { renderConProveedores, sesionDePrueba } from '../test/helpers.jsx';
import * as apiModulo from '../api.js';

describe('Login', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('redirige al panel si ya hay sesión', () => {
    renderConProveedores(
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<p>Panel</p>} />
      </Routes>,
      { ruta: '/login', sesion: sesionDePrueba() },
    );
    expect(screen.getByText('Panel')).toBeInTheDocument();
  });

  it('muestra el error cuando fallan las credenciales', async () => {
    vi.spyOn(apiModulo.api, 'post').mockRejectedValue(
      new apiModulo.ApiError(401, 'Usuario o contraseña incorrectos'),
    );
    const usuario = userEvent.setup();

    renderConProveedores(
      <Routes>
        <Route path="/login" element={<Login />} />
      </Routes>,
      { ruta: '/login' },
    );

    await usuario.type(screen.getByLabelText('Usuario'), 'admin');
    await usuario.type(screen.getByLabelText('Contraseña'), 'mala');
    await usuario.click(screen.getByRole('button', { name: 'Entrar' }));

    expect(await screen.findByText('Usuario o contraseña incorrectos')).toBeInTheDocument();
  });

  it('entra y navega al destino', async () => {
    vi.spyOn(apiModulo.api, 'post').mockResolvedValue({
      token: 'token-ok',
      expiraEn: new Date(Date.now() + 3_600_000).toISOString(),
      usuario: { nombre: 'Admin', usuario: 'admin', rol: 'ADMINISTRADOR' },
    });
    const usuario = userEvent.setup();

    renderConProveedores(
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<p>Bienvenido</p>} />
      </Routes>,
      { ruta: '/login' },
    );

    await usuario.type(screen.getByLabelText('Usuario'), 'admin');
    await usuario.type(screen.getByLabelText('Contraseña'), 'clave-ok');
    await usuario.click(screen.getByRole('button', { name: 'Entrar' }));

    await waitFor(() => expect(screen.getByText('Bienvenido')).toBeInTheDocument());
  });
});
