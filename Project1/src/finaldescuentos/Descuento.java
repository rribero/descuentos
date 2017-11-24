package finaldescuentos;

import java.text.DecimalFormat;

public class Descuento{


    private int cod;
    protected double importe;
    protected double importecondesc;
    private Tipo tipo;
    private Concepto concepto;
    private Empleado empleado;
    private double totaldescuento;
    //para poder eliminarlo del Jlist
    private int indexeliminar;
    
    
    //formato de numeros double
    DecimalFormat df = new DecimalFormat("#.00");

    public Descuento(int cod, double importe, double importecondesc, Tipo tipo, Concepto concepto, Empleado empleado,
                     double totaldescuento) {
        this.cod = cod;
        this.importe = importe;
        this.importecondesc = importecondesc;
        this.tipo = tipo;
        this.concepto = concepto;
        this.empleado = empleado;
        this.totaldescuento = totaldescuento;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getCod() {
        return cod;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getImporte() {
        return importe;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setConcepto(Concepto concepto) {
        this.concepto = concepto;
    }

    public Concepto getConcepto() {
        return concepto;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setTotaldescuento(double totaldescuento) {
        this.totaldescuento = totaldescuento;
    }

    public double getTotaldescuento() {
        return totaldescuento;
    }

    public void setImportecondesc(double importecondesc) {
        this.importecondesc = importecondesc;
    }

    public double getImportecondesc() {
        return importecondesc;
    }
    
    @Override
    public String toString() {
        return "Código: " + cod + " | Importe: $" + df.format(importe) + " | Cod y Empleado: " + empleado + " | Descuento: $" + df.format(totaldescuento);
    }

    public void setIndexeliminar(int indexeliminar) {
        this.indexeliminar = indexeliminar;
    }

    public int getIndexeliminar() {
        return indexeliminar;
    }
}
