# Instalación de CyberGuard

CyberGuard son dos aplicaciones que se ejecutan por separado: una API de Spring Boot (`backend/`, puerto 8080) y una interfaz de React (`frontend/`, puerto 5173). No hace falta publicarlas en Apache: la carpeta puede estar dentro de `htdocs`, pero Apache no la sirve.

La primera vez que arranca el backend, si MySQL está en marcha, crea la base `cyberguard`, las tablas y los datos de demostración (usuarios, reglas, dispositivos y eventos).

## Qué necesitas

| Requisito | Versión | Para qué |
| --- | --- | --- |
| JDK | 21 o superior | Compilar y ejecutar el backend |
| Node.js | 20 o superior (incluye `npm`) | Ejecutar y compilar el frontend |
| Laragon o XAMPP | MySQL o MariaDB en marcha | Base de datos local |
| Navegador | Cualquiera reciente | Abrir el panel |
| Internet | Solo la primera ejecución | El wrapper descarga Maven 3.9.11 y las dependencias; `npm install` baja las del frontend |

No instales Maven aparte. El backend incluye `mvnw.cmd` (en `backend/`), que lo descarga solo.

Comprueba Java en una terminal:

```bat
java -version
```

Tiene que decir 21 o más. Si aparece 17 u otra versión anterior, la aplicación no arranca: el proyecto se compila para Java 21. En Windows, apunta la terminal a un JDK válido antes de los comandos de Maven. Ejemplo con JDK 25:

```bat
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version
```

Ese cambio vale solo para esa ventana. Para dejarlo fijo, cambia la variable de usuario `JAVA_HOME` y pon `%JAVA_HOME%\bin` al inicio de `Path`.

## 1. Tener el código

Abre una terminal en la carpeta del proyecto, la que contiene `backend` y `frontend`. Por ejemplo, con Laragon:

```bat
cd c:\laragon\www\Cyberguard
```

## 2. Encender MySQL

- **Laragon:** abre Laragon y pulsa **Start All** (o inicia solo MySQL).
- **XAMPP:** abre el panel de control, pulsa **Start** en **MySQL** y espera a que el módulo quede en verde.

Las dos usan por defecto el usuario `root` con contraseña vacía, en `localhost` y el puerto `3306`.

## 3. Configurar la conexión (`.env`)

El backend lee la conexión y los secretos de `backend/.env`. La primera vez, copia la plantilla:

```bat
cd backend
copy .env.example .env
```

Si tu MySQL usa los valores por defecto, no hace falta cambiar nada. Si `root` tiene contraseña, o usas otro usuario, puerto o nombre de base, edita `backend/.env`:

```properties
DB_USERNAME=root
DB_PASSWORD=tu_contrasena
```

La URL incluye `createDatabaseIfNotExist=true`, así que no hace falta crear la base a mano. La lista completa de variables está en `conexion.md`.

`backend/.env` no se sube al repositorio (está en `.gitignore`); solo se versiona `.env.example`, que no lleva secretos. Si falta `.env`, el backend usa los mismos valores por defecto de la plantilla.

## 4. Arrancar el backend

En una terminal, dentro de la carpeta del proyecto:

```bat
cd backend
mvnw.cmd spring-boot:run
```

La primera vez tarda más: descarga Maven y las librerías. Cuando esté lista verás una línea parecida a esta:

```text
Started CyberguardApplication
```

Deja esa terminal abierta. Cerrarla detiene la API. El backend solo responde JSON: abrir http://localhost:8080 en el navegador no muestra ninguna pantalla.

## 5. Arrancar el frontend

En una segunda terminal:

```bat
cd frontend
npm install
npm run dev
```

`npm install` solo hace falta la primera vez, o cuando cambie `package.json`. Vite imprime la dirección cuando está listo:

```text
Local:   http://localhost:5173/
```

Abre http://localhost:5173. Vite reenvía las llamadas a `/api` al backend en el puerto 8080, así que ambos procesos tienen que estar encendidos.

## 6. Entrar

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `Admin123*` |
| Analista | `analista` | `Analista123*` |
| Consulta | `consulta` | `Consulta123*` |

El administrador puede crear usuarios y editar reglas. El analista registra dispositivos y eventos, atiende alertas y descarga reportes. Consulta solo mira el panel, los dispositivos, los eventos y las alertas.

