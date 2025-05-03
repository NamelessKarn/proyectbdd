package controladores;


public class Libro {
    private int idLibro;
    private String titulo;
    private String autor;
    private String editorial;

    public Libro(int idLibro, String titulo, String autor, String editorial) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }
}
