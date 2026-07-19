# Sistema de Gestion - Imprenta Diges'k

Sistema de informacion desarrollado para el Proyecto Final del curso
Analisis y Diseño de Sistemas de Informacion (UTP, Seccion 18176).

Integrantes: Miguel Angel Suarez Perdomo, Maricielo Jimena Huancas Guerrero, David Adrian Socola Cisneros

## Estructura del proyecto

El sistema esta separado en dos partes independientes: backend (API REST) e interfaces (frontend web).

Carpetas principales:

digesk-sistema/
├── backend/
│   ├── pom.xml
│   ├── database/
│   │   └── digesk_schema.sql
│   └── src/
│       ├── main/
│       │   ├── java/com/digesk/backend/
│       │   │   ├── controller/
│       │   │   ├── entity/
│       │   │   ├── repository/
│       │   │   ├── service/
│       │   │   ├── dto/
│       │   │   └── config/
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/digesk/backend/
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
└── README.md

## backend

Contiene la logica de negocio y el acceso a datos. Expone endpoints REST que consume interfaces.

pom.xml
Define las dependencias del proyecto: Spring Boot, Spring Data JPA, MySQL Driver, CORS.

database/digesk_schema.sql
Script SQL con la creacion de tablas: tb_pedido, tb_cliente, tb_cotizacion, tb_comprobante, tb_usuario, tb_producto. Corresponde al diagrama fisico del Avance 2.

src/main/resources/application.properties
Configuracion de conexion a MySQL (DigeskDB) y puerto del servidor.

src/main/java/com/digesk/backend/controller
Recibe las peticiones HTTP y coordina el flujo de cada caso de uso. Equivale a las clases Control del diagrama: ControlPedido, ControlCotizacion, ControlCobranza, ControlAutenticacion.

src/main/java/com/digesk/backend/entity
Clases que representan las tablas de la base de datos: Pedido, Cliente, Cotizacion, Comprobante, Producto, Usuario. Mapeadas con JPA.

src/main/java/com/digesk/backend/repository
Interfaces DAO que ejecutan las operaciones CRUD contra MySQL: DAOPedido, DAOCliente, DAOCotizacion, DAOComprobante, DAOUsuario.

src/main/java/com/digesk/backend/service
Logica de negocio interna: calculo de cotizaciones, validaciones de pedido, coordinacion entre modulos. Equivale a GestorPedido, ValidadorPedido, CoordinadorCotiza del diagrama de estructura compuesta.

src/main/java/com/digesk/backend/dto
Objetos que definen que datos viajan en formato JSON entre el backend y las interfaces.

src/main/java/com/digesk/backend/config
Configuracion de CORS (para permitir que interfaces consuma la API) y de seguridad o autenticacion por rol: Administrador, Diseñador.

src/test/java/com/digesk/backend
Pruebas unitarias del backend.

## interfaces

Contiene unicamente las vistas y su comunicacion con el backend. No tiene logica de negocio.

index.html
Pantalla de inicio de sesion, equivale a InterfazLogin.

pages/pedido
Formulario y listado de pedidos, equivale a InterfazPedido.

pages/cliente
Formulario de registro y busqueda de clientes, equivale a InterfazCliente.

pages/cotizacion
Vista de cotizacion generada, equivale a InterfazCotizacion.

pages/cobranza
Registro de entrega y cobro, equivale a InterfazCobranza.

pages/reporte
Generacion de reportes de pedidos e ingresos, equivale a InterfazReporte.

js
Funciones que hacen fetch hacia los endpoints del backend: api.js, pedido.js, cotizacion.js, cobranza.js.

css
Estilos visuales del sistema.

## Como correr el proyecto

1. Base de datos: ejecutar backend/database/digesk_schema.sql en MySQL para crear DigeskDB.
2. Backend: abrir la carpeta backend en VS Code, configurar application.properties con tus credenciales de MySQL, y ejecutar la clase principal DigeskBackendApplication.java. Por defecto corre en http://localhost:8080
3. Interfaces: abrir interfaces/index.html con la extension Live Server de VS Code. Las paginas consumen la API en http://localhost:8080/api/...

## Patrones de diseño aplicados

MVC (Modelo Vista Controlador)
Separacion entre Vista (interfaces), Controlador (backend controller) y Modelo (backend entity).

DAO
Acceso a datos desacoplado en backend repository.

Arquitectura por Capas
Presentacion (interfaces) hacia Negocio (controller y service) hacia Datos (repository y entity).