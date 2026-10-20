# Gestión de Notas

Aplicación web para **registrar estudiantes** y **consultar sus notas** por materia.

Permite guardar los datos de un estudiante (cédula, nombre y sus materias con su
respectiva nota) y luego buscarlo por cédula para ver sus resultados. La aplicación
está dividida en dos partes: un **backend** (API en Java) que se encarga de los datos
y la lógica, y un **frontend** (interfaz en Vue) con el que interactúa el usuario.

---

## ¿Qué hace?

- **Registrar estudiante:** ingresar nombre, cédula y una o varias materias, cada una con su nota.
- **Consultar estudiante:** buscar por cédula y ver sus materias, notas, el **promedio** y su **clasificación** (APROBADO / REPROBADO).
- **Listar estudiantes:** obtener todos los estudiantes registrados.
- **Persistencia:** los datos quedan guardados en una base de datos MongoDB.
- **Documentación de la API:** incluye Swagger para probar los endpoints desde el navegador.

---

## Estructura del proyecto

```
gestion-notas/
│
├── src/                          # Backend (API en Java con Spring Boot)
│   ├── main/
│   │   ├── java/com/api_restful_practice/gestion_notas/
│   │   │   ├── GestionNotasApplication.java   # Punto de arranque de la aplicación
│   │   │   ├── controller/                    # Recibe las peticiones HTTP (lo que llega del front)
│   │   │   │   └── EstudianteController.java
│   │   │   ├── model/                         # Objetos de datos
│   │   │   │   ├── Estudiante.java            # Cédula, nombre y lista de materias
│   │   │   │   └── Materia.java               # Nombre de la materia y nota
│   │   │   ├── repository/                    # Acceso a la base de datos
│   │   │   │   └── EstudianteRepository.java
│   │   │   └── service/                       # Lógica de negocio (guardar, buscar, promedio)
│   │   │       └── EstudianteService.java
│   │   └── resources/
│   │       └── application.properties         # Configuración (nombre, conexión a MongoDB)
│   └── test/                                  # Pruebas unitarias (JUnit, Mockito, MockMvc)
│
├── frontend/                     # Frontend (interfaz en Vue)
│   ├── src/
│   │   ├── main.js               # Punto de arranque del front
│   │   ├── App.vue               # Componente raíz (título + formulario)
│   │   ├── components/
│   │   │   └── FormularioEstudiante.vue   # Formulario de registro y búsqueda
│   │   └── services/
│   │       └── api.js            # Conexión con el backend (axios)
│   ├── package.json              # Dependencias del frontend
│   └── vite.config.js            # Configuración del servidor de desarrollo
│
├── Dockerfile                    # Para empaquetar el backend en un contenedor
├── docker-compose.yml            # Levanta backend + base de datos juntos
├── pom.xml                       # Dependencias del backend (Maven)
├── mvnw / mvnw.cmd               # Wrapper de Maven (no requiere instalar Maven)
└── README.md                     # Este archivo
```

---

## Tecnologías

| Parte | Tecnología |
| --- | --- |
| Backend | Java 17, Spring Boot, Spring Data MongoDB |
| Hipermedia API | Spring HATEOAS |
| Base de datos | MongoDB |
| Documentación API | SpringDoc OpenAPI (Swagger) |
| Frontend | Vue 3, Vite, Bootstrap, Axios |
| Empaquetado | Docker / Docker Compose |
| Build backend | Maven |

---

## Modelo de datos

Un **estudiante** se guarda en MongoDB con esta forma:

```
Estudiante
├── id            (generado automáticamente)
├── cedula        (texto)
├── nombre        (texto)
├── materias      (lista de Materia)
│       ├── nombre  (texto)
│       └── nota    (número decimal)
├── promedio      (calculado a partir de las materias — NO se guarda en la base de datos)
└── clasificacion (APROBADO / REPROBADO según el promedio — NO se guarda)
```

> **El promedio y la clasificación son datos derivados.** Se calculan al momento de
> guardar, consultar o listar, a partir de las `materias`, y **no** se persisten en MongoDB.
> Así se evita que queden desactualizados si las materias cambian.

Ejemplo en JSON:

```json
{
  "cedula": "123456",
  "nombre": "Diego",
  "materias": [
    { "nombre": "Bases de Datos", "nota": 4.2 },
    { "nombre": "Arquitectura de Software", "nota": 3.8 }
  ]
}
```

---

## ¿Cómo se conectan el backend y el frontend?

Aunque son dos proyectos separados, se comunican por HTTP:

1. **El frontend** corre en `http://localhost:5173` (el servidor de desarrollo de Vite).
2. **El backend** corre en `http://localhost:8080` y expone la API bajo `/api/v1`.
3. El archivo `frontend/src/services/api.js` crea un cliente de **Axios** (la librería
   que hace peticiones HTTP) apuntando al backend:

   ```js
   export default axios.create({
     baseURL: 'http://localhost:8080/api/v1',
   })
   ```

