package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import conexion.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardController {

    @FXML
    private ListView<String> alumnosList;
    @FXML
    private ListView<String> materialesList;
    @FXML
    private ListView<String> deudasList;
    @FXML
    private ListView<String> prestamosList;
    @FXML
    private ListView<String> devolucionesList;

    public void initialize() {
        cargarAlumnos();
        cargarMateriales();
        cargarDeudas();
        cargarPrestamos();
        cargarDevoluciones();
    }

    private void cargarAlumnos() {
        alumnosList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM Alumno";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String alumno = rs.getString("nombre") + " - " + rs.getString("correo") + " - " + rs.getString("carrera");
                alumnosList.getItems().add(alumno);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los alumnos.", AlertType.ERROR);
        }
    }

    private void cargarMateriales() {
        materialesList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM Material";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String material = rs.getString("nombre") + " - Total: " + rs.getInt("cantidad_total") + " - Disponible: " + rs.getInt("cantidad_disponible");
                materialesList.getItems().add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los materiales.", AlertType.ERROR);
        }
    }

    private void cargarDeudas() {
        deudasList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT a.nombre, m.nombre AS material, d.cantidad_adeudada FROM Deuda d " +
                         "JOIN Alumno a ON d.id_alumno = a.id_alumno " +
                         "JOIN Material m ON d.id_material = m.id_material";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String deuda = rs.getString("nombre") + " - Material: " + rs.getString("material") +
                               " - Deuda: " + rs.getInt("cantidad_adeudada");
                deudasList.getItems().add(deuda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las deudas.", AlertType.ERROR);
        }
    }

    private void cargarPrestamos() {
        prestamosList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT a.nombre, m.nombre AS material, dp.cantidad_prestada, p.fecha_prestamo " +
                         "FROM Prestamo p " +
                         "JOIN DetallePrestamo dp ON p.id_prestamo = dp.id_prestamo " +
                         "JOIN Material m ON dp.id_material = m.id_material " +
                         "JOIN Alumno a ON p.id_alumno = a.id_alumno";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String prestamo = rs.getString("nombre") + " - Material: " + rs.getString("material") +
                                  " - Cantidad: " + rs.getInt("cantidad_prestada") +
                                  " - Fecha préstamo: " + rs.getDate("fecha_prestamo");
                prestamosList.getItems().add(prestamo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los préstamos.", AlertType.ERROR);
        }
    }

    private void cargarDevoluciones() {
        devolucionesList.getItems().clear();
        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT a.nombre, m.nombre AS material, d.cantidad_devuelta, d.fecha_devolucion " +
                         "FROM Devolucion d " +
                         "JOIN DetallePrestamo dp ON d.id_detalle = dp.id_detalle " +
                         "JOIN Material m ON dp.id_material = m.id_material " +
                         "JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo " +
                         "JOIN Alumno a ON p.id_alumno = a.id_alumno";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String devolucion = rs.getString("nombre") + " - Material: " + rs.getString("material") +
                                    " - Cantidad devuelta: " + rs.getInt("cantidad_devuelta") +
                                    " - Fecha: " + rs.getDate("fecha_devolucion");
                devolucionesList.getItems().add(devolucion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las devoluciones.", AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
