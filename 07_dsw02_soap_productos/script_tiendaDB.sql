-- SQL Server
CREATE DATABASE TiendaDB;
GO
USE TiendaDB;
GO

CREATE TABLE categoria (
    id_categoria INT IDENTITY(1,1) NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  VARCHAR(250) NULL,
    CONSTRAINT pk_categoria PRIMARY KEY (id_categoria)
);
GO

CREATE TABLE producto (
    id_producto  INT IDENTITY(1,1) NOT NULL,
    nombre       VARCHAR(150)  NOT NULL,
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT           NOT NULL CONSTRAINT df_producto_stock DEFAULT 0,
    id_categoria INT           NOT NULL,
    CONSTRAINT pk_producto PRIMARY KEY (id_producto),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria)
);
GO

INSERT INTO categoria (nombre, descripcion) VALUES
('Laptops',     'Equipos portatiles'),
('Perifericos', 'Teclados, mouse y accesorios'),
('Monitores',   'Pantallas y monitores');

INSERT INTO producto (nombre, precio, stock, id_categoria) VALUES
('Laptop Lenovo ThinkPad E14', 3499.90, 12, 1),
('Laptop HP Pavilion 15',      2899.00,  8, 1),
('Teclado mecanico Redragon',   189.90, 40, 2),
('Mouse Logitech M185',          59.90, 75, 2),
('Monitor LG 27" IPS',          899.00, 15, 3);
GO