4. Cuando el usuario pulsa un botón, el componente `FormularioEstudiante.vue` llama a:

   - **Guardar:** `api.post('/estudiantes', estudiante)` → petición `POST` al backend.
   - **Buscar:** `api.get('/estudiantes/{cedula}')` → petición `GET` al backend.

5. El backend recibe la petición en `EstudianteController`, la pasa al `EstudianteService`,
   que usa el `EstudianteRepository` para leer/escribir en MongoDB, y devuelve la respuesta
   al frontend, que la muestra en pantalla.

### Esquema del flujo

```
  Usuario
     │  (interactúa con la pantalla)
     ▼
  Frontend (Vue)  ──  http://localhost:5173
     │
     │  petición HTTP (axios) hacia http://localhost:8080/api/v1/estudiantes
     ▼
  Backend (Spring Boot)  ──  http://localhost:8080
     │   Controller → Service → Repository
     ▼
  MongoDB  ──  base de datos "gestion-notas-db"
```

> **¿Por qué a veces falla la conexión?** Como frontend (`5173`) y backend (`8080`) son
> direcciones distintas, el navegador aplica una regla de seguridad llamada **CORS**.
> Por eso en el backend el controlador está anotado con
> `@CrossOrigin(origins = "http://localhost:5173")`, que autoriza al frontend a
> comunicarse con la API.

---

## Cómo ejecutarlo

### Requisitos

- **Java 17** o superior (para el backend).
- **MongoDB** corriendo en `localhost:27017` (la base de datos).
- **Node.js** y **pnpm** (para el frontend).

### 1. Backend

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

Queda disponible en `http://localhost:8080`.

> La configuración de la base de datos está en
> `src/main/resources/application.properties`:
> ```
> spring.application.name=gestion-notas
> spring.mongodb.uri=mongodb://localhost:27017/gestion-notas-db
> ```

### 2. Frontend

En otra terminal:

```bash
cd frontend
pnpm install
pnpm dev
```

Queda disponible en `http://localhost:5173`.

#### Scripts disponibles (frontend)

Desde la carpeta `frontend/`, además de `pnpm dev` (servidor de desarrollo con recarga
automática), el `package.json` define otros comandos:

| Comando | Qué hace |
| --- | --- |
| `pnpm dev` | Levanta el servidor de desarrollo en `http://localhost:5173`. |
| `pnpm build` | Genera la versión de producción (optimizada) en `dist/`. |
| `pnpm preview` | Sirve localmente la versión ya compilada para revisarla antes de desplegar. |
| `pnpm lint` | Revisa y corrige el estilo del código (oxlint + eslint). |
| `pnpm format` | Formatea el código automáticamente con Prettier. |

### Alternativa: todo con Docker

Se levanta backend y base de datos juntos con Docker Compose (desde la raíz del proyecto):

```bash
docker compose up --build
```

Esto crea dos contenedores: la base de datos (`mongo`) y la aplicación (`app`),
conectados entre sí. En este modo el frontend se levanta igual que antes (`pnpm dev`).

---

## Configuración

### Backend (`application.properties`)

La configuración del backend vive en `src/main/resources/application.properties`:

```properties
spring.application.name=gestion-notas
spring.mongodb.uri=mongodb://localhost:27017/gestion-notas-db
```

- `spring.application.name`: nombre de la aplicación.
- `spring.mongodb.uri`: dirección de la base de datos MongoDB. Lo que va después
  de la barra (`gestion-notas-db`) es la base de datos donde se guardan los estudiantes.

### Variables de entorno (Docker)

Al usar Docker Compose, el `docker-compose.yml` sobreescribe esa configuración mediante
variables de entorno, para que la aplicación apunte al contenedor de MongoDB en lugar de
a `localhost`:

| Variable | Valor | Para qué sirve |
| --- | --- | --- |
| `SPRING_MONGODB_URI` (servicio `app`) | `mongodb://mongo:27017/gestion-notas-db` | Dirección de MongoDB dentro de la red de Docker (`mongo` es el nombre del servicio/contenedor). |
| `MONGO_INITDB_DATABASE` (servicio `mongo`) | `gestion-notas-db` | Nombre de la base de datos que crea MongoDB al iniciar. |

> Las variables de entorno de Spring siguen una convención: el nombre se escribe en
> MAYÚSCULAS y los puntos del `application.properties` se reemplazan por guiones bajos.
> Así, `spring.mongodb.uri` se puede sobreescribir con `SPRING_MONGODB_URI`.

---

## Tests

El proyecto tiene **pruebas unitarias** en `src/test/java/...`, además del *smoke test*
que genera Spring Initializr:

| Archivo | Tipo | Qué cubre |
| --- | --- | --- |
| `GestionNotasApplicationTests` | *Smoke test* (`@SpringBootTest`) | Que la aplicación arranca |
| `EstudianteServiceTest` | Unitario (**Mockito**) | Lógica de `EstudianteService` |
| `EstudianteControllerTest` | Web (**`@WebMvcTest` + MockMvc**) | Endpoints HTTP |

