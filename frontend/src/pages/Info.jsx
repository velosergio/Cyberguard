import { useEffect, useRef, useState } from 'react';
import { Encabezado } from '../components/ui.jsx';
import {
  AnillosRoles, CapasArquitectura, DiagramaER, FlujoEvento, GlifoRelacion, GraficoReglas,
  MatrizNormalizacion, MedidorRiesgo, RamasGit, RedPortada,
} from '../components/info/Diagramas.jsx';
import {
  CONFLICTOS_SKILLS, DECISIONES, DESNORMALIZACIONES, EQUIPO, ESCALONES, LIMITES, PERMISOS,
  REGLAS_INICIALES, RELACIONES, SECCIONES, SKILLS, TERMINADO,
} from '../components/info/contenido.js';
import '../components/info/info.css';

const GLIFOS = {
  '1 : 0..N': ['uno', 'cero-muchos'],
  '1 : 0..1': ['uno', 'cero-uno'],
  'N : M': ['cero-muchos', 'cero-muchos'],
};

/** Marca con `visible` los elementos `.revelar` cuando entran en pantalla y sigue la sección actual. */
function useRecorrido(contenedor) {
  const [actual, setActual] = useState(SECCIONES[0].id);

  useEffect(() => {
    const raiz = contenedor.current;
    const revelar = new IntersectionObserver((entradas) => {
      entradas.forEach((e) => {
        if (e.isIntersecting) {
          e.target.classList.add('visible');
          revelar.unobserve(e.target);
        }
      });
    }, { threshold: 0.12 });
    raiz.querySelectorAll('.revelar').forEach((el) => revelar.observe(el));

    const seguir = new IntersectionObserver((entradas) => {
      entradas.forEach((e) => { if (e.isIntersecting) setActual(e.target.id); });
    }, { rootMargin: '-40% 0px -55% 0px' });
    raiz.querySelectorAll('section[id]').forEach((el) => seguir.observe(el));

    return () => { revelar.disconnect(); seguir.disconnect(); };
  }, [contenedor]);

  return actual;
}

function Seccion({ id, numero, titulo, simple, children }) {
  return (
    <section id={id} className="info-seccion">
      <header className="revelar">
        <span className="info-numero">{String(numero).padStart(2, '0')}</span>
        <h2>{titulo}</h2>
      </header>
      {simple && (
        <p className="en-simple revelar"><span aria-hidden="true">💡</span> {simple}</p>
      )}
      {children}
    </section>
  );
}

