import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth.jsx';
import { CatalogosProvider } from '../catalogos.jsx';

export default function Layout() {
  const { usuario, esAdmin, puedeEscribir, salir } = useAuth();
  const navegar = useNavigate();

  const cerrarSesion = () => {
    salir();
    navegar('/login', { replace: true, state: { sesionCerrada: true } });
  };

  return (
    <>
      <nav className="barra">
        <Link className="marca" to="/">
          <span className="marca-marca">CG</span>
          <span>CyberGuard</span>
        </Link>
        <div className="menu">
          <NavLink to="/" end>Panel</NavLink>
          <NavLink to="/dispositivos">Dispositivos</NavLink>
          <NavLink to="/eventos">Eventos</NavLink>
          <NavLink to="/alertas">Alertas</NavLink>
          {puedeEscribir && <NavLink to="/reglas">Reglas</NavLink>}
          {puedeEscribir && <NavLink to="/reportes">Reportes</NavLink>}
          {esAdmin && <NavLink to="/usuarios">Usuarios</NavLink>}
        </div>
        <div className="sesion">
          <span className="quien">{usuario.usuario}</span>
          <button className="boton boton-fantasma" type="button" onClick={cerrarSesion}>
            Salir
          </button>
        </div>
      </nav>
      <main className="contenido">
        <CatalogosProvider>
          <Outlet />
        </CatalogosProvider>
      </main>
    </>
  );
}
