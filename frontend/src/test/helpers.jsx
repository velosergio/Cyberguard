import { MemoryRouter } from 'react-router-dom';
import { render } from '@testing-library/react';
import { AuthProvider } from '../auth.jsx';
import { guardarSesion } from '../api.js';

export function sesionDePrueba({ rol = 'ADMINISTRADOR', token = 'token-prueba' } = {}) {
  const expiraEn = new Date(Date.now() + 60 * 60 * 1000).toISOString();
  return {
    token,
    expiraEn,
    usuario: { nombre: 'Usuario prueba', usuario: 'prueba', rol },
  };
}

export function iniciarSesion(opciones) {
  guardarSesion(sesionDePrueba(opciones));
}

export function renderConProveedores(ui, { ruta = '/', sesion } = {}) {
  if (sesion) {
    guardarSesion(sesion);
  }
  return render(
    <MemoryRouter initialEntries={[ruta]}>
      <AuthProvider>{ui}</AuthProvider>
    </MemoryRouter>,
  );
}
