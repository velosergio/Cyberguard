import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, borrarSesion, guardarSesion, leerSesion, registrarAlExpirar } from './api.js';

const AuthContext = createContext(null);

function sesionVigente() {
  const sesion = leerSesion();
  return sesion && new Date(sesion.expiraEn) > new Date() ? sesion : null;
}

export function AuthProvider({ children }) {
  const [sesion, setSesion] = useState(sesionVigente);

  const salir = useCallback(() => {
    borrarSesion();
    setSesion(null);
  }, []);

  useEffect(() => {
    registrarAlExpirar(salir);
  }, [salir]);

  const valor = useMemo(() => {
    const rol = sesion?.usuario.rol;
    return {
      usuario: sesion?.usuario ?? null,
      autenticado: sesion !== null,
      esAdmin: rol === 'ADMINISTRADOR',
      puedeEscribir: rol === 'ADMINISTRADOR' || rol === 'ANALISTA',
      salir,
      async entrar(usuario, contrasena) {
        const respuesta = await api.post('/api/auth/login', { usuario, contrasena });
        const nueva = { token: respuesta.token, expiraEn: respuesta.expiraEn, usuario: respuesta.usuario };
        guardarSesion(nueva);
        setSesion(nueva);
      },
    };
  }, [sesion, salir]);

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