### ¿Qué es un "smoke test"?

Es una **prueba de humo** (en inglés *smoke test*): una verificación rápida y superficial
para comprobar que **lo esencial funciona**. El nombre viene de la electrónica: se enchufa
un aparato y, si no sale humo, "al menos enciende". En software, un smoke test valida que
el sistema **arranca sin errores**, no que cada funcionalidad sea correcta.

`GestionNotasApplicationTests` usa `@SpringBootTest`, que levanta la aplicación completa
(inicia Spring, crea los *beans* y prepara la conexión con MongoDB). Su único método,
`contextLoads()`, no ejecuta ninguna acción: solo comprueba que el **contexto carga
correctamente**. Si falta una dependencia o la configuración es inválida, este test falla.

### Pruebas del service (Mockito)

`EstudianteServiceTest` prueba la **lógica de negocio sin base de datos**: se simula
(*mock*) el `EstudianteRepository` para no depender de MongoDB. Cubre:

- el cálculo del promedio (lista nula, vacía, un valor, varios valores y el redondeo),
- buscar por cédula cuando existe y cuando **no** (lanza excepción),
- el guardado de un estudiante.

### Pruebas del controller (MockMvc)

`EstudianteControllerTest` prueba los **endpoints HTTP** sin levantar todo el servidor:
simula el `EstudianteService` y usa `MockMvc` para lanzar peticiones. Cubre:

- `GET /api/v1/estudiantes/{cedula}` → `200` con `promedio` y los enlaces HATEOAS,
- `POST /api/v1/estudiantes` → `200` con el estudiante guardado.

### Cómo ejecutarlos

```bash
./mvnw test                    # todas las pruebas
./mvnw test -Dtest=EstudianteServiceTest      # solo el service
./mvnw test -Dtest=EstudianteControllerTest   # solo el controller
```

> Las pruebas del service y del controller **no** necesitan MongoDB (usan mocks). El
> smoke test (`contextLoads`) sí arranca el contexto completo, por lo que se recomienda
> tener MongoDB corriendo antes de ejecutarlo.

---

## API de la aplicación

| Método | Endpoint | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/estudiantes` | Registrar un estudiante |
| `GET` | `/api/v1/estudiantes` | Listar todos los estudiantes |
| `GET` | `/api/v1/estudiantes/{cedula}` | Consultar un estudiante por su cédula |

Documentación interactiva (Swagger): `http://localhost:8080/swagger-ui.html`

También se puede probar desde la terminal:

```bash
# Registrar
curl -X POST http://localhost:8080/api/v1/estudiantes \
  -H "Content-Type: application/json" \
  -d '{"cedula":"123456","nombre":"Diego","materias":[{"nombre":"Bases de Datos","nota":4.2}]}'

# Consultar
curl http://localhost:8080/api/v1/estudiantes/123456

# Listar todos
curl http://localhost:8080/api/v1/estudiantes
```

---

## HATEOAS

La **consulta de un estudiante** no devuelve solo los datos: además incluye **enlaces
(links)** que indican qué otras acciones se pueden hacer con ese recurso. Esto es lo
que hace **HATEOAS** (Hypermedia As The Engine Of Application State), una característica
de las APIs REST que hace que las respuestas sean "navegables".

En el backend, el endpoint `GET /api/v1/estudiantes/{cedula}` devuelve un `EntityModel`
(la clase de Spring HATEOAS) al que se le agregan dos enlaces:

- **`self`** → el enlace al propio estudiante que se consultó.
- **`crear`** → el enlace a la acción de registrar un estudiante.

Así, la respuesta JSON de una consulta se ve parecida a esto:

```json
{
  "cedula": "123456",
  "nombre": "Diego",
  "materias": [
    { "nombre": "Bases de Datos", "nota": 4.2 }
  ],
  "promedio": 4.2,
  "clasificacion": "APROBADO",
  "_links": {
    "self":  { "href": "http://localhost:8080/api/v1/estudiantes/123456" },
    "crear": { "href": "http://localhost:8080/api/v1/estudiantes" }
  }
}
```

> Los datos del estudiante (`cedula`, `nombre`, `materias`) aparecen juntos con el objeto
> especial `_links`. El prefijo guion bajo (`_`) distingue los enlaces de los campos propios
> del estudiante, evitando que se mezclen.

Esto da la ventaja de que quien consume la API (como el frontend) puede **descubrir las
acciones disponibles** leyendo la propia respuesta, en lugar de tener las URLs
"escritas a mano".

---

## Resumen

**Gestión de Notas** es una pequeña aplicación full-stack: un backend en Spring Boot
que guarda estudiantes y sus notas en MongoDB, y un frontend en Vue que ofrece el
formulario para registrarlos y consultarlos. Ambos se comunican vía HTTP, y todo el
conjunto puede ejecutarse de forma local o mediante Docker.
