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
        cargarDatos("SELECT * FROM Alumno", alumnosList, "nombre", "correo", "carrera");
        cargarDatos("SELECT * FROM Material", materialesList, "nombre", "cantidad_total", "cantidad_disponible");
        cargarDeudas();
        cargarPrestamos();
        cargarDevoluciones();
    }

    private void cargarDatos(String sql, ListView<String> listView, String... columnas) {
        listView.getItems().clear();
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StringBuilder item = new StringBuilder();
                for (String columna : columnas) {
                    item.append(rs.getString(columna)).append(" - ");
                }
                item.setLength(item.length() - 3); // Elimina el último guion
                listView.getItems().add(item.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los datos.", AlertType.ERROR);
        }
    }

    private void cargarDeudas() {
        deudasList.getItems().clear();
        String sql = "SELECT a.nombre, m.nombre AS material, d.cantidad_adeudada " +
                     "FROM Deuda d " +
                     "JOIN Alumno a ON d.id_alumno = a.id_alumno " +
                     "JOIN Material m ON d.id_material = m.id_material";
        cargarDatos(sql, deudasList, "nombre", "material", "cantidad_adeudada");
    }

    private void cargarPrestamos() {
        prestamosList.getItems().clear();
        String sql = "SELECT a.nombre, m.nombre AS material, dp.cantidad_prestada, p.fecha_prestamo " +
                     "FROM Prestamo p " +
                     "JOIN DetallePrestamo dp ON p.id_prestamo = dp.id_prestamo " +
                     "JOIN Material m ON dp.id_material = m.id_material " +
                     "JOIN Alumno a ON p.id_alumno = a.id_alumno";
        cargarDatos(sql, prestamosList, "nombre", "material", "cantidad_prestada", "fecha_prestamo");
    }

    private void cargarDevoluciones() {
        devolucionesList.getItems().clear();
        String sql = "SELECT a.nombre, m.nombre AS material, d.cantidad_devuelta, d.fecha_devolucion " +
                     "FROM Devolucion d " +
                     "JOIN DetallePrestamo dp ON d.id_detalle = dp.id_detalle " +
                     "JOIN Material m ON dp.id_material = m.id_material " +
                     "JOIN Prestamo p ON dp.id_prestamo = p.id_prestamo " +
                     "JOIN Alumno a ON p.id_alumno = a.id_alumno";
        cargarDatos(sql, devolucionesList, "nombre", "material", "cantidad_devuelta", "fecha_devolucion");
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
