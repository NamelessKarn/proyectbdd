
CREATE DATABASE IF NOT EXISTS laboratorio_circuitos;
USE laboratorio_circuitos;

-- Alumno
CREATE TABLE Alumno (
    id_alumno INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    carrera VARCHAR(100),
    contraseña VARCHAR(100) NOT NULL
);

-- Administrador
CREATE TABLE Administrador (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(100) NOT NULL
);

--  Material
CREATE TABLE Material (
    id_material INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cantidad_total INT NOT NULL,
    cantidad_disponible INT NOT NULL
);

-- Prestamo
CREATE TABLE Prestamo (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY,
    id_alumno INT,
    fecha_prestamo DATE NOT NULL,
    fecha_limite DATE NOT NULL,
    FOREIGN KEY (id_alumno) REFERENCES Alumno(id_alumno)
);

--  DetallePrestamo
CREATE TABLE DetallePrestamo (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_prestamo INT,
    id_material INT,
    cantidad_prestada INT NOT NULL,
    FOREIGN KEY (id_prestamo) REFERENCES Prestamo(id_prestamo),
    FOREIGN KEY (id_material) REFERENCES Material(id_material)
);

--  Devolucion
CREATE TABLE Devolucion (
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_detalle INT,
    cantidad_devuelta INT NOT NULL,
    fecha_devolucion DATE NOT NULL,
    FOREIGN KEY (id_detalle) REFERENCES DetallePrestamo(id_detalle)
);

-- Deuda
CREATE TABLE Deuda (
    id_deuda INT AUTO_INCREMENT PRIMARY KEY,
    id_alumno INT,
    id_material INT,
    cantidad_adeudada INT NOT NULL,
    fecha_generacion DATE NOT NULL,
    FOREIGN KEY (id_alumno) REFERENCES Alumno(id_alumno),
    FOREIGN KEY (id_material) REFERENCES Material(id_material)
);

-- INSERTAR VALORES DE PRUEBA A LAS TABLAS
INSERT INTO Material (nombre, cantidad_total, cantidad_disponible)
VALUES
('Diodo LED', 200, 180),
('Papel Aislante', 500, 500),
('Pila AA', 120, 100),
('Condensador Electrolytic 220µF', 40, 40),
('Punto de Soldadura', 50, 50),
('Cable de Conexión', 300, 250),
('Resistencia 10kΩ', 150, 120),
('Placa de Circuito', 75, 60),
('Transistor PNP', 25, 25),
('Interruptor', 100, 90);
INSERT INTO Alumno (nombre, correo, carrera, contraseña)
VALUES
('Juan Pérez', 'juan.perez@ejemplo.com', 'Ingeniería Electrónica', 'contrasena123'),
('Ana García', 'ana.garcia@ejemplo.com', 'Ingeniería Eléctrica', '1234secreta');


INSERT INTO Administrador (nombre, correo, contraseña)
VALUES
('Carlos López', 'carlos.lopez@ejemplo.com', 'adminpass123'),
('Marta Sánchez', 'marta.sanchez@ejemplo.com', 'admin321');


INSERT INTO Prestamo (id_alumno, fecha_prestamo, fecha_limite)
VALUES
(1, '2025-04-20', '2025-04-27'),
(2, '2025-04-21', '2025-04-28');

INSERT INTO DetallePrestamo (id_prestamo, id_material, cantidad_prestada)
VALUES
(1, 1, 5),
(1, 2, 10),
(2, 3, 3);


INSERT INTO Devolucion (id_detalle, cantidad_devuelta, fecha_devolucion)
VALUES
(1, 5, '2025-04-25'),
(2, 10, '2025-04-25'),
(3, 3, '2025-04-26');

INSERT INTO Deuda (id_alumno, id_material, cantidad_adeudada, fecha_generacion)
VALUES
(1, 1, 5, '2025-04-23'),
(2, 3, 3, '2025-04-24');
