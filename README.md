# Sistema de Gestión - Imprenta Diges'k

Sistema de información desarrollado para el Proyecto Final del curso
Análisis y Diseño de Sistemas de Información (UTP, Sección 18176).

Integrantes: Miguel Angel Suarez Perdomo, Maricielo Jimena Huancas Guerrero, David Adrian Socola Cisneros

## Estructura del proyecto

El sistema está separado en dos partes independientes: backend (API REST) e interfaces (frontend web).

digesk-sistema/
├── backend/
│   ├── pom.xml
│   ├── database/
│   │   └── digesk_schema.sql
│   └── src/
│       ├── main/
│       │   ├── java/com/digesk/backend/
│       │   │   ├── BackendApplication.java
│       │   │   ├── config/
│       │   │   │   └── CorsConfig.java
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   ├── entity/
│       │   │   ├── repository/
│       │   │   └── service/
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/digesk/backend/
│               └── BackendApplicationTests.java
│
├── interfaces/
│   ├── index.html
│   ├── pages/
│   │   ├── pedido/
│   │   ├── cliente/
│   │   ├── cotizacion/
│   │   ├── cobranza/
│   │   └── reporte/
│   ├── js/
│   └── css/
│
├── estructura.py
└── README.md

## backend

Contiene la lógica de negocio y el acceso a datos. Expone endpoints REST que consume `interfaces`.

**pom.xml**
Define las dependencias del proyecto: Spring Boot, Spring Data JPA, MySQL Driver, CORS.

**database/digesk_schema.sql**
Script SQL con la creación de tablas: `tb_pedido`, `tb_cliente`, `tb_cotizacion`, `tb_comprobante`, `tb_usuario`, `tb_producto`. Corresponde al diagrama físico del Avance 2.

**src/main/resources/application.properties**
Configuración de conexión a MySQL (DigeskDB) y puerto del servidor.

**src/main/java/com/digesk/backend/controller** (8 clases)
`ClienteController`, `ComprobanteController`, `CotizacionController`, `EntregaCobroController`, `PedidoController`, `ProductoController`, `ReporteController`, `UsuarioController`. Reciben las peticiones HTTP y equivalen a las clases Control del diagrama de análisis (`ControlPedido`, `ControlCotizacion`, `ControlCobranza`, `ControlAutenticacion`). No hay una separación 1 a 1 exacta con esos 4 nombres del diagrama; cada módulo del sistema tiene su propio controller.

**src/main/java/com/digesk/backend/entity** (6 clases)
`Cliente`, `Comprobante`, `Cotizacion`, `Pedido`, `Producto`, `Usuario`. Mapeadas con JPA, representan las tablas de la base de datos.

**src/main/java/com/digesk/backend/repository** (6 interfaces)
`ClienteRepository`, `ComprobanteRepository`, `CotizacionRepository`, `PedidoRepository`, `ProductoRepository`, `UsuarioRepository`. Interfaces de Spring Data JPA que ejecutan las operaciones CRUD contra MySQL. **Cumplen el rol del patrón DAO** (`DAOPedido`, `DAOCliente`, etc. en el informe), aunque en el código llevan el sufijo `Repository` por ser la convención de Spring Data JPA, no `DAOxxx` literal.

**src/main/java/com/digesk/backend/service** (7 clases)
`ClienteService`, `ComprobanteService`, `CotizacionService`, `EntregaCobroService`, `PedidoService`, `ProductoService`, `UsuarioService`. Lógica de negocio interna: cálculo de cotizaciones, validaciones de pedido, registro de entrega y cobro, listado de deudas.

**src/main/java/com/digesk/backend/dto** (10 clases)
`ClienteDTO`, `ClienteFrecuenteDTO`, `ComprobanteDTO`, `CotizacionDTO`, `EntregaCobroDTO`, `PedidoDTO`, `ProductoDTO`, `ReporteDTO`, `ResultadoEntregaDTO`, `UsuarioDTO`. Definen qué datos viajan en formato JSON entre el backend y las interfaces.

**src/main/java/com/digesk/backend/config**
Solo contiene `CorsConfig.java`, que habilita CORS para que `interfaces` pueda consumir la API desde otro origen/puerto. **No hay configuración de seguridad ni autenticación por rol implementada todavía** (ver sección "Pendientes").

**src/test/java/com/digesk/backend**
Contiene `BackendApplicationTests.java`, la prueba por defecto que genera Spring Initializr. No hay pruebas unitarias específicas de los servicios o controllers todavía.

## interfaces

