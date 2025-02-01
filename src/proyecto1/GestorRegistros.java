package proyecto1;

import java.util.HashMap;
import java.util.Map;

public class GestorRegistros {
    private Map<String, Registro> registros;
    
    //Constructor
    public GestorRegistros() {
        registros = new HashMap<>();
        //Crea los registros temporales de proposito general $t0-$t9
        for (int i = 0; i <= 9; i++) {
            registros.put("$t" + i, new Registro("$t" + i));
        }
    }
    
    
    public String asignarRegistro() {
        for (Registro reg : registros.values()) {
            if (reg.isDisponible()) {
                reg.asignarEtiqueta("");
                return reg.getEnsamblador(); // Retorna el registro asignado
        }
    }
    return null; // No hay registros disponibles
    }

    // Asigna una etiqueta temporal a un registro disponible
    public String asignarRegistroTemp(String etiqueta) {
        for (Registro reg : registros.values()) {
            if (reg.isDisponible()) {
                reg.asignarEtiqueta(etiqueta);
                return reg.getEnsamblador(); // Retorna el registro asignado
            }
        }
        return null; // No hay registros disponibles
    }


    // Libera un registro basado en la etiqueta temporal
    public boolean liberarRegistro(String etiqueta) {
        for (Registro reg : registros.values()) {
            if (etiqueta.equals(reg.getEtiqueta())) {
                reg.liberar();
                return true;
            }
        }
        return false; // No se encontró la etiqueta
    }
    
    //Obtener registro por etiqueta
    public String obtenerRegistroPorEtiqueta(String etiqueta) {
    for (Map.Entry<String, Registro> entry : registros.entrySet()) {
        if (etiqueta.equals(entry.getValue().getEtiqueta())) {
            return entry.getKey(); // Retorna el nombre del registro ($t0, $t1, etc.)
        }
    }
    return null; // No se encontró la etiqueta
}

  

    // Imprime el estado actual de los registros
    public void mostrarRegistros() {
        for (Registro reg : registros.values()) {
            System.out.println(reg);
        }
    }
}

