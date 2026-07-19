-- =========================================================
-- Script de respaldo/documentación de la base de datos digeskdb
-- Generado automáticamente por Hibernate (ddl-auto=update)
-- Refleja la estructura física real del sistema Diges'k
-- Actualizado: tb_comprobante ahora admite múltiples registros
-- por pedido (pagos parciales / abonos) — relación ManyToOne
-- =========================================================

CREATE DATABASE IF NOT EXISTS digeskdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE digeskdb;

-- Tabla: tb_cliente
CREATE TABLE tb_cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    telefono   VARCHAR(20),
    correo     VARCHAR(100),
    tipo       VARCHAR(20)
);

-- Tabla: tb_usuario
CREATE TABLE tb_usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    contrasena VARCHAR(200) NOT NULL,
    rol        VARCHAR(20)
);

-- Tabla: tb_pedido
CREATE TABLE tb_pedido (
    id_pedido     INT AUTO_INCREMENT PRIMARY KEY,
    descripcion   TEXT,
    cantidad      INT,
    fecha_entrega DATE,
    estado        VARCHAR(20),
    id_cliente    INT NOT NULL,
    id_usuario    INT,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente)
        REFERENCES tb_cliente (id_cliente) ON DELETE RESTRICT,
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario)
        REFERENCES tb_usuario (id_usuario) ON DELETE RESTRICT
);

-- Tabla: tb_producto
CREATE TABLE tb_producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT,
    tipo        VARCHAR(30),
    id_pedido   INT NOT NULL UNIQUE,
    CONSTRAINT fk_producto_pedido FOREIGN KEY (id_pedido)
        REFERENCES tb_pedido (id_pedido) ON DELETE RESTRICT
);

-- Tabla: tb_cotizacion
CREATE TABLE tb_cotizacion (
    id_cotizacion INT AUTO_INCREMENT PRIMARY KEY,
    precio_total  DECIMAL(10,2),
    fecha         DATE,
    estado        VARCHAR(20),
    id_pedido     INT NOT NULL UNIQUE,
    CONSTRAINT fk_cotizacion_pedido FOREIGN KEY (id_pedido)
        REFERENCES tb_pedido (id_pedido) ON DELETE RESTRICT
);

-- Tabla: tb_comprobante
-- NOTA: id_pedido ya NO es UNIQUE. Un pedido puede generar
-- varios comprobantes (pago inicial + abonos posteriores),
-- necesario para soportar CU-06 (Gestionar Cobranza).
CREATE TABLE tb_comprobante (
    id_comprobante INT AUTO_INCREMENT PRIMARY KEY,
    monto          DECIMAL(10,2),
    fecha_pago     DATE,
    tipo           VARCHAR(20),
    id_pedido      INT NOT NULL,
    CONSTRAINT fk_comprobante_pedido FOREIGN KEY (id_pedido)
        REFERENCES tb_pedido (id_pedido) ON DELETE RESTRICT
);