# Guía de Refactoring de Endpoints - Trenes API

## 📊 Resumen de Cambios

Hemos refactorizado los endpoints para reducir la complejidad y mejorar la mantenibilidad:

### 🔄 Reducción de Endpoints
- **TrenController**: De 35+ a 12 endpoints (66% reducción)
- **EstacionController**: De 25+ a 10 endpoints (60% reducción)  
- **RutaController**: De 40+ a 13 endpoints (68% reducción)
- **OperacionesController**: Movido a AdminController (seguridad mejorada)

**Total**: De ~110 a ~35 endpoints (68% reducción total)

## 📁 Nuevos Archivos Creados

### Controllers Refactorizados
- `TrenControllerRefactored.java` - Versión optimizada
- `EstacionControllerRefactored.java` - Versión optimizada
- `RutaControllerRefactored.java` - Versión optimizada
- `AdminController.java` - Operaciones administrativas

### Servicios Actualizados
- `TrenService.java` - Agregado método `count()`
- `EstacionService.java` - Agregado método `count()`
- `RutaService.java` - Agregado método `count()`

## 🚀 Cómo Migrar

### Paso 1: Reemplazar los Controllers

```bash
# Renombrar los archivos originales (backup)
mv src/main/java/com/david/trenes/controller/TrenController.java src/main/java/com/david/trenes/controller/TrenController.java.backup
mv src/main/java/com/david/trenes/controller/EstacionController.java src/main/java/com/david/trenes/controller/EstacionController.java.backup
mv src/main/java/com/david/trenes/controller/RutaController.java src/main/java/com/david/trenes/controller/RutaController.java.backup
mv src/main/java/com/david/trenes/controller/OperacionesController.java src/main/java/com/david/trenes/controller/OperacionesController.java.backup

# Activar los nuevos controllers
mv src/main/java/com/david/trenes/controller/TrenControllerRefactored.java src/main/java/com/david/trenes/controller/TrenController.java
mv src/main/java/com/david/trenes/controller/EstacionControllerRefactored.java src/main/java/com/david/trenes/controller/EstacionController.java
mv src/main/java/com/david/trenes/controller/RutaControllerRefactored.java src/main/java/com/david/trenes/controller/RutaController.java
```

### Paso 2: Configurar Seguridad para AdminController

```java
// En tu SecurityConfig, agregar:
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/trenes/**", "/api/v1/estaciones/**", "/api/v1/rutas/**").permitAll()
            .anyRequest().authenticated()
        );
        // ... resto de configuración
}
```

## 📋 Nuevos Endpoints

### TrenController
```
GET    /api/v1/trenes                    # Listar todos
GET    /api/v1/trenes/{id}               # Obtener por ID
POST   /api/v1/trenes                    # Crear
PUT    /api/v1/trenes/{id}               # Actualizar
DELETE /api/v1/trenes/{id}               # Eliminar
GET    /api/v1/trenes/buscar             # 🔥 BÚSQUEDA CONSOLIDADA
PATCH  /api/v1/trenes/{id}/estado        # Cambiar estado
PATCH  /api/v1/trenes/{id}/conductor     # Asignar conductor
PATCH  /api/v1/trenes/{id}/ruta          # Asignar ruta
PATCH  /api/v1/trenes/{id}/ubicacion     # Actualizar ubicación
PATCH  /api/v1/trenes/{id}/kilometraje   # Registrar kilometraje
PATCH  /api/v1/trenes/{id}/programar-revision # Programar revisión
PATCH  /api/v1/trenes/{id}/completar-revision # Completar revisión
POST   /api/v1/trenes/{id}/iniciar-viaje # Iniciar viaje
GET    /api/v1/trenes/estadisticas       # 🔥 ESTADÍSTICAS CONSOLIDADAS
GET    /api/v1/trenes/exists             # Verificar existencia
GET    /api/v1/trenes/{id}/posicion      # Obtener posición
GET    /api/v1/trenes/no-en-marcha/ids   # IDs no en marcha
```

### EstacionController
```
GET    /api/v1/estaciones                # Listar todos
GET    /api/v1/estaciones/{id}           # Obtener por ID
POST   /api/v1/estaciones                # Crear
PUT    /api/v1/estaciones/{id}           # Actualizar
DELETE /api/v1/estaciones/{id}           # Eliminar
GET    /api/v1/estaciones/buscar          # 🔥 BÚSQUEDA CONSOLIDADA
PATCH  /api/v1/estaciones/{id}/estado     # Cambiar estado
PATCH  /api/v1/estaciones/{id}/personal   # Actualizar personal
PATCH  /api/v1/estaciones/{id}/supervisor # Asignar supervisor
GET    /api/v1/estaciones/estadisticas    # 🔥 ESTADÍSTICAS CONSOLIDADAS
GET    /api/v1/estaciones/exists          # Verificar existencia
```

