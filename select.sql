
-- CONSULTAS SQL PARA EL SISTEMA DE LABORATORIO


-- 1. Consulta de materiales disponibles con su cantidad
-- Muestra el nombre de cada material y la cantidad disponible en inventario
SELECT nombre, cantidad_disponible
FROM Material;

-- 2. Préstamos activos por alumno
-- Lista los préstamos cuya fecha límite aún no ha pasado
SELECT a.nombre AS alumno, p.fecha_prestamo, p.fecha_limite
FROM Prestamo p
JOIN Alumno a ON p.id_alumno = a.id_alumno
WHERE p.fecha_limite >= CURDATE();

-- 3. Consulta de deudas pendientes por alumno
-- Muestra los alumnos que tienen deudas, con el nombre del material y cantidad adeudada
SELECT a.nombre AS alumno, m.nombre AS material, d.cantidad_adeudada
FROM Deuda d
JOIN Alumno a ON d.id_alumno = a.id_alumno
JOIN Material m ON d.id_material = m.id_material
WHERE d.cantidad_adeudada > 0;

-- 4. Historial de devoluciones realizadas por un alumno
-- Devuelve el historial de devoluciones para un alumno específico
-- Reemplaza '1' por el ID del alumno que deseas consultar
SELECT a.nombre AS alumno, m.nombre AS material, de.cantidad_devuelta, de.fecha_devolucion
FROM Devolucion de
JOIN DetallePrestamo dp ON de.id_detalle = dp.id_detalle
JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo
JOIN Alumno a ON p.id_alumno = a.id_alumno
JOIN Material m ON dp.id_material = m.id_material
WHERE a.id_alumno = 1;

-- 5. Alumnos con más préstamos registrados
-- Lista los alumnos ordenados por la cantidad de préstamos realizados
SELECT a.nombre AS alumno, COUNT(p.id_prestamo) AS total_prestamos
FROM Prestamo p
JOIN Alumno a ON p.id_alumno = a.id_alumno
GROUP BY a.id_alumno
ORDER BY total_prestamos DESC;

-- 6. Inventario general agrupado por tipo de material
-- Lista todos los materiales con su cantidad total y disponible
SELECT nombre AS material, cantidad_total, cantidad_disponible
FROM Material;

-- 7. Subconsulta: alumnos que han devuelto menos del 100% de lo prestado
-- Identifica a los alumnos que no han devuelto todo el material que pidieron
SELECT DISTINCT a.nombre AS alumno
FROM Alumno a
JOIN Prestamo p ON a.id_alumno = p.id_alumno
JOIN DetallePrestamo dp ON p.id_prestamo = dp.id_prestamo
LEFT JOIN (
    SELECT id_detalle, SUM(cantidad_devuelta) AS total_devuelto
    FROM Devolucion
    GROUP BY id_detalle
) d ON dp.id_detalle = d.id_detalle
WHERE IFNULL(d.total_devuelto, 0) < dp.cantidad_prestada;
