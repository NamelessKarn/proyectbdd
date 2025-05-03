package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import conexion.Conexion;

import java.sql.*;
import java.time.LocalDate;

public class AlumnoDashboardController {

    @FXML private Label nombreLabel;
    @FXML private Label carreraLabel;
    @FXML private ListView<String> materialesPrestadosList;
    @FXML private ListView<String> deudasList;
    @FXML private ListView<String> devolucionesList;

    @FXML private ComboBox<String> comboMaterialesDisponibles;
    @FXML private ComboBox<String> comboMaterialesPrestados;
    @FXML private TextField cantidadPedirField;
    @FXML private TextField cantidadDevolverField;

    private int idAlumno;

    public void mostrarDatosAlumno(int idAlumno, String nombre, String carrera) {
        this.idAlumno = idAlumno;
        nombreLabel.setText("Nombre: " + nombre);
        carreraLabel.setText("Carrera: " + carrera);

        cargarMaterialesDisponibles();
        cargarMaterialesPrestados(idAlumno);
        cargarDeudas(idAlumno);
        cargarDevoluciones(idAlumno);
    }

    private void cargarMaterialesDisponibles() {
        comboMaterialesDisponibles.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT nombre FROM Material";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while (rs.next()) comboMaterialesDisponibles.getItems().add(rs.getString("nombre"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarMaterialesPrestados(int idAlumno) {
        comboMaterialesPrestados.getItems().clear();
        materialesPrestadosList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT m.nombre, dp.cantidad_prestada, p.fecha_prestamo FROM Material m " +
                    "JOIN DetallePrestamo dp ON m.id_material = dp.id_material " +
                    "JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo WHERE p.id_alumno = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String item = nombre + " - Cantidad: " + rs.getInt("cantidad_prestada") +
                        " - Fecha: " + rs.getDate("fecha_prestamo");
                materialesPrestadosList.getItems().add(item);
                if (!comboMaterialesPrestados.getItems().contains(nombre)) {
                    comboMaterialesPrestados.getItems().add(nombre);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarDeudas(int idAlumno) {
        deudasList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT m.nombre, d.cantidad_adeudada FROM Deuda d " +
                         "JOIN Material m ON d.id_material = m.id_material WHERE d.id_alumno = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String deuda = rs.getString("nombre") + " - Deuda: " + rs.getInt("cantidad_adeudada");
                deudasList.getItems().add(deuda);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarDevoluciones(int idAlumno) {
        devolucionesList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT m.nombre, dp.cantidad_prestada, d.cantidad_devuelta, d.fecha_devolucion FROM Devolucion d " +
                    "JOIN DetallePrestamo dp ON d.id_detalle = dp.id_detalle " +
                    "JOIN Material m ON dp.id_material = m.id_material " +
                    "JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo WHERE p.id_alumno = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String devolucion = rs.getString("nombre") + " - Prestado: " + rs.getInt("cantidad_prestada") +
                        " - Devuelto: " + rs.getInt("cantidad_devuelta") +
                        " - Fecha: " + rs.getDate("fecha_devolucion");
                devolucionesList.getItems().add(devolucion);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void pedirMaterial() {
        String nombreMaterial = comboMaterialesDisponibles.getValue();
        if (nombreMaterial == null || cantidadPedirField.getText().isEmpty()) {
            mostrarAlerta("Selecciona un material y escribe una cantidad.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadPedirField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número válido.");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            String sqlMat = "SELECT id_material, cantidad_disponible FROM Material WHERE nombre = ?";
            PreparedStatement stmtMat = conn.prepareStatement(sqlMat);
            stmtMat.setString(1, nombreMaterial);
            ResultSet rs = stmtMat.executeQuery();

            if (rs.next()) {
                int idMaterial = rs.getInt("id_material");
                int disponibles = rs.getInt("cantidad_disponible");

                if (cantidad > disponibles) {
                    mostrarAlerta("No hay suficiente material disponible.");
                    return;
                }

                // Registrar el préstamo
                String insertPrestamo = "INSERT INTO Prestamo (id_alumno, fecha_prestamo, fecha_limite) VALUES (?, ?, ?)";
                PreparedStatement stmtPrestamo = conn.prepareStatement(insertPrestamo, Statement.RETURN_GENERATED_KEYS);
                stmtPrestamo.setInt(1, idAlumno);
                stmtPrestamo.setDate(2, Date.valueOf(LocalDate.now()));
                stmtPrestamo.setDate(3, Date.valueOf(LocalDate.now().plusDays(7)));
                stmtPrestamo.executeUpdate();
                ResultSet rsKey = stmtPrestamo.getGeneratedKeys();
                rsKey.next();
                int idPrestamo = rsKey.getInt(1);

                // Registrar detalle del préstamo
                String insertDetalle = "INSERT INTO DetallePrestamo (id_prestamo, id_material, cantidad_prestada) VALUES (?, ?, ?)";
                PreparedStatement stmtDetalle = conn.prepareStatement(insertDetalle);
                stmtDetalle.setInt(1, idPrestamo);
                stmtDetalle.setInt(2, idMaterial);
                stmtDetalle.setInt(3, cantidad);
                stmtDetalle.executeUpdate();

                // Actualizar la cantidad disponible de material
                String updateMat = "UPDATE Material SET cantidad_disponible = cantidad_disponible - ? WHERE id_material = ?";
                PreparedStatement stmtUpdate = conn.prepareStatement(updateMat);
                stmtUpdate.setInt(1, cantidad);
                stmtUpdate.setInt(2, idMaterial);
                stmtUpdate.executeUpdate();

                // Crear o actualizar deuda automáticamente
                String sqlDeuda = "SELECT cantidad_adeudada FROM Deuda WHERE id_alumno = ? AND id_material = ?";
                PreparedStatement stmtDeuda = conn.prepareStatement(sqlDeuda);
                stmtDeuda.setInt(1, idAlumno);
                stmtDeuda.setInt(2, idMaterial);
                ResultSet rsDeuda = stmtDeuda.executeQuery();

                if (!rsDeuda.next()) {
                    // Si no existe deuda, insertamos la deuda con la cantidad pedida y la fecha actual
                    String insertDeuda = "INSERT INTO Deuda (id_alumno, id_material, cantidad_adeudada, fecha_generacion) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmtInsertDeuda = conn.prepareStatement(insertDeuda);
                    stmtInsertDeuda.setInt(1, idAlumno);
                    stmtInsertDeuda.setInt(2, idMaterial);
                    stmtInsertDeuda.setInt(3, cantidad);
                    stmtInsertDeuda.setDate(4, Date.valueOf(LocalDate.now())); // Fecha actual
                    stmtInsertDeuda.executeUpdate();
                } else {
                    // Si ya existe deuda, actualizamos sumando la cantidad pedida
                    String actualizarDeuda = "UPDATE Deuda SET cantidad_adeudada = cantidad_adeudada + ? WHERE id_alumno = ? AND id_material = ?";
                    PreparedStatement stmtActualizarDeuda = conn.prepareStatement(actualizarDeuda);
                    stmtActualizarDeuda.setInt(1, cantidad);
                    stmtActualizarDeuda.setInt(2, idAlumno);
                    stmtActualizarDeuda.setInt(3, idMaterial);
                    stmtActualizarDeuda.executeUpdate();
                }

                // Actualizar las listas de la vista
                cargarMaterialesPrestados(idAlumno);
                cargarMaterialesDisponibles();
                cargarDeudas(idAlumno);
                mostrarAlerta("Préstamo registrado correctamente.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }


    @FXML private void devolverMaterial() {
        String nombreMaterial = comboMaterialesPrestados.getValue();
        if (nombreMaterial == null || cantidadDevolverField.getText().isEmpty()) {
            mostrarAlerta("Selecciona un material prestado y escribe una cantidad.");
            return;
        }

        int cantidadDevuelta;
        try {
            cantidadDevuelta = Integer.parseInt(cantidadDevolverField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número válido.");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            String sqlDetalle = "SELECT dp.id_detalle, m.id_material, IFNULL(d.cantidad_adeudada, dp.cantidad_prestada) AS cantidad_adeudada " +
                    "FROM DetallePrestamo dp " +
                    "JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo " +
                    "JOIN Material m ON dp.id_material = m.id_material " +
                    "LEFT JOIN Deuda d ON d.id_alumno = p.id_alumno AND d.id_material = m.id_material " +
                    "WHERE p.id_alumno = ? AND m.nombre = ? LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sqlDetalle);
            stmt.setInt(1, idAlumno);
            stmt.setString(2, nombreMaterial);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int idDetalle = rs.getInt("id_detalle");
                int idMaterial = rs.getInt("id_material");
                int adeudado = rs.getInt("cantidad_adeudada");

                if (adeudado == 0) {
                    mostrarAlerta("No se debe nada de este material.");
                    return;
                }

                String insertDev = "INSERT INTO Devolucion (id_detalle, cantidad_devuelta, fecha_devolucion) VALUES (?, ?, ?)";
                PreparedStatement stmtDev = conn.prepareStatement(insertDev);
                stmtDev.setInt(1, idDetalle);
                stmtDev.setInt(2, cantidadDevuelta);
                stmtDev.setDate(3, Date.valueOf(LocalDate.now()));
                stmtDev.executeUpdate();

                String updateMat = "UPDATE Material SET cantidad_disponible = cantidad_disponible + ? WHERE id_material = ?";
                PreparedStatement stmtUpdateMat = conn.prepareStatement(updateMat);
                stmtUpdateMat.setInt(1, cantidadDevuelta);
                stmtUpdateMat.setInt(2, idMaterial);
                stmtUpdateMat.executeUpdate();

                // Actualizamos la deuda si corresponde
                String sqlDeuda = "SELECT cantidad_adeudada FROM Deuda WHERE id_alumno = ? AND id_material = ?";
                PreparedStatement stmtDeuda = conn.prepareStatement(sqlDeuda);
                stmtDeuda.setInt(1, idAlumno);
                stmtDeuda.setInt(2, idMaterial);
                ResultSet rsDeuda = stmtDeuda.executeQuery();
                if (rsDeuda.next()) {
                    int deuda = rsDeuda.getInt("cantidad_adeudada");
                    if (deuda > cantidadDevuelta) {
                        String updateDeuda = "UPDATE Deuda SET cantidad_adeudada = cantidad_adeudada - ? WHERE id_alumno = ? AND id_material = ?";
                        PreparedStatement stmtUpdateDeuda = conn.prepareStatement(updateDeuda);
                        stmtUpdateDeuda.setInt(1, cantidadDevuelta);
                        stmtUpdateDeuda.setInt(2, idAlumno);
                        stmtUpdateDeuda.setInt(3, idMaterial);
                        stmtUpdateDeuda.executeUpdate();
                    } else {
                        String deleteDeuda = "DELETE FROM Deuda WHERE id_alumno = ? AND id_material = ?";
                        PreparedStatement stmtDeleteDeuda = conn.prepareStatement(deleteDeuda);
                        stmtDeleteDeuda.setInt(1, idAlumno);
                        stmtDeleteDeuda.setInt(2, idMaterial);
                        stmtDeleteDeuda.executeUpdate();
                    }
                }

                cargarMaterialesPrestados(idAlumno);
                cargarDeudas(idAlumno);
                cargarDevoluciones(idAlumno);
                mostrarAlerta("Devolución registrada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
