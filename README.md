# Trenes

Aplicación backend (Java + Spring Boot) para la gestión del dominio “Trenes”.

## Requisitos
- Java: 24
- Maven: (incluye Maven Wrapper `./mvnw`)
- MongoDB: (local o remoto)

## Cómo ejecutar (desarrollo)

### 1) Clonar el repo


### 2) Configurar MongoDB
Asegúrate de tener MongoDB levantado.

Ejemplo (Mongo local típico):
- Host: `localhost`
- Puerto: `27017`
- Base de datos: `trenes`

### 3) Configurar variables / properties
Configura la conexión a MongoDB y cualquier otra configuración en:
- `src/main/resources/application.properties` o `application.yml`

Ejemplo (orientativo; ajusta a tu proyecto):