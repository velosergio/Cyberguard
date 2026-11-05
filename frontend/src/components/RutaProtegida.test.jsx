import { Route, Routes } from 'react-router-dom';
import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import RutaProtegida from './RutaProtegida.jsx';
import { renderConProveedores, sesionDePrueba } from '../test/helpers.jsx';

function AppDePrueba() {
  return (
    <Routes>
      <Route path="/login" element={<p>Pantalla de login</p>} />
      <Route element={<RutaProtegida />}>
        <Route path="/" element={<p>Panel privado</p>} />
        <Route element={<RutaProtegida roles={['ADMINISTRADOR']} />}>
          <Route path="/usuarios" element={<p>Gestión de usuarios</p>} />
        </Route>
      </Route>
    </Routes>
  );
}

describe('RutaProtegida', () => {
  it('redirige al login sin sesión', () => {
    renderConProveedores(<AppDePrueba />, { ruta: '/' });
    expect(screen.getByText('Pantalla de login')).toBeInTheDocument();
  });

  it('muestra el contenido autenticado', () => {
    renderConProveedores(<AppDePrueba />, { ruta: '/', sesion: sesionDePrueba() });
    expect(screen.getByText('Panel privado')).toBeInTheDocument();
  });

  it('niega el acceso si el rol no alcanza', () => {
    renderConProveedores(<AppDePrueba />, {
      ruta: '/usuarios',
      sesion: sesionDePrueba({ rol: 'CONSULTA' }),
    });
    expect(screen.getByText('Acceso denegado')).toBeInTheDocument();
  });

  it('permite el acceso al administrador', () => {
    renderConProveedores(<AppDePrueba />, {
      ruta: '/usuarios',
      sesion: sesionDePrueba({ rol: 'ADMINISTRADOR' }),
    });
    expect(screen.getByText('Gestión de usuarios')).toBeInTheDocument();
  });
});