Contiene únicamente las vistas y su comunicación con el backend. No tiene lógica de negocio.

**index.html**
Pantalla principal / dashboard del sistema. **No implementa un formulario de inicio de sesión ni valida usuario/contraseña**; actualmente es el punto de entrada directo a los módulos (ver "Pendientes").

**pages/pedido** — Formulario y listado de pedidos, equivale a `InterfazPedido`.
**pages/cliente** — Formulario de registro y búsqueda de clientes, equivale a `InterfazCliente`.
**pages/cotizacion** — Vista de cotización generada, equivale a `InterfazCotizacion`.
**pages/cobranza** — Registro de entrega y cobro, y listado de deudas pendientes, equivale a `InterfazCobranza`.
**pages/reporte** — Generación de reportes de pedidos e ingresos, equivale a `InterfazReporte`.

**js** (6 archivos)
`clientes.js`, `cobranza.js`, `cotizaciones.js`, `dashboard.js`, `pedidos.js`, `reportes.js`. Cada uno hace `fetch` hacia los endpoints del backend correspondientes a su módulo. No existe un `api.js` centralizado ni un `login.js`; cada archivo define su propia constante `API_BASE` de forma independiente.

**css**
`base.css`, `dashboard.css`, `forms.css`, `module.css`, `styles.css`, `tables.css`. Estilos visuales del sistema.

## Cómo correr el proyecto

1. **Base de datos:** ejecutar `backend/database/digesk_schema.sql` en MySQL para crear `DigeskDB`.
2. **Backend:** abrir la carpeta `backend` en VS Code o IntelliJ, configurar `application.properties` con tus credenciales de MySQL, y ejecutar la clase principal `BackendApplication.java`. Por defecto corre en `http://localhost:8080`.
3. **Interfaces:** abrir `interfaces/index.html` con la extensión Live Server de VS Code. Las páginas consumen la API en `http://localhost:8080/api/...`.

## Patrones de diseño aplicados

**MVC (Modelo Vista Controlador)**
Separación entre Vista (`interfaces`), Controlador (`backend/controller`) y Modelo (`backend/entity`).

**DAO**
Acceso a datos desacoplado en `backend/repository`, implementado con interfaces de Spring Data JPA en lugar de clases DAO manuales.

**Arquitectura por Capas**
Presentación (`interfaces`) → Negocio (`controller` y `service`) → Datos (`repository` y `entity`).

## Pendientes / limitaciones conocidas

Esto refleja honestamente el estado actual del prototipo frente al diseño UML de los Avances 2 y 3, para que quede documentado antes de la sustentación:

- **No hay autenticación real (CU-07 Iniciar Sesión sin implementar).** El sistema no valida usuario/contraseña ni genera sesión/token. `UsuarioController` y `UsuarioService` existen pero, hasta donde se ha revisado, solo cubren operaciones CRUD sobre la tabla `tb_usuario`, no un endpoint de login.
- **No hay diferenciación de roles (Administrador vs. Diseñador).** Como no hay login, tampoco hay control de acceso: cualquier persona que abra `interfaces/index.html` puede entrar a todos los módulos (pedidos, cotizaciones, clientes, cobranza, reportes), incluyendo los que en el diseño están reservados solo al Administrador. Esto es una brecha directa entre el diagrama de casos de uso (que sí especifica roles) y el prototipo actual.
- **`config/` solo tiene CORS**, no hay `SecurityConfig` ni filtro de autorización.
- El repositorio no incluye archivos de configuración de base de datos con credenciales reales (correcto por seguridad), por lo que quien clone el proyecto debe crear su propio `application.properties` local.

### Cómo cerrar la brecha antes de la sustentación (si el tiempo alcanza)

Opción rápida para la demo, sin implementar Spring Security completo:
1. Backend: agregar un endpoint simple `POST /api/usuarios/login` en `UsuarioController` que reciba usuario/contraseña y devuelva el usuario con su `rol` si coincide.
2. Frontend: crear una pantalla de login simple en `index.html` (o una página nueva) que llame a ese endpoint y guarde el rol devuelto (por ejemplo en `sessionStorage`).
3. En `dashboard.js`, ocultar o deshabilitar los enlaces a los módulos que el rol actual no debería ver (ej. si `rol === "Diseñador"`, solo mostrar acceso al módulo de Pedidos para actualizar estado).

Esto no es una autorización segura a nivel de backend (cualquiera podría llamar a la API directamente sin pasar por el frontend), pero sí demuestra visualmente la diferenciación de roles para efectos de la sustentación y deja documentado que la seguridad completa queda como trabajo futuro.