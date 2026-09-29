# Instalación de CyberGuard

CyberGuard es una aplicación web de Spring Boot. Se ejecuta con su propio servidor en el puerto 8080. No hace falta publicarla en Apache: la carpeta puede estar dentro de `htdocs`, pero Apache no la sirve.

La primera vez que arranca, si MySQL está en marcha, crea la base `cyberguard`, las tablas y los datos de demostración (usuarios, reglas, dispositivos y eventos).

## Qué necesitas

| Requisito | Versión | Para qué |
| --- | --- | --- |
| JDK | 21 o superior | Compilar y ejecutar la aplicación |
| XAMPP | MySQL o MariaDB en marcha | Base de datos local |
| Navegador | Cualquiera reciente | Abrir el panel |
| Internet | Solo la primera ejecución | El wrapper descarga Maven 3.9.11 y las dependencias |

No instales Maven aparte. El proyecto incluye `mvnw.cmd`, que lo descarga solo.

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

Abre una terminal en la carpeta del proyecto, la que contiene `mvnw.cmd` y `pom.xml`. En este equipo suele ser:

```bat
cd c:\xampp\htdocs\mangomorado\Cyberguard
```

## 2. Encender MySQL

1. Abre el panel de control de XAMPP.
2. Pulsa **Start** en **MySQL**.
3. Espera a que el módulo quede en verde.

La instalación local de XAMPP usa el usuario `root` con contraseña vacía, en `localhost` y el puerto `3306`. Esos son los valores de `src/main/resources/application.properties`. La URL incluye `createDatabaseIfNotExist=true`, así que no hace falta crear la base a mano.

Si `root` tiene contraseña, edita esa línea y deja el resto igual:

```properties
spring.datasource.password=tu_contrasena
```

No subas contraseñas reales al repositorio. En este proyecto el valor vacío es el de una instalación local de XAMPP.

## 3. Arrancar la aplicación

En la carpeta del proyecto:

```bat
mvnw.cmd spring-boot:run
```

La primera vez tarda más: descarga Maven y las librerías. Cuando esté lista verás una línea parecida a esta:

```text
Started CyberguardApplication
```

Deja esa terminal abierta. Cerrarla detiene el programa.

Abre http://localhost:8080

## 4. Entrar

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `Admin123*` |
| Analista | `analista` | `Analista123*` |
| Consulta | `consulta` | `Consulta123*` |

El administrador puede crear usuarios y editar reglas. El analista registra dispositivos y eventos, atiende alertas y descarga reportes. Consulta solo mira el panel, los dispositivos, los eventos y las alertas.

Esas cuentas se crean una sola vez, cuando la tabla de usuarios está vacía. Reiniciar la aplicación no las duplica.

## 5. Ejecutar las pruebas

Las pruebas usan una base H2 en memoria. MySQL puede estar apagado.

```bat
mvnw.cmd test
```

Al terminar, Maven indica si todas pasaron. Cubren la clasificación de riesgo, el motor de reglas y el acceso según el rol.

## Arrancar el jar (opcional)

Si prefieres un archivo ejecutable en lugar de `spring-boot:run`:

```bat
mvnw.cmd -DskipTests package
java -jar target\cyberguard-0.0.1-SNAPSHOT.jar
```

Sigue haciendo falta el JDK 21 o superior y MySQL en marcha. La aplicación queda en el mismo puerto 8080.

## Detenerla

En la terminal donde está corriendo, pulsa `Ctrl+C` y confirma si Windows lo pide.

MySQL puede seguir encendido en XAMPP. Los datos quedan en la base `cyberguard`.

## Si algo falla

**`java` no se reconoce o la versión es menor a 21.** Instala un JDK 21 o superior y define `JAVA_HOME` como en la sección de requisitos. Un error `UnsupportedClassVersionError` o una mención al bytecode 65 significa lo mismo: Java es demasiado antiguo.

**No conecta con MySQL.** El mensaje suele hablar de `Communications link failure` o de que se rechazó la conexión en el puerto 3306. Enciende MySQL en XAMPP y vuelve a lanzar `mvnw.cmd spring-boot:run`.

**Acceso denegado para `root`.** La contraseña de MySQL no está vacía. Escríbela en `spring.datasource.password`.

**El puerto 8080 ya está en uso.** Otra copia de CyberGuard, u otro programa, ocupa ese puerto. Cierra la anterior o cambia `server.port` en `application.properties`.

**La página no carga.** Confirma que la terminal sigue en `Started CyberguardApplication` y que entras por http://localhost:8080, no por el puerto 80 de Apache.

**La primera ejecución se queda descargando.** Hace falta internet para bajar Maven y las dependencias. Las siguientes arrancan con lo ya guardado en tu carpeta de usuario (`.m2`).

## Después de instalar

El uso de las pantallas, los roles y el motor de reglas están en `GUIA.md`, en la raíz del proyecto. Un arranque rápido también está en `README.md`.
