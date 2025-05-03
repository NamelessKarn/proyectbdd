package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

public class LoginAdministradorController {

    @FXML
    private TextField correoField;

    @FXML
    private PasswordField contraseñaField;

    public void loginAdministrador() {
        String correo = correoField.getText();
        String contraseña = contraseñaField.getText();

        try (Connection conn = Conexion.getConnection()) {
            String sql = "SELECT * FROM Administrador WHERE correo = ? AND contraseña = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, correo);
            stmt.setString(2, contraseña);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Login correcto
                System.out.println("Login exitoso para administrador.");
                abrirVentanaAdministrador();
            } else {
                // Si no se encuentra al administrador
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

    private void abrirVentanaAdministrador() {
        try {
            // Cargar la vista de la ventana del administrador
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/AdminDashboard.fxml"));
            VBox vbox = loader.load();

   
            Scene scene = new Scene(vbox);
            Stage stage = new Stage();
            stage.setTitle("Panel de Administrador");
            stage.setScene(scene);
            stage.show();

          
            Stage loginStage = (Stage) correoField.getScene().getWindow();
            loginStage.close();  

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana del administrador.", AlertType.ERROR);
        }
    }
}