export default function Info() {
  const contenedor = useRef(null);
  const actual = useRecorrido(contenedor);
  const [seleccion, setSeleccion] = useState(['ESCANEO_PUERTOS', 'DISPOSITIVO_NO_REGISTRADO']);

  const alternar = (codigo) => setSeleccion((s) => (
    s.includes(codigo) ? s.filter((c) => c !== codigo) : [...s, codigo]
  ));
  const puntos = REGLAS_INICIALES
    .filter((r) => seleccion.includes(r.codigo))
    .reduce((suma, r) => suma + r.puntos, 0);

  return (
    <div className="info" ref={contenedor}>
      <Encabezado titulo="Info" subtitulo="Cómo funciona CyberGuard por dentro, en simple y en técnico." />

      <div className="info-cuerpo">
        <nav className="info-indice" aria-label="Secciones">
          {SECCIONES.map((s, i) => (
            <a key={s.id} href={`#${s.id}`} className={actual === s.id ? 'activo' : ''}>
              <span>{String(i + 1).padStart(2, '0')}</span>{s.titulo}
            </a>
          ))}
        </nav>

        <div className="info-contenido">
          <Seccion id="inicio" numero={1} titulo="Qué es CyberGuard">
            <div className="portada revelar">
              <div>
                <p className="portada-lema">Anota lo que pasa en una red autorizada, lo revisa con reglas y avisa cuando algo parece sospechoso.</p>
                <div className="voz simple"><b>En simple</b><p>Es como un cuaderno de vigilancia inteligente: tú escribes lo que viste, y el sistema te dice si ese patrón es preocupante.</p></div>
                <div className="voz tecnica"><b>Detalle técnico</b><p>Dos aplicaciones independientes: una API REST en Spring Boot 3.5 con Java 21 (puerto 8080) y un cliente React 19 con Vite (puerto 5173), que se comunican por HTTP + JSON con autenticación JWT.</p></div>
                <p className="aviso">CyberGuard <b>no</b> escanea la red, no captura tráfico y no bloquea conexiones. Una alerta es una señal para que una persona la revise.</p>
              </div>
              <RedPortada />
            </div>
            <div className="cifras revelar">
              {[['6', 'tablas'], ['6', 'reglas de detección'], ['3', 'roles'], ['4', 'niveles de riesgo']].map(([n, t]) => (
                <div key={t} className="cifra"><strong>{n}</strong><span>{t}</span></div>
              ))}
            </div>
          </Seccion>

          <Seccion
            id="funcionamiento" numero={2} titulo="Cómo funciona"
            simple="Todo empieza cuando alguien registra un evento. Desde ahí, el sistema lo guarda, lo analiza y decide si merece una alerta. Pulsa un paso para verlo."
          >
            <div className="revelar"><FlujoEvento /></div>
            <p className="ayuda revelar">El análisis ocurre solo al registrar, nunca al listar. Por eso abrir la lista de alertas no cambia nada.</p>
          </Seccion>

          <Seccion
            id="arquitectura" numero={3} titulo="Arquitectura por capas"
            simple="Como un edificio: cada piso tiene un trabajo y solo habla con el piso de abajo. Así un cambio en un piso no derrumba los demás."
          >
            <div className="revelar"><CapasArquitectura /></div>
            <div className="tarjetas-3 revelar">
              <article className="mini">
                <h3>SOLID</h3>
                <p>Responsabilidad única por capa; abierto/cerrado con reglas nuevas como clases nuevas; inyección por constructor con campos <code>final</code>.</p>
              </article>
              <article className="mini">
                <h3>DRY</h3>
                <p>Enums en <code>domain/enums</code>, permisos en <code>SecurityConfig</code>, errores en <code>ApiExceptionHandler</code>, HTTP del cliente en <code>api.js</code>.</p>
              </article>
              <article className="mini">
                <h3>Patrón estrategia</h3>
                <p>Cada regla implementa <code>ReglaDeteccion</code>. El motor no conoce las clases concretas: Spring se las entrega en una lista.</p>
              </article>
            </div>
          </Seccion>

          <Seccion
            id="datos" numero={4} titulo="Base de datos y relaciones"
            simple="La información vive en seis tablas. Pasa el cursor sobre una tabla para ver con cuáles está conectada."
          >
            <div className="revelar"><DiagramaER /></div>
            <div className="dos-voces revelar">
              <div className="voz simple"><b>En simple</b><p>El inventario (dispositivos) y el historial (eventos) viven separados: un evento puede hablar de una IP que todavía no está en el inventario.</p></div>
              <div className="voz tecnica"><b>Detalle técnico</b><p>MySQL/MariaDB. Hibernate crea el esquema con <code>ddl-auto=update</code>; claves <code>BIGINT AUTO_INCREMENT</code> con nombre <code>id_&lt;tabla&gt;</code>; enums como <code>STRING</code>; sin borrado en cascada.</p></div>
            </div>
          </Seccion>

          <Seccion
            id="cardinalidad" numero={5} titulo="Cardinalidad"
            simple="La cardinalidad dice cuántos de un lado se pueden unir con cuántos del otro: uno con uno, uno con muchos o muchos con muchos."
          >
            <div className="relaciones">
              {RELACIONES.map((r) => (
                <article key={`${r.desde}-${r.hacia}`} className="relacion revelar">
                  <header>
                    <span className="mono">{r.desde}</span>
                    {GLIFOS[r.notacion]
                      ? <GlifoRelacion izquierda={r.opcional ? 'cero-uno' : GLIFOS[r.notacion][0]} derecha={GLIFOS[r.notacion][1]} />
                      : <span className="sin-relacion">— aislada —</span>}
                    <span className="mono">{r.hacia}</span>
                    <span className="notacion">{r.notacion}</span>
                  </header>
                  <p>{r.simple}</p>
                  <p className="ayuda">{r.tecnico}</p>
                </article>
              ))}
            </div>
          </Seccion>

          <Seccion
            id="normalizacion" numero={6} titulo="Normalización"
            simple="Normalizar es ordenar los datos para no repetirlos: si un dato está en un solo lugar, no puede quedar contradicho en otro."
          >
            <ol className="escalera revelar">
              {ESCALONES.map((e, i) => (
                <li key={e.forma} style={{ '--i': i }}>
                  <strong>{e.forma}</strong>
                  <p>{e.simple}</p>
                  <p className="ayuda">{e.tecnico}</p>
                </li>
              ))}
            </ol>
            <div className="columnas-info revelar">
              <div className="panel"><h3>Qué cumple cada tabla</h3><MatrizNormalizacion /></div>
              <div className="panel">
                <h3>Desnormalizaciones deliberadas</h3>
                <ul className="desnormalizaciones">
                  {DESNORMALIZACIONES.map((d) => (
                    <li key={d.id}>
                      <span className="etiqueta-d">{d.id}</span>
                      <div><span className="mono">{d.donde}</span><p>{d.motivo}</p></div>
                      <span className={`riesgo riesgo-${d.riesgo}`}>Riesgo {d.riesgo.toLowerCase()}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
            <p className="ayuda revelar">En conjunto el esquema está en 3FN, salvo copias puntuales en <code>alertas</code> que se guardan a propósito como «foto» del análisis.</p>
          </Seccion>

          <Seccion
            id="motor" numero={7} titulo="Motor de reglas y riesgo"
            simple="Cada regla es una pista. Si se cumplen varias, sus puntos se suman y el total decide qué tan urgente es revisar. Pulsa las barras para simular."
          >
            <div className="columnas-info revelar">
              <div className="panel">
                <h3>Puntos iniciales por regla</h3>
                <GraficoReglas seleccion={seleccion} alternar={alternar} />
                <p className="ayuda">Valores de los datos de demostración. El administrador los ajusta en Reglas.</p>
              </div>
              <div className="panel">
                <h3>Simulador de clasificación</h3>
                <MedidorRiesgo puntos={puntos} />
                <div className="voz tecnica">
                  <b>Detalle técnico</b>
                  <p><code>ClasificadorRiesgo</code> suma, limita a 0–100 y aplica los cortes. La condición vive en <code>analysis/reglas/*</code>; los números en la tabla <code>reglas</code>. Una regla nueva es una clase <code>@Component</code> nueva, sin tocar <code>MotorAnalisis</code>.</p>
                </div>
              </div>
            </div>
            <div className="ventana revelar" aria-label="Ventana de tiempo del historial">
              <div className="ventana-eje">
                <span className="ventana-rango">fechaHora − ventana</span>
                <span className="ventana-ahora">evento nuevo</span>
                <span className="ventana-fuera">después: no cuenta</span>
              </div>
              <p className="ayuda">Cada regla mira hacia atrás desde la fecha del evento. El historial incluye el evento recién guardado; los posteriores no cuentan. Por eso el formulario rechaza fechas futuras.</p>
            </div>
          </Seccion>

          <Seccion
            id="seguridad" numero={8} titulo="Seguridad y roles"
            simple="Al entrar recibes una credencial temporal (token). Cada vez que pides algo, el servidor la revisa y comprueba si tu rol lo permite."
          >
            <div className="columnas-info revelar">
              <div className="panel centrado"><AnillosRoles /></div>
              <div className="panel">
                <table className="permisos">
                  <thead><tr><th>Acción</th><th>Admin</th><th>Analista</th><th>Consulta</th></tr></thead>
                  <tbody>
                    {PERMISOS.map((p) => (
                      <tr key={p.accion}>
                        <td>{p.accion}</td>
                        {p.roles.map((ok, i) => (
                          <td key={i} className={ok ? 'si' : 'no'} aria-label={ok ? 'permitido' : 'no permitido'}>{ok ? '●' : '○'}</td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
            <ol className="secuencia revelar">
              <li><b>Login</b><span>POST /api/auth/login con usuario y contraseña (BCrypt).</span></li>
              <li><b>Token</b><span>El servidor firma un JWT HS256; el cliente lo guarda en sessionStorage.</span></li>
              <li><b>Petición</b><span>api.js envía Authorization: Bearer &lt;token&gt;.</span></li>
              <li><b>Filtro</b><span>SecurityConfig valida firma, expiración y rol por ruta y método.</span></li>
              <li><b>401 / 403</b><span>Token vencido cierra la sesión; rol insuficiente se rechaza.</span></li>
            </ol>
            <p className="ayuda revelar">Ocultar un botón en la interfaz no protege nada: el control real está en el backend.</p>
          </Seccion>

          <Seccion
            id="gobernanza" numero={9} titulo="Gobernanza del proyecto"
            simple="Son las reglas del equipo: quién decide qué, cómo se sube el código y cuándo una tarea está terminada."
          >
            <div className="limites revelar">
              <h3>Límites que no se negocian</h3>
              <ul>{LIMITES.map((l) => <li key={l}>{l}</li>)}</ul>
            </div>
            <div className="equipo revelar">
              {EQUIPO.map((p) => (
                <article key={p.nombre} className="persona">
                  <span className="avatar">{p.nombre.split(' ').slice(0, 2).map((n) => n[0]).join('')}</span>
                  <div><strong>{p.nombre}</strong><span>{p.rol} · {p.area}</span></div>
                </article>
              ))}
            </div>
            <div className="panel revelar">
              <h3>Flujo con Git</h3>
              <RamasGit />
              <p className="ayuda">Nadie hace push a <code>main</code>. Ramas <code>feature/</code>, <code>fix/</code> o <code>docs/</code>, commits en español e imperativo, y un PR aprobado por otro integrante.</p>
            </div>
            <div className="columnas-info revelar">
              <div className="panel">
                <h3>Definición de terminado</h3>
                <ul className="checklist">{TERMINADO.map((t, i) => <li key={t} style={{ '--i': i }}>{t}</li>)}</ul>
              </div>
              <div className="panel">
                <h3>Quién decide</h3>
                <table>
                  <tbody>{DECISIONES.map(([q, quien]) => <tr key={q}><td>{q}</td><td className="ayuda">{quien}</td></tr>)}</tbody>
                </table>
                <p className="ayuda">Metodología Scrum por sprints: planeación, seguimiento, revisión con demostración y retrospectiva.</p>
              </div>
            </div>
          </Seccion>

          <Seccion
            id="skills" numero={10} titulo="Skills de asistentes de IA"
            simple="Las skills son guías que se les dan a los asistentes de IA (Claude Code, Cursor…) para que escriban código siguiendo buenas prácticas."
          >
            <div className="skills revelar">
              {SKILLS.map((s, i) => (
                <article key={s.nombre} className="skill" style={{ '--i': i }}>
                  <span className="skill-icono" aria-hidden="true">{s.icono}</span>
                  <strong className="mono">{s.nombre}</strong>
                  <span className="skill-origen">{s.origen}</span>
                  <p>{s.uso}</p>
                </article>
              ))}
            </div>
            <div className="panel revelar">
              <h3>Cuando una skill contradice al proyecto, manda el proyecto</h3>
              <table>
                <thead><tr><th>La skill sugiere</th><th>En CyberGuard se hace</th></tr></thead>
                <tbody>{CONFLICTOS_SKILLS.map(([a, b]) => <tr key={a}><td>{a}</td><td>{b}</td></tr>)}</tbody>
              </table>
              <p className="ayuda">Se instalan con <code>npx skills</code> en <code>.claude/skills/</code> y <code>.agents/skills/</code>, con su hash en <code>skills-lock.json</code>. Agregar una es decisión del equipo, en su propio PR.</p>
            </div>
          </Seccion>
        </div>
      </div>
    </div>
  );
}
