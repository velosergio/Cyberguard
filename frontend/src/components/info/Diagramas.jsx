import { useEffect, useState } from 'react';
import {
  CAPAS, FORMAS, MATRIZ_NORMALIZACION, NIVELES, PASOS_FLUJO, REGLAS_INICIALES, TABLAS,
} from './contenido.js';

/* ---------- Portada: red autorizada con paquetes viajando ---------- */

const NODOS = [
  { x: 60, y: 70, n: 'PC' }, { x: 60, y: 190, n: 'CAM' }, { x: 200, y: 40, n: 'SRV' },
  { x: 200, y: 220, n: '?' }, { x: 330, y: 130, n: 'CG' },
];
const ENLACES = ['M60 70 L330 130', 'M60 190 L330 130', 'M200 40 L330 130', 'M200 220 L330 130'];

export function RedPortada() {
  return (
    <svg className="red-portada" viewBox="0 0 400 260" role="img" aria-label="Equipos de la red enviando eventos a CyberGuard">
      {ENLACES.map((d, i) => (
        <g key={d}>
          <path d={d} className="red-enlace" />
          <circle r="4" className={i === 3 ? 'red-paquete sospechoso' : 'red-paquete'}>
            <animateMotion dur={`${2.4 + i * 0.5}s`} repeatCount="indefinite" path={d} />
          </circle>
        </g>
      ))}
      {NODOS.map((nodo) => {
        const centro = nodo.n === 'CG';
        return (
          <g key={nodo.n} className={nodo.n === '?' ? 'red-nodo desconocido' : 'red-nodo'}>
            <circle cx={nodo.x} cy={nodo.y} r={centro ? 30 : 20} className="red-pulso" />
            <circle cx={nodo.x} cy={nodo.y} r={centro ? 30 : 20} className={centro ? 'red-centro' : ''} />
            <text x={nodo.x} y={nodo.y + 4} textAnchor="middle">{nodo.n}</text>
          </g>
        );
      })}
    </svg>
  );
}

/* ---------- Flujo del registro de un evento ---------- */

export function FlujoEvento() {
  const [activo, setActivo] = useState(0);
  const [manual, setManual] = useState(false);

  useEffect(() => {
    if (manual) return undefined;
    const id = setInterval(() => setActivo((a) => (a + 1) % PASOS_FLUJO.length), 3200);
    return () => clearInterval(id);
  }, [manual]);

  const elegir = (i) => { setManual(true); setActivo(i); };
  const paso = PASOS_FLUJO[activo];

  return (
    <div className="flujo">
      <ol className="flujo-pasos" style={{ '--avance': activo / (PASOS_FLUJO.length - 1) }}>
        {PASOS_FLUJO.map((p, i) => (
          <li key={p.titulo}>
            <button
              type="button"
              className={i === activo ? 'activo' : i < activo ? 'hecho' : ''}
              onClick={() => elegir(i)}
              aria-pressed={i === activo}
            >
              <span className="flujo-num">{i + 1}</span>
              {p.titulo}
            </button>
          </li>
        ))}
      </ol>
      <div className="dos-voces" key={activo}>
        <div className="voz simple"><b>En simple</b><p>{paso.simple}</p></div>
        <div className="voz tecnica"><b>Detalle técnico</b><p>{paso.tecnico}</p></div>
      </div>
      {manual && (
        <button type="button" className="boton boton-fantasma reanudar" onClick={() => setManual(false)}>
          ▶ Reanudar recorrido
        </button>
      )}
    </div>
  );
}

/* ---------- Capas de la arquitectura ---------- */

export function CapasArquitectura() {
  const [elegida, setElegida] = useState(4);
  const capa = CAPAS[elegida];
  return (
    <div className="capas">
      <div className="capas-pila">
        <div className="capas-via" aria-hidden="true"><span className="capas-peticion" /></div>
        {CAPAS.map((c, i) => (
          <button
            key={c.nombre}
            type="button"
            className={`capa capa-${i}${i === elegida ? ' activa' : ''}`}
            onMouseEnter={() => setElegida(i)}
            onFocus={() => setElegida(i)}
            onClick={() => setElegida(i)}
          >
            <strong>{c.nombre}</strong>
            <span className="mono">{c.ruta}</span>
          </button>
        ))}
      </div>
      <aside className="capas-detalle" key={elegida}>
        <h3>{capa.nombre}</h3>
        <div className="voz simple"><b>En simple</b><p>{capa.simple}</p></div>
        <div className="voz tecnica"><b>Detalle técnico</b><p>{capa.tecnico}</p></div>
        <p className="ayuda">Cada capa solo habla con la de abajo. La petición baja hasta la base y la respuesta sube en JSON.</p>
      </aside>
    </div>
  );
}

