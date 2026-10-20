# API de Solicitudes de Soporte Técnico

API RESTful desarrollada con **Java 17 y Spring Boot 3** que permite registrar, consultar, actualizar y eliminar solicitudes de soporte técnico de una empresa de servicios tecnológicos.

Los datos se almacenan **en memoria** (`ConcurrentHashMap`), sin base de datos: se reinician cada vez que se detiene la aplicación. Al iniciar, se cargan datos de ejemplo (2 clientes, 3 técnicos y 2 solicitudes) para facilitar las pruebas.

## Tecnologías

| Herramienta | Uso |
|---|---|
| Java 17 | Lenguaje |
| Spring Boot 3.5 (Web, Validation) | API REST y validaciones con anotaciones |
| springdoc-openapi (Swagger UI) | Documentación interactiva de endpoints |
| Maven | Gestión de dependencias y compilación |
| JUnit 5 + MockMvc | Pruebas de integración |
| Postman | Pruebas funcionales |

## Requisitos

- **JDK 17 o superior** (`java -version`)
- **Maven 3.9+** (`mvn -v`) — o un IDE con Maven integrado (IntelliJ IDEA, Eclipse/STS, VS Code con Extension Pack for Java)
- **Postman** (para las pruebas funcionales)

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/<usuario>/soporte-tecnico-api.git
cd soporte-tecnico-api

# 2. Compilar y ejecutar las pruebas
mvn clean install

# 3. Iniciar la aplicación
mvn spring-boot:run
```

La API queda disponible en **http://localhost:8080**.

**Desde IntelliJ IDEA:** *File → Open* → seleccionar la carpeta del proyecto → esperar a que Maven descargue las dependencias → ejecutar `SoporteTecnicoApiApplication`.

**Como JAR:**

```bash
mvn clean package
java -jar target/soporte-tecnico-api-1.0.0.jar
```

## Documentación (Swagger)

Con la aplicación en ejecución:

- Swagger UI: **http://localhost:8080/swagger-ui.html**
- Especificación OpenAPI (JSON): http://localhost:8080/v3/api-docs

## Endpoints

### Solicitudes — `/api/solicitudes`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/api/solicitudes` | Listar (filtros opcionales: `estado`, `prioridad`, `clienteId`, `tecnicoId`) | 200 |
| GET | `/api/solicitudes/{id}` | Obtener por id | 200, 404 |
| POST | `/api/solicitudes` | Registrar (inicia en `ABIERTA`) | 201, 400, 404, 409 |
| PUT | `/api/solicitudes/{id}` | Actualizar completa | 200, 400, 404, 409 |
| PATCH | `/api/solicitudes/{id}/estado` | Cambiar estado | 200, 400, 404, 409 |
| PATCH | `/api/solicitudes/{id}/tecnico` | Asignar técnico | 200, 400, 404, 409 |
| DELETE | `/api/solicitudes/{id}` | Eliminar | 204, 404 |

### Clientes — `/api/clientes` y Técnicos — `/api/tecnicos`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/clientes` · `/api/tecnicos?disponible=true` | Listar |
| GET | `/{id}` | Obtener por id |
| POST | `/` | Registrar |
| PUT | `/{id}` | Actualizar |
| DELETE | `/{id}` | Eliminar (409 si tiene solicitudes asociadas) |

### Ejemplo: registrar una solicitud

```http
POST /api/solicitudes
Content-Type: application/json

{
  "titulo": "No hay conexión a internet en el área de ventas",
  "descripcion": "Desde las 9:00 a.m. las 5 PCs del área de ventas no tienen acceso a internet.",
  "prioridad": "ALTA",
  "clienteId": 1
}
```

Respuesta `201 Created`:

```json
{
  "id": 3,
  "titulo": "No hay conexión a internet en el área de ventas",
  "descripcion": "Desde las 9:00 a.m. las 5 PCs del área de ventas no tienen acceso a internet.",
  "prioridad": "ALTA",
  "estado": "ABIERTA",
  "clienteId": 1,
  "tecnicoId": null,
  "fechaCreacion": "2026-10-19T21:15:30.123",
  "fechaActualizacion": "2026-10-19T21:15:30.123"
}
```

