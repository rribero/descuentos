package finaldescuentos;


public class Empleado {
    
    private int codigo;
    private String nombre;
    private int dni;
    private Categoria categoria;

    public Empleado(int codigo, String nombre, int dni, Categoria categoria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.dni = dni;
        this.categoria = categoria;
    }

    
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public int getDni() {
        return dni;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