/* ---------- Diagrama entidad-relación con notación pata de gallo ---------- */

const ANCHO = 210;
const FILA = 20;
const CABECERA = 30;
const POSICIONES = {
  usuarios: { x: 20, y: 20 },
  reglas: { x: 820, y: 20, ancho: 200 },
  dispositivos: { x: 20, y: 300 },
  eventos: { x: 290, y: 300 },
  alertas: { x: 560, y: 300 },
  alerta_regla: { x: 830, y: 330, ancho: 190 },
};

/** Extremo de una relación. Se dibuja como si la entidad estuviera a la derecha y se rota. */
function Extremo({ x, y, giro, tipo }) {
  const partes = [];
  if (tipo === 'muchos' || tipo === 'cero-muchos') {
    partes.push(<path key="pata" d="M-14 0 L0 -8 M-14 0 L0 8 M-14 0 L0 0" />);
    partes.push(tipo === 'muchos'
      ? <path key="b" d="M-18 -7 L-18 7" />
      : <circle key="c" cx="-23" cy="0" r="4.5" className="er-circulo" />);
  } else {
    partes.push(<path key="b1" d="M-8 -7 L-8 7" />);
    partes.push(tipo === 'uno'
      ? <path key="b2" d="M-14 -7 L-14 7" />
      : <circle key="c" cx="-18" cy="0" r="4.5" className="er-circulo" />);
  }
  return <g transform={`translate(${x} ${y}) rotate(${giro})`}>{partes}</g>;
}

const ARISTAS = [
  {
    entre: ['dispositivos', 'eventos'], d: 'M230 370 L290 370',
    extremos: [{ x: 230, y: 370, giro: 180, tipo: 'cero-uno' }, { x: 290, y: 370, giro: 0, tipo: 'cero-muchos' }],
  },
  {
    entre: ['eventos', 'alertas'], d: 'M500 370 L560 370',
    extremos: [{ x: 500, y: 370, giro: 180, tipo: 'uno' }, { x: 560, y: 370, giro: 0, tipo: 'cero-uno' }],
  },
  {
    entre: ['alertas', 'alerta_regla'], d: 'M770 375 L830 375',
    extremos: [{ x: 770, y: 375, giro: 180, tipo: 'uno' }, { x: 830, y: 375, giro: 0, tipo: 'cero-muchos' }],
  },
  {
    entre: ['reglas', 'alerta_regla'], d: 'M925 210 L925 330',
    extremos: [{ x: 925, y: 210, giro: -90, tipo: 'uno' }, { x: 925, y: 330, giro: 90, tipo: 'cero-muchos' }],
  },
  {
    entre: ['dispositivos', 'alertas'], d: 'M125 510 L125 545 L665 545 L665 510', copia: true,
    extremos: [{ x: 125, y: 510, giro: -90, tipo: 'cero-uno' }, { x: 665, y: 510, giro: -90, tipo: 'cero-muchos' }],
  },
];

