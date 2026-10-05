import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth.jsx';

export default function Login() {
  const { autenticado, entrar } = useAuth();
  const ubicacion = useLocation();
  const navegar = useNavigate();
  const [usuario, setUsuario] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState(null);
  const [enviando, setEnviando] = useState(false);

  const destino = ubicacion.state?.desde?.pathname ?? '/';
  if (autenticado) {
    return <Navigate to={destino} replace />;
  }

  const enviar = async (evento) => {
    evento.preventDefault();
    setError(null);
    setEnviando(true);
    try {
      await entrar(usuario, contrasena);
      navegar(destino, { replace: true });
    } catch (e) {
      setError(e.message);
      setEnviando(false);
    }
  };

  return (
    <div className="login-pagina">
      <main className="login-caja">
        <p className="marca">
          <span className="marca-marca">CG</span> CyberGuard
        </p>
        <h1>Ingreso</h1>
        <p className="subtitulo">Registro y análisis de eventos de una red autorizada.</p>
        {error && <p className="aviso error">{error}</p>}
        {ubicacion.state?.sesionCerrada && <p className="aviso">Sesión cerrada.</p>}
        <form onSubmit={enviar}>
          <label>
            Usuario
            <input
              value={usuario}
              onChange={(e) => setUsuario(e.target.value)}
              autoComplete="username"
              required
            />
          </label>
          <label>
            Contraseña
            <input
              type="password"
              value={contrasena}
              onChange={(e) => setContrasena(e.target.value)}
              autoComplete="current-password"
              required
            />
          </label>
          <button className="boton" type="submit" disabled={enviando}>
            Entrar
          </button>
        </form>
      </main>
    </div>
  );
}
