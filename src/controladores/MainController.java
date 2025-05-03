package controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {


    public void abrirLoginAdministrador() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginAdministrador.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Login Administrador");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void abrirLoginAlumno() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginAlumno.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Login Alumno");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