export function DiagramaER() {
  const [foco, setFoco] = useState('eventos');
  const tabla = TABLAS[foco];

  return (
    <div className="er">
      {/* group y no img: img vuelve "una sola imagen" todo su contenido y escondería las tablas enfocables. */}
      <svg viewBox="0 0 1040 565" className="er-svg" role="group" aria-label="Diagrama entidad-relación de la base cyberguard">
        <g className="er-leyenda" transform="translate(300 40)">
          <text x="0" y="0" className="er-leyenda-titulo">Cómo leer las líneas</text>
          {[
            ['uno', 'exactamente uno'], ['cero-uno', 'cero o uno'],
            ['cero-muchos', 'cero o muchos'], ['muchos', 'uno o muchos'],
          ].map(([tipo, texto], i) => (
            <g key={tipo} transform={`translate(0 ${28 + i * 30})`}>
              <g className="er-linea">
                <path d="M0 0 L60 0" />
                <Extremo x={60} y={0} giro={0} tipo={tipo} />
              </g>
              <text x="78" y="5">{texto}</text>
            </g>
          ))}
          <g transform="translate(0 150)">
            <g className="er-linea copia"><path d="M0 0 L60 0" /></g>
            <text x="78" y="5">dato copiado (desnormalización)</text>
          </g>
        </g>

        {ARISTAS.map((a) => {
          const activa = a.entre.includes(foco);
          return (
            <g key={a.d} className={`er-linea${a.copia ? ' copia' : ''}${activa ? ' activa' : ''}`}>
              <path d={a.d} />
              {a.extremos.map((e) => <Extremo key={`${e.x}-${e.y}`} {...e} />)}
            </g>
          );
        })}

        {Object.entries(TABLAS).map(([nombre, t]) => {
          const { x, y, ancho = ANCHO } = POSICIONES[nombre];
          const alto = CABECERA + t.columnas.length * FILA + 8;
          const relacionada = ARISTAS.some((a) => a.entre.includes(foco) && a.entre.includes(nombre));
          return (
            <g
              key={nombre}
              className={`er-tabla${foco === nombre ? ' foco' : ''}${relacionada ? ' relacionada' : ''}`}
              transform={`translate(${x} ${y})`}
              tabIndex="0"
              role="button"
              aria-label={`Tabla ${nombre}`}
              onMouseEnter={() => setFoco(nombre)}
              onFocus={() => setFoco(nombre)}
              onClick={() => setFoco(nombre)}
            >
              <rect width={ancho} height={alto} rx="10" className="er-caja" />
              <path d={`M0 ${CABECERA} H${ancho}`} className="er-separador" />
              <text x="12" y="20" className="er-nombre">{nombre}</text>
              {t.columnas.map(([col, marca], i) => (
                <g key={col} transform={`translate(12 ${CABECERA + 16 + i * FILA})`}>
                  <text className={marca.includes('PK') ? 'er-col pk' : 'er-col'}>{col}</text>
                  {marca && <text x={ancho - 24} textAnchor="end" className="er-marca">{marca}</text>}
                </g>
              ))}
            </g>
          );
        })}
        <text x="20" y="250" className="er-nota">usuarios: sin claves foráneas</text>
      </svg>
      <div className="er-detalle" key={foco}>
        <span className="mono">{foco}</span>
        <p>{tabla.simple}</p>
        <p className="ayuda">PK clave primaria · FK clave foránea · ∅ admite nulo · UQ única · ENUM valor de un enum guardado como texto</p>
      </div>
    </div>
  );
}

/* ---------- Glifo pequeño de cardinalidad ---------- */

export function GlifoRelacion({ izquierda, derecha }) {
  return (
    <svg viewBox="0 0 140 24" className="glifo" aria-hidden="true">
      <g className="er-linea activa">
        <path d="M24 12 L116 12" />
        <Extremo x={24} y={12} giro={180} tipo={izquierda} />
        <Extremo x={116} y={12} giro={0} tipo={derecha} />
      </g>
    </svg>
  );
}

/* ---------- Normalización: matriz y escalera ---------- */

const SIMBOLO = { si: '✓', casi: '✓*', no: '✗' };
const TEXTO = { si: 'cumple', casi: 'cumple con un dato derivado', no: 'no cumple' };

