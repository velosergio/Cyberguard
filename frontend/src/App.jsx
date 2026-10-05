import { Route, Routes } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import RutaProtegida from './components/RutaProtegida.jsx';
import AlertaDetalle from './pages/AlertaDetalle.jsx';
import Alertas from './pages/Alertas.jsx';
import DispositivoForm from './pages/DispositivoForm.jsx';
import Dispositivos from './pages/Dispositivos.jsx';
import EventoForm from './pages/EventoForm.jsx';
import Eventos from './pages/Eventos.jsx';
import Login from './pages/Login.jsx';
import NoEncontrada from './pages/NoEncontrada.jsx';
import Panel from './pages/Panel.jsx';
import ReglaForm from './pages/ReglaForm.jsx';
import Reglas from './pages/Reglas.jsx';
import Reportes from './pages/Reportes.jsx';
import UsuarioForm from './pages/UsuarioForm.jsx';
import Usuarios from './pages/Usuarios.jsx';

const ADMIN = ['ADMINISTRADOR'];
const ESCRITURA = ['ADMINISTRADOR', 'ANALISTA'];

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route element={<RutaProtegida />}>
        <Route element={<Layout />}>
          <Route index element={<Panel />} />
          <Route path="dispositivos" element={<Dispositivos />} />
          <Route path="eventos" element={<Eventos />} />
          <Route path="alertas" element={<Alertas />} />
          <Route path="alertas/:id" element={<AlertaDetalle />} />

          <Route element={<RutaProtegida roles={ESCRITURA} />}>
            <Route path="dispositivos/nuevo" element={<DispositivoForm />} />
            <Route path="dispositivos/:id/editar" element={<DispositivoForm />} />
            <Route path="eventos/nuevo" element={<EventoForm />} />
            <Route path="reglas" element={<Reglas />} />
            <Route path="reportes" element={<Reportes />} />
          </Route>

          <Route element={<RutaProtegida roles={ADMIN} />}>
            <Route path="reglas/:id/editar" element={<ReglaForm />} />
            <Route path="usuarios" element={<Usuarios />} />
            <Route path="usuarios/nuevo" element={<UsuarioForm />} />
            <Route path="usuarios/:id/editar" element={<UsuarioForm />} />
          </Route>

          <Route path="*" element={<NoEncontrada />} />
        </Route>
      </Route>
    </Routes>
  );
}
