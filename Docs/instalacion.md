# Instalación de CyberGuard

CyberGuard son dos aplicaciones que se ejecutan por separado: una API de Spring Boot (`backend/`, puerto 8080) y una interfaz de React (`frontend/`, puerto 5173). No hace falta publicarlas en Apache: la carpeta puede estar dentro de `htdocs`, pero Apache no la sirve.

La primera vez que arranca el backend, si MySQL está en marcha, crea la base `cyberguard`, las tablas y los datos de demostración (usuarios, reglas, dispositivos y eventos).

## Qué necesitas

| Requisito | Versión | Para qué |
| --- | --- | --- |
| JDK | 21 o superior | Compilar y ejecutar el backend |
| Node.js | 20 o superior (incluye `npm`) | Ejecutar y compilar el frontend |
| XAMPP | MySQL o MariaDB en marcha | Base de datos local |
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

Abre una terminal en la carpeta del proyecto, la que contiene `backend` y `frontend`. En este equipo suele ser:

```bat
cd c:\xampp\htdocs\mangomorado\Cyberguard
```

## 2. Encender MySQL

1. Abre el panel de control de XAMPP.
2. Pulsa **Start** en **MySQL**.
3. Espera a que el módulo quede en verde.

La instalación local de XAMPP usa el usuario `root` con contraseña vacía, en `localhost` y el puerto `3306`. Esos son los valores de `backend/src/main/resources/application.properties`. La URL incluye `createDatabaseIfNotExist=true`, así que no hace falta crear la base a mano.

Si `root` tiene contraseña, edita esa línea y deja el resto igual:

```properties
spring.datasource.password=tu_contrasena
```

No subas contraseñas reales al repositorio. En este proyecto el valor vacío es el de una instalación local de XAMPP.

## 3. Arrancar el backend

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

## 4. Arrancar el frontend

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

## 5. Entrar

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `Admin123*` |
| Analista | `analista` | `Analista123*` |
| Consulta | `consulta` | `Consulta123*` |

El administrador puede crear usuarios y editar reglas. El analista registra dispositivos y eventos, atiende alertas y descarga reportes. Consulta solo mira el panel, los dispositivos, los eventos y las alertas.

Esas cuentas se crean una sola vez, cuando la tabla de usuarios está vacía. Reiniciar la aplicación no las duplica.

## 6. Ejecutar las pruebas

Las pruebas del backend usan una base H2 en memoria. MySQL puede estar apagado.

```bat
cd backend
mvnw.cmd test
```

Al terminar, Maven indica si todas pasaron. Cubren la clasificación de riesgo, el motor de reglas, el login con JWT y el acceso a la API según el rol.

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

El resultado queda en `frontend\dist`: archivos estáticos que cualquier servidor web puede servir. Si la API no está en la misma dirección, define `VITE_API_URL` antes de compilar (ver `frontend/.env.example`) y agrega el origen del frontend a la variable de entorno `CYBERGUARD_CORS_ORIGENES` del backend. Cambia también `CYBERGUARD_JWT_SECRET` por una clave propia de al menos 32 caracteres.

## Detenerla

En cada terminal (backend y frontend), pulsa `Ctrl+C` y confirma si Windows lo pide.

MySQL puede seguir encendido en XAMPP. Los datos quedan en la base `cyberguard`.

## Si algo falla

**`java` no se reconoce o la versión es menor a 21.** Instala un JDK 21 o superior y define `JAVA_HOME` como en la sección de requisitos. Un error `UnsupportedClassVersionError` o una mención al bytecode 65 significa lo mismo: Java es demasiado antiguo.

**No conecta con MySQL.** El mensaje suele hablar de `Communications link failure` o de que se rechazó la conexión en el puerto 3306. Enciende MySQL en XAMPP y vuelve a lanzar `mvnw.cmd spring-boot:run`.

**Acceso denegado para `root`.** La contraseña de MySQL no está vacía. Escríbela en `spring.datasource.password`.

**El puerto 8080 ya está en uso.** Otra copia de CyberGuard, u otro programa, ocupa ese puerto. Cierra la anterior o cambia `server.port` en `application.properties` y el destino del proxy en `frontend/vite.config.js`.

**El puerto 5173 ya está en uso.** Vite elige el siguiente puerto libre y lo imprime. Si entras por uno distinto de 5173, el backend rechaza la llamada por CORS: agrega ese origen a `CYBERGUARD_CORS_ORIGENES`.

**La página no carga o el login dice «No se pudo conectar con el servidor».** Confirma que la terminal del backend sigue en `Started CyberguardApplication`, que el frontend está encendido y que entras por http://localhost:5173, no por el puerto 80 de Apache ni por el 8080.

**Te devuelve al login sin avisar.** El token dura 120 minutos y se borra al cerrar la pestaña. Ingresa de nuevo.

**`npm install` falla con `SELF_SIGNED_CERT_IN_CHAIN`.** La red intercepta el tráfico HTTPS con su propio certificado. Si el sistema ya confía en él, usa `set NODE_OPTIONS=--use-system-ca` antes de `npm install`. En Maven, el equivalente es `set MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT`.

**La primera ejecución se queda descargando.** Hace falta internet para bajar Maven, las dependencias y los paquetes de npm. Las siguientes arrancan con lo ya guardado en tu carpeta de usuario (`.m2`).

## Después de instalar

El uso de las pantallas, los roles, la API y el motor de reglas están en `GUIA.md`, en la carpeta `Docs`. Un arranque rápido también está en `README.md`.
