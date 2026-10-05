import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth.jsx';

/** Sin sesión lleva al login. Con `roles`, además exige uno de esos roles. */
export default function RutaProtegida({ roles }) {
  const { autenticado, usuario } = useAuth();
  const ubicacion = useLocation();

  if (!autenticado) {
    return <Navigate to="/login" replace state={{ desde: ubicacion }} />;
  }
  if (roles && !roles.includes(usuario.rol)) {
    return (
      <section className="panel">
        <h1>Acceso denegado</h1>
        <p className="subtitulo">Tu rol no tiene permiso para ver esta página.</p>
      </section>
    );
  }
  return <Outlet />;
}