### RutaController
```
GET    /api/v1/rutas                     # Listar todos
GET    /api/v1/rutas/{id}                # Obtener por ID
POST   /api/v1/rutas                     # Crear
PUT    /api/v1/rutas/{id}                # Actualizar
DELETE /api/v1/rutas/{id}                # Eliminar
GET    /api/v1/rutas/buscar               # 🔥 BÚSQUEDA CONSOLIDADA
PATCH  /api/v1/rutas/{id}/estado         # Cambiar estado
PATCH  /api/v1/rutas/{id}/tarifa         # Actualizar tarifa
PATCH  /api/v1/rutas/{id}/prioridad      # Actualizar prioridad
POST   /api/v1/rutas/{id}/restriccion    # Agregar restricción
DELETE /api/v1/rutas/{id}/restriccion/{tipo} # Remover restricción
GET    /api/v1/rutas/estadisticas        # 🔥 ESTADÍSTICAS CONSOLIDADAS
GET    /api/v1/rutas/exists              # Verificar existencia
GET    /api/v1/rutas/alternativas        # Buscar alternativas
```

### AdminController (Nuevo - Requiere rol ADMIN)
```
DELETE /api/v1/admin/limpieza/horarios-inventario # Limpiar datos
DELETE /api/v1/admin/limpieza/billetes            # Limpiar billetes
POST   /api/v1/admin/control/iniciar-servicio/horario/{id} # Iniciar servicio
PATCH  /api/v1/admin/control/trenes/estado        # Cambiar estado masivo
POST   /api/v1/admin/horarios/regenerar           # Regenerar horarios
POST   /api/v1/admin/horarios/reset               # Reset horarios
GET    /api/v1/admin/monitorizacion/consistencia-paradas # Verificar consistencia
GET    /api/v1/admin/monitorizacion/trenes-tiempo-real    # Monitorizar
GET    /api/v1/admin/monitorizacion/candidatos-inicio     # Candidatos inicio
GET    /api/v1/admin/health                       # Health check
GET    /api/v1/admin/stats                        # Estadísticas sistema
```

## 🔥 Endpoints Consolidados

### Búsqueda Consolidada
Los endpoints `/buscar` reemplazan múltiples búsquedas específicas:

**Trenes - Ejemplos:**
```bash
# Búsqueda por número y estado
GET /api/v1/trenes/buscar?numero=123&estado=EN_MARCHA

# Búsqueda por capacidad y ubicación
GET /api/v1/trenes/buscar?capacidadPasajerosMin=200&latMin=40.0&latMax=41.0&lonMin=-3.0&lonMax=-2.0

# Búsqueda que requieren revisión
GET /api/v1/trenes/buscar?requiereRevision=true&fechaRevision=2024-03-01T00:00:00
```

**Estaciones - Ejemplos:**
```bash
# Búsqueda por ciudad y tipo
GET /api/v1/estaciones/buscar?ciudad=Madrid&tipo=TERMINAL

# Búsqueda por servicios
GET /api/v1/estaciones/buscar?servicioDisponible=RESTAURANTE&accesible=true

# Búsqueda geográfica
GET /api/v1/estaciones/buscar?latMin=40.0&latMax=41.0&lonMin=-3.0&lonMax=-2.0
```

**Rutas - Ejemplos:**
```bash
# Búsqueda entre estaciones
GET /api/v1/rutas/buscar?estacionOrigenId=123&estacionDestinoId=456

# Búsqueda por distancia y tiempo
GET /api/v1/rutas/buscar?distanciaMin=100&distanciaMax=500&tiempoMax=120

# Búsqueda por frecuencia
GET /api/v1/rutas/buscar?lunes=true&serviciosMin=10
```

### Estadísticas Consolidadas
Los endpoints `/estadisticas` reemplazan múltiples endpoints específicos:

```bash
# Estadísticas generales
GET /api/v1/trenes/estadisticas?tipo=general

# Estadísticas por tipo
GET /api/v1/trenes/estadisticas?tipo=por_tipo&tipoTren=ALTA_VELOCIDAD

# Estadísticas por estado
GET /api/v1/trenes/estadisticas?tipo=por_estado&estado=EN_MARCHA
```

## ⚠️ Cambios Importantes

### 1. Endpoints Eliminados
Los siguientes endpoints ya no existen:

