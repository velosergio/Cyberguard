import { MemoryRouter } from 'react-router-dom';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AuthProvider, useAuth } from './auth.jsx';
import { renderConProveedores, sesionDePrueba } from './test/helpers.jsx';
import * as apiModulo from './api.js';

function SondaAuth() {
  const { autenticado, esAdmin, puedeEscribir, usuario, salir, entrar } = useAuth();
  return (
    <div>
      <p>autenticado:{String(autenticado)}</p>
      <p>admin:{String(esAdmin)}</p>
      <p>escribir:{String(puedeEscribir)}</p>
      <p>usuario:{usuario?.usuario ?? 'nadie'}</p>
      <button type="button" onClick={salir}>
        Salir
      </button>
      <button type="button" onClick={() => entrar('admin', 'clave')}>
        Entrar
      </button>
    </div>
  );
}

describe('AuthProvider', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('expone permisos según el rol', () => {
    renderConProveedores(<SondaAuth />, { sesion: sesionDePrueba({ rol: 'ANALISTA' }) });
    expect(screen.getByText('autenticado:true')).toBeInTheDocument();
    expect(screen.getByText('admin:false')).toBeInTheDocument();
    expect(screen.getByText('escribir:true')).toBeInTheDocument();
  });

  it('salir limpia la sesión', async () => {
    const usuario = userEvent.setup();
    renderConProveedores(<SondaAuth />, { sesion: sesionDePrueba() });

    await usuario.click(screen.getByRole('button', { name: 'Salir' }));

    expect(screen.getByText('autenticado:false')).toBeInTheDocument();
    expect(apiModulo.leerSesion()).toBeNull();
  });

  it('entrar persiste el token recibido de la API', async () => {
    vi.spyOn(apiModulo.api, 'post').mockResolvedValue({
      token: 'nuevo',
      expiraEn: new Date(Date.now() + 3_600_000).toISOString(),
      usuario: { nombre: 'Admin', usuario: 'admin', rol: 'ADMINISTRADOR' },
    });
    const usuario = userEvent.setup();

    render(
      <MemoryRouter>
        <AuthProvider>
          <SondaAuth />
        </AuthProvider>
      </MemoryRouter>,
    );

    await usuario.click(screen.getByRole('button', { name: 'Entrar' }));

    await waitFor(() => expect(screen.getByText('usuario:admin')).toBeInTheDocument());
    expect(apiModulo.leerSesion()?.token).toBe('nuevo');
  });
});
