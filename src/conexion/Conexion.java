package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/laboratorio_circuitos"; // Cambia esto según tu configuración
    private static final String USER = "root";  // Cambia según tu usuario
    private static final String PASSWORD = "danyxD31";  // Cambia según tu contraseña

    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de MySQL (asegurarte de tener el conector JDBC de MySQL en tu proyecto)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error al conectar con la base de datos.");
        }
    }
}
