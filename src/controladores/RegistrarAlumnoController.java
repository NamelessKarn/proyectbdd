package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import conexion.Conexion;
import java.sql.*;

public class RegistrarAlumnoController {

    @FXML
    private TextField nombreField, correoField, carreraField;

    @FXML
    private PasswordField contraseñaField;

    public void registrarAlumno() {
        String nombre = nombreField.getText();
        String correo = correoField.getText();
        String carrera = carreraField.getText();
        String contraseña = contraseñaField.getText();

        if (nombre.isEmpty() || correo.isEmpty() || carrera.isEmpty() || contraseña.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingresa todos los campos.", AlertType.WARNING);
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            String sqlCheck = "SELECT * FROM Alumno WHERE correo = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, correo);
            ResultSet rsCheck = stmtCheck.executeQuery();

            if (rsCheck.next()) {
                mostrarAlerta("Error", "El correo ya está registrado.", AlertType.ERROR);
            } else {
                String sqlInsert = "INSERT INTO Alumno (nombre, correo, carrera, contraseña) VALUES (?, ?, ?, ?)";
                PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);
                stmtInsert.setString(1, nombre);
                stmtInsert.setString(2, correo);
                stmtInsert.setString(3, carrera);
                stmtInsert.setString(4, contraseña);

                int rowsAffected = stmtInsert.executeUpdate();
                if (rowsAffected > 0) {
                    mostrarAlerta("Éxito", "Registro exitoso. Ahora puedes iniciar sesión.", AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "Hubo un problema con el registro. Intenta de nuevo.", AlertType.ERROR);
                }
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
}
