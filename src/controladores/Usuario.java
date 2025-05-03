package controladores;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String tipoUsuario;

    public Usuario(int idUsuario, String nombre, String correo, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.tipoUsuario = tipoUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
}
