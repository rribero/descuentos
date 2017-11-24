package finaldescuentos;

import java.util.Arrays;
import java.util.List;

public enum Categoria {
    Uno(0.97), Dos(0.95), Tres(0.93);

    @Override
    public String toString() {
        return name();
    }

    private double descuento;
    
    Categoria(double d){
        descuento = d;
    }
    
    public double descuento(double valor){
        return valor*descuento;
    }
    
    public static List<Categoria> getCategorias(){
        return Arrays.asList(values());
    }
    
}