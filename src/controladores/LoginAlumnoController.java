package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import conexion.Conexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginAlumnoController {

    @FXML
    private TextField correoField;

    @FXML
    private PasswordField contraseñaField;

    @FXML
    private Button btnRegistrar;  // Botón para ir a registrar

    public void loginAlumno() {
        String correo = correoField.getText();
        String contraseña = contraseñaField.getText();

        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM Alumno WHERE correo = ? AND contraseña = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            stmt.setString(2, contraseña);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Login correcto, mostrar datos del alumno
                int idAlumno = rs.getInt("id_alumno");
                String nombre = rs.getString("nombre");
                String carrera = rs.getString("carrera");

                // Redirigir a la vista AlumnoDashboard.fxml
                abrirVentanaAlumno(idAlumno, nombre, carrera);
            } else {
                // Si no se encuentra al alumno
                mostrarAlerta("Error", "Correo o contraseña incorrectos.", AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Hubo un problema con la conexión a la base de datos.", AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVentanaAlumno(int idAlumno, String nombre, String carrera) {
        try {
            // Asegúrate de que la ruta sea correcta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/AlumnoDashboard.fxml"));
            VBox vbox = loader.load();
            AlumnoDashboardController controller = loader.getController();
            controller.mostrarDatosAlumno(idAlumno, nombre, carrera);

            // Crear y mostrar la nueva ventana
            Scene scene = new Scene(vbox);
            Stage stage = new Stage();
            stage.setTitle("Panel de Alumno");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana del alumno.", AlertType.ERROR);
        }
    }

    // Nuevo método para redirigir a la ventana de registro
    public void irARegistrarAlumno() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/RegistrarAlumno.fxml"));
            VBox vbox = loader.load();

            Scene scene = new Scene(vbox);
            Stage stage = new Stage();
            stage.setTitle("Registrar Alumno");
            stage.setScene(scene);
            stage.show();
            
            // Cerrar ventana actual (Login)
            Stage currentStage = (Stage) btnRegistrar.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana de registro.", AlertType.ERROR);
        }
    }
}
