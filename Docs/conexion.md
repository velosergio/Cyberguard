# Conexión a la base de datos

CyberGuard usa **MySQL** (en Laragon o XAMPP suele ser compatible con MariaDB) en el puerto `3306`. El backend lee la configuración desde `backend/.env`.

## Archivos

| Archivo | Rol |
| --- | --- |
| `backend/.env.example` | Plantilla sin secretos; sí va al repositorio |
| `backend/.env` | Valores reales de tu máquina; **no** se versiona |
| `frontend/.env.example` | Plantilla del frontend (`VITE_API_URL`) |
| `frontend/.env` | Opcional en desarrollo (Vite ya reenvía `/api`) |

Copia la plantilla la primera vez:

```bat
cd backend
copy .env.example .env
```

## Variables de la base de datos

| Variable | Por defecto | Qué es |
| --- | --- | --- |
| `DB_HOST` | `localhost` | Host de MySQL/MariaDB |
| `DB_PORT` | `3306` | Puerto |
| `DB_NAME` | `cyberguard` | Nombre de la base |
| `DB_USERNAME` | `root` | Usuario |
| `DB_PASSWORD` | *(vacío)* | Contraseña (vacía en Laragon/XAMPP por defecto) |

Otras variables en el mismo `.env`:

| Variable | Por defecto | Qué es |
| --- | --- | --- |
| `CYBERGUARD_JWT_SECRET` | clave de desarrollo | Firma de los JWT (≥ 32 caracteres en despliegue) |
| `CYBERGUARD_CORS_ORIGENES` | `http://localhost:5173` | Orígenes del frontend |
| `SERVER_PORT` | `8080` | Puerto de la API |

`application.properties` arma la URL JDBC con esas variables y crea la base si no existe (`createDatabaseIfNotExist=true`).

## Laragon

1. Abre Laragon y asegúrate de que **MySQL** esté en marcha.
2. Usuario típico: `root` sin contraseña.
3. Arranca el backend: `cd backend` → `.\mvnw.cmd spring-boot:run`.
4. Hibernate crea/actualiza las tablas; `DataInitializer` carga usuarios, reglas y datos de demo si las tablas están vacías.

Si cambias nombres de columnas (por ejemplo los `id_*`) y `ddl-auto=update` deja columnas viejas, recrea la base:

```bat
mysql -u root -e "DROP DATABASE IF EXISTS cyberguard; CREATE DATABASE cyberguard CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Luego vuelve a arrancar el backend.

## Comprobar la conexión

Con MySQL en marcha:

```bat
mysql -u root -e "SHOW DATABASES LIKE 'cyberguard';"
```

Con la API arriba:

```bat
curl http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"usuario\":\"admin\",\"contrasena\":\"Admin123*\"}"
```

Si responde un token JWT, la base y la conexión están bien.
