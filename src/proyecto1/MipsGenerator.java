package proyecto1;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MipsGenerator {
    //TAC
    private String codigoIntermedio;
    private StringBuilder data;
    private StringBuilder text;
    //MIPS final
    private StringBuilder codigoMips;
    //Estructura de gestor de registros
    private GestorRegistros gestorRegistros;
    
    //Encontrar y reservar estructuras estaticas: Strings, Arrays
    
    public MipsGenerator(String codigoIntermedio) {
            this.codigoIntermedio = codigoIntermedio;
            this.codigoMips = new StringBuilder();
            this.data = new StringBuilder(".data\n");
            this.text = new StringBuilder(".text\n");
            this.gestorRegistros = new GestorRegistros();
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
        String[] partes = line.split("="); // Divide por "="
        String etiqueta = partes[0].trim(); // "t0", "t1",
        String expresion = partes[1].trim();
        
        // Detectar operador (+, -, *, /, %)
        Pattern pattern = Pattern.compile("(.+?)([+\\-*/%])(.+)");
        Matcher matcher = pattern.matcher(expresion);
        if (matcher.matches()) {
            String operando1 = matcher.group(1).trim(); // Primer operando
            String operador = matcher.group(2).trim();  // Operador
            String operando2 = matcher.group(3).trim(); // Segundo operando

            System.out.println("Etiqueta Temporal: " + etiqueta);
            System.out.println("Operando 1: " + operando1);
            System.out.println("Operador: " + operador);
            System.out.println("Operando 2: " + operando2);
            traducirTemporal(etiqueta, operando1, operando2, operador);
        } else {
            System.out.println("Formato inválido: " + line);
        }
        
        
        
    }
    public void traducirTemporal(String temporal, String op1, String op2, String operador){
         String reg1, reg2, regRes;
         if (op1.matches("^-?\\d+$")) { //Si es un numero
             reg1 = gestorRegistros.asignarRegistro();
             text.append("li " + reg1 + ", " + op1 + "\n");
         } else if (op1.matches("^t\\d+$")){ //Si es un temporal
             reg1 = gestorRegistros.obtenerRegistroPorEtiqueta(op1);
             
         } else { //Si es un identificador
             reg1 = gestorRegistros.asignarRegistroTemp(op1);
             text.append("lw " + reg1 + ", " + op1 + "\n");
         }
         if (op2.matches("^-?\\d+$")) { //Si es un numero
             reg2 = gestorRegistros.asignarRegistro();
             text.append("li " + reg2 + ", " + op2 + "\n");
         } else if (op2.matches("^t\\d+$")){ //Si es un temporal
             reg2 = gestorRegistros.obtenerRegistroPorEtiqueta(op2);
         } else { //Si es un identificador
             reg2 = gestorRegistros.asignarRegistroTemp(op2);
             text.append("lw " + reg2 + ", " + op2 + "\n");
         }
         regRes = gestorRegistros.asignarRegistroTemp(temporal);
         if (regRes == null) {
            System.out.println("Error: No hay registros disponibles.");
            return;
        }
        // Generar la operación MIPS
        switch (operador) {
            case "+": text.append("add " + regRes + ", " + reg1 + ", " + reg2 + "\n"); break;
            case "-": text.append("sub " + regRes + ", " + reg1 + ", " + reg2 + "\n"); break;
            case "*": text.append("mul " + regRes + ", " + reg1 + ", " + reg2 + "\n"); break;
            case "/": text.append("div " + reg1 + ", " + reg2 + "\n" + "mflo " + regRes + "\n"); break;
            case "%": text.append("div " + reg1 + ", " + reg2 + "\n" + "mfhi " + regRes + "\n"); break;
            default:
                System.out.println("Error: Operador no reconocido.");
                return;
        }
        // Liberar registros temporales si no son resultado
        gestorRegistros.liberarRegistro(op1);
        gestorRegistros.liberarRegistro(op2);
         
        
        // Si op es un numero cargar imediato
        // Si op es un temporal buscarlo en la estrucutura y agregar utilizar el registro asignado
        // Finalmete asignar el temporal a un registro y realizar la operación correspondiente con los registros utilizados
    }

    //Imprimir codigo momentaneamente 
    public String getCodigoMIPS() {
        System.out.println(codigoMips.toString());
        return codigoMips.toString();
    }
    
}