Esas cuentas se crean una sola vez, cuando la tabla de usuarios está vacía. Reiniciar la aplicación no las duplica.

## 7. Ejecutar las pruebas

Las pruebas del backend usan una base H2 en memoria. MySQL puede estar apagado.

```bat
cd backend
mvnw.cmd test
```

Al terminar, Maven indica si todas pasaron. Cubren la clasificación de riesgo, el motor de reglas, la ventana del historial de eventos, la validación de fechas del formulario, el login con JWT y el acceso a la API según el rol.

## Arrancar el jar (opcional)

Si prefieres un archivo ejecutable en lugar de `spring-boot:run`:

```bat
cd backend
mvnw.cmd -DskipTests package
java -jar target\cyberguard-0.0.1-SNAPSHOT.jar
```

Sigue haciendo falta el JDK 21 o superior y MySQL en marcha. La API queda en el mismo puerto 8080.

## Publicar el frontend (opcional)

```bat
cd frontend
npm run build
```

El resultado queda en `frontend\dist`: archivos estáticos que cualquier servidor web puede servir. Si la API no está en la misma dirección, define `VITE_API_URL` antes de compilar (ver `frontend/.env.example`) y agrega el origen del frontend a `CYBERGUARD_CORS_ORIGENES` en `backend/.env`. Cambia también `CYBERGUARD_JWT_SECRET` por una clave propia de al menos 32 caracteres.

## Detenerla

En cada terminal (backend y frontend), pulsa `Ctrl+C` y confirma si Windows lo pide.

MySQL puede seguir encendido en Laragon o XAMPP. Los datos quedan en la base `cyberguard`.

## Si algo falla

**`java` no se reconoce o la versión es menor a 21.** Instala un JDK 21 o superior y define `JAVA_HOME` como en la sección de requisitos. Un error `UnsupportedClassVersionError` o una mención al bytecode 65 significa lo mismo: Java es demasiado antiguo.

**No conecta con MySQL.** El mensaje suele hablar de `Communications link failure` o de que se rechazó la conexión en el puerto 3306. Enciende MySQL en Laragon o XAMPP, revisa `DB_HOST` y `DB_PORT` en `backend/.env` y vuelve a lanzar `mvnw.cmd spring-boot:run`.

**Acceso denegado para `root`.** La contraseña de MySQL no está vacía. Escríbela en `DB_PASSWORD`, dentro de `backend/.env`.

**El puerto 8080 ya está en uso.** Otra copia de CyberGuard, u otro programa, ocupa ese puerto. Cierra la anterior o cambia `SERVER_PORT` en `backend/.env` y el destino del proxy en `frontend/vite.config.js`.

**El puerto 5173 ya está en uso.** Vite elige el siguiente puerto libre y lo imprime. Si entras por uno distinto de 5173, el backend rechaza la llamada por CORS: agrega ese origen a `CYBERGUARD_CORS_ORIGENES` en `backend/.env`.

**Error de columnas al arrancar después de actualizar el código** (por ejemplo, una columna `id_*` que no existe). `ddl-auto=update` no renombra columnas. En una base de laboratorio, bórrala y deja que el backend la cree de nuevo; los pasos están en `conexion.md`.

**La página no carga o el login dice «No se pudo conectar con el servidor».** Confirma que la terminal del backend sigue en `Started CyberguardApplication`, que el frontend está encendido y que entras por http://localhost:5173, no por el puerto 80 de Apache ni por el 8080.

**Te devuelve al login sin avisar.** El token dura 120 minutos y se borra al cerrar la pestaña. Ingresa de nuevo.

**`npm install` falla con `SELF_SIGNED_CERT_IN_CHAIN`.** La red intercepta el tráfico HTTPS con su propio certificado. Si el sistema ya confía en él, usa `set NODE_OPTIONS=--use-system-ca` antes de `npm install`. En Maven, el equivalente es `set MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT`.

**La primera ejecución se queda descargando.** Hace falta internet para bajar Maven, las dependencias y los paquetes de npm. Las siguientes arrancan con lo ya guardado en tu carpeta de usuario (`.m2`).

## Después de instalar

El uso de las pantallas, los roles, la API y el motor de reglas están en `GUIA.md`, en la carpeta `Docs`. Un arranque rápido también está en `README.md`.