### Formato de error

Todos los errores tienen la misma estructura (generada por `GlobalExceptionHandler`):

```json
{
  "timestamp": "2026-10-19T21:16:02.481",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "La solicitud contiene datos inválidos",
  "ruta": "/api/solicitudes",
  "detalles": {
    "titulo": "El título es obligatorio",
    "prioridad": "La prioridad es obligatoria (BAJA, MEDIA, ALTA, CRITICA)"
  }
}
```

## Reglas de negocio

- Toda solicitud nueva inicia en estado `ABIERTA`.
- Transiciones de estado permitidas: `ABIERTA → EN_PROCESO | CERRADA`, `EN_PROCESO → RESUELTA | ABIERTA`, `RESUELTA → CERRADA | EN_PROCESO`. `CERRADA` es estado final.
- Para pasar a `EN_PROCESO` la solicitud debe tener un técnico asignado.
- Solo se pueden asignar técnicos con `disponible = true`.
- Una solicitud `CERRADA` no puede modificarse.
- El email de clientes y técnicos es único.
- No se puede eliminar un cliente o técnico que tenga solicitudes asociadas.

## Datos de ejemplo cargados al iniciar

| Tipo | Id | Datos |
|---|---|---|
| Cliente | 1 | María López — Comercial Andina S.A.C. |
| Cliente | 2 | José Quispe — Ferretería del Sur E.I.R.L. |
| Técnico | 1 | Carlos Ramírez — Redes (disponible) |
| Técnico | 2 | Ana Torres — Hardware (disponible) |
| Técnico | 3 | Luis Mendoza — Software (**no disponible**) |
| Solicitud | 1 | Impresora no imprime (cliente 1, técnico 2) |
| Solicitud | 2 | Correo no sincroniza (cliente 2, sin técnico) |

## Pruebas

**Pruebas automatizadas (JUnit + MockMvc):**

```bash
mvn test
```

**Pruebas funcionales con Postman:**

1. Iniciar la aplicación (`mvn spring-boot:run`).
2. En Postman: *Import* → seleccionar `postman/Soporte-Tecnico-API.postman_collection.json`.
3. Ejecutar la colección completa con *Run collection* (29 peticiones con validaciones automáticas), o cada petición por separado.

> Ejecutar la colección con la aplicación recién iniciada, ya que algunas pruebas usan los datos de ejemplo.

## Estructura del proyecto

```
soporte-tecnico-api/
├── pom.xml
├── README.md
├── postman/
│   └── Soporte-Tecnico-API.postman_collection.json
└── src/
    ├── main/
    │   ├── java/pe/soporte/api/
    │   │   ├── SoporteTecnicoApiApplication.java   # Clase principal
    │   │   ├── config/        # Swagger y carga de datos de ejemplo
    │   │   ├── controller/    # Endpoints REST (capa de presentación)
    │   │   ├── dto/           # Objetos de entrada/salida (requests y ErrorResponse)
    │   │   ├── exception/     # Excepciones propias y @ControllerAdvice
    │   │   ├── model/         # Entidades: Solicitud, Cliente, Tecnico y enums
    │   │   ├── repository/    # Almacenamiento en memoria (Map)
    │   │   └── service/       # Interfaces de servicio (lógica de negocio)
    │   │       └── impl/      # Implementaciones de los servicios
    │   └── resources/
    │       └── application.properties
    └── test/java/pe/soporte/api/
        └── SolicitudApiTest.java
```

Flujo de una petición: `Controller → Service (interfaz) → ServiceImpl → Repository (Map en memoria)`. Las excepciones lanzadas en cualquier capa las captura `GlobalExceptionHandler`.

## Equipo

| Integrante | Rol |
|---|---|
| _Integrante 1_ | Modelado y capa de datos en memoria (model, repository) |
| _Integrante 2_ | Lógica de negocio y manejo de errores (service, exception) |
| _Integrante 3_ | Endpoints, documentación Swagger y pruebas (controller, Postman, tests) |
