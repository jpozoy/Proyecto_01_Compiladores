package proyecto1;
import java.util.*;

public class MipsGenerator {
    //TAC
    private String codigoIntermedio;
    private StringBuilder data;
    private StringBuilder text;
    //MIPS final
    private StringBuilder codigoMips;
    
    //Encontrar y reservar estructuras estaticas: Strings, Arrays
    
    public MipsGenerator(String codigoIntermedio) {
            this.codigoIntermedio = codigoIntermedio;
            this.codigoMips = new StringBuilder();
            this.data = new StringBuilder(".data\n");
            this.text = new StringBuilder(".text\n");
            System.out.println("Esto sucede");
    }
    public void procesarTac() {
        //Separar lineas de codigo intermedio
        String[] lines = codigoIntermedio.split("\n");
        for (String line : lines) {
            procesarLinea(line);
        }
        //Concatenera sección .data y .text al final
        this.codigoMips.append(data);
        this.codigoMips.append(text);
    }
    //Procesar cada linea
    public void procesarLinea(String line) {
        //Si es un string
        if(line.startsWith("string:")){
            procesarString(line);
        } else if (line.matches("^_[a-zA-Z0-9_]+:$")) {
            text.append(line + "\n");
        } else if(line.startsWith("int:")) {
            procesarInt(line);
        }
        else if (line.matches("^t[0-9]+=.*$")) {
            procesarTemporal(line);
        }
    };
    //Procesar cada línea independiente
    public void procesarString(String line) {
        String[] partes = line.split("=", 2);
        String nombre = partes[0].replace("string:", "").trim(); // Tomar el nombre y quitar "string:"
        String valor = (partes.length > 1) ? "\"" + partes[1].trim() + "\"" : "\"\""; // Envolver en comillas

        String newline = nombre + ":  .asciiz  " + valor + "\n";  
        data.append(newline);
    }
    //Si es un entero
    public void procesarInt(String line) {
        String[] partes = line.split(":|="); // Divide por ":" o "="

        if (partes.length < 3) return; // Evita errores en líneas incorrectas

        String nombre = partes[1].trim(); // Obtiene el nombre de la variable
        String valor = partes[2].trim();  // Obtiene el valor

        // Solo procesa si el valor es un número (ignora registros temporales como t0, t1...)
        if (valor.matches("\\d+")) {
            String newline = nombre + ": .half " + valor + "\n";
            data.append(newline);
        }
    }
    
    public void procesarTemporal(String line){
        System.out.println("Se Proceso un temporal");
        System.out.println(line);
    }

    //Imprimir codigo momentaneamente 
    public String getCodigoMIPS() {
        System.out.println(codigoMips.toString());
        return codigoMips.toString();
    }
    
}
