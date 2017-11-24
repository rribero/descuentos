package finaldescuentos;

import java.util.Arrays;
import java.util.List;

public enum Concepto {
    Supermercado(0.95), Indumentaria(0.85), Combustible(0.90);

    @Override
    public String toString() {
        return name();
    }

    private double descuento;
    
    Concepto(double d){
        descuento = d;
    }
    
    public double descuento(double valor){
        return valor*descuento;
    }
    
    public static List<Concepto> getConceptos(){
        return Arrays.asList(values());
    }
    
}
