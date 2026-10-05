import { EnlaceBoton } from '../components/ui.jsx';

export default function NoEncontrada() {
  return (
    <section className="panel">
      <h1>Página no encontrada</h1>
      <p className="subtitulo">La dirección no existe en CyberGuard.</p>
      <EnlaceBoton to="/">Volver al panel</EnlaceBoton>
    </section>
  );
}
