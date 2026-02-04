# Enriquecimiento del Backend de Trenes

## Resumen de Mejoras Implementadas

Se ha enriquecido el backend del sistema de trenes con las siguientes funcionalidades:

### 1. Nuevos Tipos de Trenes y Configuración de Vagones

#### Trenes Creados:
- **Tren de Pasajeros (EXP-001)**: 8 vagones incluyendo restaurante y cafetería
- **Tren de Mercancías (MER-001)**: 8 vagones especializados en carga
- **Tren Regional (REG-001)**: 8 vagones con servicios regionales
- **Tren de Alta Velocidad (AVE-001)**: 10 vagones con servicios premium

#### Configuración de Vagones:
- **Mínimo 8 vagones** por tren (excepto AVE con 10)
- **Vagones restaurante** en trenes de pasajeros y alta velocidad
- **Tipos especializados**: primera clase, turista, restaurante, cafetería, carga cerrada/abierta, cisterna

### 2. Gestión de Infraestructura

#### Desvíos Implementados:
- **DSV-001**: Conexión entre vías principales en Estación Norte
- **DSV-002**: Acceso a taller de mantenimiento
- **DSV-003**: Bypass para trenes de mercancías

#### Vías Muertas Creadas:
- **VMT-001**: Almacenamiento temporal
- **VMT-002**: Área de desguace
- **VMT-003**: Estacionamiento nocturno

#### Señales Asociadas:
- Sistema de señalización completo para desvíos y vías muertas
- Estados operativos y de control

### 3. Sistema de Rutas Ampliado

#### Nuevas Rutas:
1. **EXP-MAD-BCN-001**: Expreso Madrid-Barcelona (621 km, alta velocidad)
2. **REG-VAL-ALI-001**: Regional Valencia-Alicante (185 km)
3. **CAR-BIL-ZAR-001**: Mercancías Bilbao-Zaragoza (300 km)
4. **TUR-SAN-COR-001**: Turística Santiago-A Coruña (75 km)
5. **CER-MAD-TOL-001**: Cercanías Madrid-Toledo (74 km)

#### Características de Rutas:
- Frecuencias configuradas por tipo de ruta
- Paradas intermedias con tiempos definidos
- Restricciones específicas (velocidad, peso, tipo de tren)
- Tarifas base y zonas de cobertura

### 4. Gestión de Horarios Automatizada

#### Funcionalidades:
- **Generación automática** de horarios basada en frecuencias de rutas
- **Asignación inteligente** de trenes según tipo de ruta
- **Creación de paradas** con tiempos programados
- **Clases de servicio** según configuración de vagones
- **Estados de horario**: Programado, En Marcha, Retrasado, Cancelado, Completado

## Endpoints API

### Trenes y Datos
```
POST /api/v1/trenes-data/crear-trenes-ejemplo
```
Crea los trenes de ejemplo con configuración completa.

### Infraestructura
```
POST /api/v1/infraestructura/crear-infraestructura-ejemplo
GET  /api/v1/infraestructura/desvios
GET  /api/v1/infraestructura/vias-muertas
PATCH /api/v1/infraestructura/desvios/{viaId}/estado
POST /api/v1/infraestructura/vias-muertas/{viaMuertaId}/asignar-tren
```

### Rutas
```
POST /api/v1/rutas-data/crear-rutas-ejemplo
GET  /api/v1/rutas-data/tipo/{tipoRuta}
GET  /api/v1/rutas-data/estacion/{estacionId}
```

### Gestión de Horarios
```
POST /api/v1/gestion-horarios/crear-horarios-programados
GET  /api/v1/gestion-horarios/ruta/{rutaId}
GET  /api/v1/gestion-horarios/tren/{trenId}
GET  /api/v1/gestion-horarios/estado/{estado}
PATCH /api/v1/gestion-horarios/{horarioId}/estado
```

## Arquitectura Implementada

### Servicios Creados:
- **TrenDataService**: Gestión de trenes de ejemplo
- **InfraestructuraService**: Manejo de desvíos y vías muertas
- **RutaDataService**: Creación de rutas variadas
- **GestionHorariosService**: Sistema automatizado de horarios

### Controladores Nuevos:
- **TrenDataController**: Endpoints para datos de trenes
- **InfraestructuraController**: Gestión de infraestructura
- **RutaDataController**: Manejo de rutas
- **GestionHorariosController**: Control de horarios

## Características Técnicas

### Modelos Enriquecidos:
- **Tren**: Configuración completa de locomotoras y vagones
- **Via**: Tipos DESVIO y VIA_MUERTA implementados
- **Ruta**: Frecuencias, restricciones y paradas detalladas
- **Horario**: Sistema completo de programación y estados
- **Signal**: Integración con infraestructura

### Lógica de Negocio:
- Asignación automática de trenes según tipo de ruta
- Generación de horarios basada en frecuencias
- Control de estados de trenes y horarios
- Gestión de infraestructura ferroviaria

## Próximos Pasos Sugeridos

1. **Implementar frontend** para visualizar la nueva infraestructura
2. **Añadir validaciones** para asignación de trenes a rutas
3. **Crear dashboard** de control de horarios en tiempo real
4. **Implementar notificaciones** para cambios de estado
5. **Añadir reporting** y estadísticas avanzadas

## Uso

Para inicializar el sistema con todos los datos de ejemplo:

```bash
# 1. Crear trenes
curl -X POST http://localhost:8080/api/v1/trenes-data/crear-trenes-ejemplo

# 2. Crear infraestructura
curl -X POST http://localhost:8080/api/v1/infraestructura/crear-infraestructura-ejemplo

# 3. Crear rutas
curl -X POST http://localhost:8080/api/v1/rutas-data/crear-rutas-ejemplo

# 4. Generar horarios
curl -X POST http://localhost:8080/api/v1/gestion-horarios/crear-horarios-programados
```

El sistema quedará completamente enriquecido con trenes variados, infraestructura completa, rutas diversificadas y horarios automatizados.