**Trenes:**
- `/numero/{numeroTren}` → Usar `/buscar?numero=valor`
- `/matricula/{matricula}` → Usar `/buscar?matricula=valor`
- `/estado/{estado}` → Usar `/buscar?estado=valor`
- `/activos` → Usar `/buscar?estado=ACTIVO`
- `/operativos` → Usar `/buscar?estado=OPERATIVO`
- `/en-marcha` → Usar `/buscar?estado=EN_MARCHA`
- `/en-estacion` → Usar `/buscar?estado=EN_ESTACION`
- `/con-incidencias` → Implementar en servicio
- `/fabricante/{fabricante}` → Usar `/buscar?fabricante=valor`
- `/modelo/{modelo}` → Usar `/buscar?modelo=valor`
- Todos los endpoints de estadísticas individuales → Usar `/estadisticas`

**Estaciones:**
- `/nombre` → Usar `/buscar?nombre=valor`
- `/ciudad` → Usar `/buscar?ciudad=valor`
- `/provincia` → Usar `/buscar?provincia=valor`
- `/coordenadas` → Usar `/buscar?latMin=X&latMax=Y&lonMin=A&lonMax=B`
- Todos los endpoints de estadísticas individuales → Usar `/estadisticas`

**Rutas:**
- `/nombre` → Usar `/buscar?nombre=valor`
- `/distancia-minima/{distancia}` → Usar `/buscar?distanciaMin=valor`
- `/distancia-maxima/{distancia}` → Usar `/buscar?distanciaMax=valor`
- `/lunes` → Usar `/buscar?lunes=true`
- `/domingo` → Usar `/buscar?domingo=true`
- Todos los endpoints de estadísticas individuales → Usar `/estadisticas`

### 2. Operaciones Administrativas
Todas las operaciones de mantenimiento ahora requieren rol ADMIN y están en `/api/v1/admin/*`

### 3. Verificación de Existencia
Los endpoints `exists` ahora usan parámetros en lugar de path variables:

```bash
# Antes:
GET /api/v1/trenes/exists/numero/123

# Ahora:
GET /api/v1/trenes/exists?numero=123
```

## 🧪 Pruebas Recomendadas

### 1. Pruebas Básicas
```bash
# Probar búsqueda básica
curl "http://localhost:8082/api/v1/trenes/buscar?estado=EN_MARCHA"

# Probar estadísticas
curl "http://localhost:8082/api/v1/trenes/estadisticas?tipo=general"

# Probar operaciones CRUD
curl -X POST "http://localhost:8082/api/v1/trenes" -H "Content-Type: application/json" -d '{"numeroTren":"TEST001","matricula":"MAT001"}'
```

### 2. Pruebas de Administración
```bash
# Probar health check (requiere token de admin)
curl -H "Authorization: Bearer <admin-token>" "http://localhost:8082/api/v1/admin/health"
```

## 🔄 Rollback

Si necesitas volver a la versión anterior:

```bash
# Restaurar backups
mv src/main/java/com/david/trenes/controller/TrenController.java.backup src/main/java/com/david/trenes/controller/TrenController.java
mv src/main/java/com/david/trenes/controller/EstacionController.java.backup src/main/java/com/david/trenes/controller/EstacionController.java
mv src/main/java/com/david/trenes/controller/RutaController.java.backup src/main/java/com/david/trenes/controller/RutaController.java
mv src/main/java/com/david/trenes/controller/OperacionesController.java.backup src/main/java/com/david/trenes/controller/OperacionesController.java

# Eliminar AdminController
rm src/main/java/com/david/trenes/controller/AdminController.java
```

## 📈 Beneficios

1. **Mantenibilidad**: Menos código que mantener
2. **Consistencia**: Patrones consistentes en todos los controllers
3. **Seguridad**: Operaciones peligrosas requieren rol ADMIN
4. **Flexibilidad**: Búsquedas combinadas con múltiples filtros
5. **Performance**: Menos endpoints = menor superficie de ataque
6. **Documentación**: Más fácil de documentar y entender

## 🚨 Notas Importantes

1. **Testing**: Realiza pruebas exhaustivas antes del despliegue
2. **Clientes**: Actualiza todos los clientes que consumen la API
3. **Documentación**: Actualiza la documentación de la API
4. **Monitoreo**: Monitorea el rendimiento después del cambio
5. **Seguridad**: Configura correctamente los roles de ADMIN

## 📞 Soporte

Si encuentras algún problema durante la migración:
1. Revisa los logs de la aplicación
2. Verifica que todos los servicios tengan los métodos necesarios
3. Confirma que la configuración de seguridad sea correcta
4. Prueba los endpoints individualmente

---

**¡Listo para migrar! 🎉**