export function MatrizNormalizacion() {
  return (
    <table className="matriz">
      <caption className="ayuda">✓ cumple · ✓* cumple, con un dato derivado deliberado · ✗ no cumple a propósito</caption>
      <thead>
        <tr><th>Tabla</th>{FORMAS.map((f) => <th key={f}>{f}</th>)}</tr>
      </thead>
      <tbody>
        {MATRIZ_NORMALIZACION.map((fila, i) => (
          <tr key={fila.tabla}>
            <td className="mono">{fila.tabla}</td>
            {fila.valores.map((v, j) => (
              <td key={FORMAS[j]}>
                <span
                  className={`celda celda-${v}`}
                  style={{ animationDelay: `${(i * 5 + j) * 35}ms` }}
                  title={`${fila.tabla} · ${FORMAS[j]}: ${TEXTO[v]}`}
                >
                  {SIMBOLO[v]}
                </span>
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}

/* ---------- Motor: barras de puntuación y simulador de riesgo ---------- */

export function GraficoReglas({ seleccion, alternar }) {
  const maximo = Math.max(...REGLAS_INICIALES.map((r) => r.puntos));
  return (
    <div className="barras" role="group" aria-label="Puntos iniciales por regla; pulsa para incluirla en la simulación">
      {REGLAS_INICIALES.map((r, i) => (
        <button
          key={r.codigo}
          type="button"
          className={`barra-fila${seleccion.includes(r.codigo) ? ' elegida' : ''}`}
          onClick={() => alternar(r.codigo)}
          aria-pressed={seleccion.includes(r.codigo)}
        >
          <span className="barra-nombre">{r.nombre}</span>
          <span className="barra-pista">
            <span className="barra-valor" style={{ '--ancho': `${(r.puntos / maximo) * 100}%`, animationDelay: `${i * 90}ms` }} />
          </span>
          <span className="barra-numero">{r.puntos} pts</span>
          <span className="barra-tip">
            <b>{r.codigo}</b>
            {r.cuando}
            <br />Umbral {r.umbral} · ventana {r.ventana ? `${r.ventana} min` : 'sin ventana'}
          </span>
        </button>
      ))}
    </div>
  );
}

export function MedidorRiesgo({ puntos }) {
  const total = Math.min(100, puntos);
  const nivel = NIVELES.find((n) => total >= n.desde && total <= n.hasta);
  return (
    <div className="medidor">
      <div className="medidor-escala">
        {NIVELES.map((n) => (
          <div
            key={n.nivel}
            className={`medidor-tramo tramo-${n.nivel}${n === nivel && puntos > 0 ? ' actual' : ''}`}
            style={{ flexBasis: `${n.hasta - n.desde + (n.desde === 0 ? 0 : 1)}%` }}
          >
            <span>{n.etiqueta}</span>
            <small>{n.desde}–{n.hasta}</small>
          </div>
        ))}
        <span className="medidor-aguja" style={{ left: `${total}%` }} aria-hidden="true" />
      </div>
      <p className="medidor-lectura" aria-live="polite">
        {puntos === 0 ? (
          'Ninguna regla elegida: no se crea alerta.'
        ) : (
          <>
            Suma {puntos}{puntos > 100 ? ' → tope 100' : ''} ·{' '}
            <span className={`nivel nivel-${nivel.nivel}`}>{nivel.etiqueta}</span>
          </>
        )}
      </p>
    </div>
  );
}

/* ---------- Seguridad: anillos de roles ---------- */

export function AnillosRoles() {
  return (
    <svg viewBox="0 0 300 260" className="anillos" role="img" aria-label="Administrador contiene a Analista, que contiene a Consulta">
      <circle cx="150" cy="135" r="120" className="anillo a1" />
      <circle cx="150" cy="160" r="82" className="anillo a2" />
      <circle cx="150" cy="185" r="45" className="anillo a3" />
      <text x="150" y="40" textAnchor="middle">ADMINISTRADOR</text>
      <text x="150" y="102" textAnchor="middle">ANALISTA</text>
      <text x="150" y="190" textAnchor="middle">CONSULTA</text>
    </svg>
  );
}

/* ---------- Gobernanza: ramas de Git ---------- */

export function RamasGit() {
  return (
    <svg viewBox="0 0 640 190" className="git" role="img" aria-label="Ramas cortas que salen de main y vuelven por pull request">
      <path d="M20 95 H620" className="git-main" />
      <path d="M90 95 C120 95 120 40 160 40 H270 C310 40 310 95 340 95" className="git-rama r1" />
      <path d="M220 95 C250 95 250 150 290 150 H420 C460 150 460 95 490 95" className="git-rama r2" />
      {[20, 90, 220, 340, 490, 620].map((x) => <circle key={x} cx={x} cy="95" r="7" className="git-commit" />)}
      {[[180, 40], [240, 40], [310, 150], [380, 150]].map(([x, y]) => <circle key={`${x}${y}`} cx={x} cy={y} r="6" className="git-commit rama" />)}
      <text x="20" y="125">main</text>
      <text x="160" y="25">feature/regla-horario</text>
      <text x="290" y="178">fix/filtro-fechas</text>
      <text x="318" y="78" className="git-pr">PR ✓</text>
      <text x="468" y="128" className="git-pr">PR ✓</text>
    </svg>
  );
}
