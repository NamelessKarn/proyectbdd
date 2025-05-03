
-- BASE DE DATOS: laboratorio_circuitos
-- Vistas, Procedimientos, Funciones y Triggers
USE laboratorio_circuitos;


-- VISTA 1: Vista de materiales disponibles

CREATE OR REPLACE VIEW vista_materiales_disponibles AS
SELECT 
    id_material,
    nombre,
    cantidad_total,
    cantidad_disponible
FROM Material
WHERE cantidad_disponible > 0;


-- VISTA 2: Vista de préstamos activos con nombre del alumno

CREATE OR REPLACE VIEW vista_prestamos_activos AS
SELECT 
    P.id_prestamo,
    A.nombre AS alumno,
    P.fecha_prestamo,
    P.fecha_limite
FROM Prestamo P
JOIN Alumno A ON A.id_alumno = P.id_alumno
WHERE P.id_prestamo IN (
    SELECT id_prestamo FROM DetallePrestamo
    WHERE id_detalle NOT IN (
        SELECT id_detalle FROM Devolucion
    )
);

-- FUNCION: Obtener total prestado por alumno


DELIMITER //

CREATE FUNCTION total_material_prestado(id INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE total INT;
    SELECT SUM(cantidad_prestada) INTO total
    FROM DetallePrestamo DP
    JOIN Prestamo P ON DP.id_prestamo = P.id_prestamo
    WHERE P.id_alumno = id;
    
    RETURN IFNULL(total, 0);
END;
//

DELIMITER ;


-- PROCEDIMIENTO: Generar deuda por materiales no devueltos

DELIMITER //

CREATE PROCEDURE generarDeuda()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE idDet INT;
    DECLARE idAlum INT;
    DECLARE idMat INT;
    DECLARE cantPrest INT;
    DECLARE cantDev INT;
    DECLARE cantAdeudo INT;
    DECLARE cur CURSOR FOR
        SELECT DP.id_detalle, P.id_alumno, DP.id_material, DP.cantidad_prestada
        FROM DetallePrestamo DP
        JOIN Prestamo P ON P.id_prestamo = DP.id_prestamo
        WHERE DP.id_detalle NOT IN (
            SELECT id_detalle FROM Devolucion
        );
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    leer: LOOP
        FETCH cur INTO idDet, idAlum, idMat, cantPrest;
        IF done THEN
            LEAVE leer;
        END IF;

        SELECT IFNULL(SUM(cantidad_devuelta), 0) INTO cantDev
        FROM Devolucion
        WHERE id_detalle = idDet;

        SET cantAdeudo = cantPrest - cantDev;

        IF cantAdeudo > 0 THEN
            INSERT INTO Deuda(id_alumno, id_material, cantidad_adeudada, fecha_generacion)
            VALUES (idAlum, idMat, cantAdeudo, CURDATE());
        END IF;

    END LOOP;

    CLOSE cur;
END;
//

DELIMITER ;


-- TRIGGER: Actualizar cantidad_disponible al devolver material

DELIMITER //

CREATE TRIGGER actualizarInventarioDevolucion
AFTER INSERT ON Devolucion
FOR EACH ROW
BEGIN
    DECLARE idMat INT;
    SELECT id_material INTO idMat
    FROM DetallePrestamo
    WHERE id_detalle = NEW.id_detalle;

    UPDATE Material
    SET cantidad_disponible = cantidad_disponible + NEW.cantidad_devuelta
    WHERE id_material = idMat;
END;
//

DELIMITER ;

-- TRIGGER: Prevenir préstamo mayor al stock disponible

DELIMITER //

CREATE TRIGGER validarDisponibilidadAntesDePrestar
BEFORE INSERT ON DetallePrestamo
FOR EACH ROW
BEGIN
    DECLARE stock INT;
    SELECT cantidad_disponible INTO stock
    FROM Material
    WHERE id_material = NEW.id_material;

    IF NEW.cantidad_prestada > stock THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No hay suficiente material disponible para realizar el préstamo';
    END IF;
END;
//

DELIMITER ;
