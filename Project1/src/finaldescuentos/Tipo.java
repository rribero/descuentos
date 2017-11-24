package finaldescuentos;

import java.util.Arrays;
import java.util.List;

public enum Tipo {
    Basico(0.97), Especial(0.985);

    @Override
    public String toString() {
        return name();
    }

    private double descuento;
    
    Tipo(double d){
        descuento = d;
    }
    
    public double descuento(double valor){
        return valor*descuento;
    }
    
    public static List<Tipo> getTipos(){
        return Arrays.asList(values());
    }
    
}
