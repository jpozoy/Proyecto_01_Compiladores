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
            this.text = new StringBuilder(".text\nmain:\n");
            this.gestorRegistros = new GestorRegistros();
    }
    public void procesarTac() {
        //Separar lineas de codigo intermedio
        String[] lines = codigoIntermedio.split("\n");
        for (String line : lines) {
            procesarLinea(line);
        }
        //Concatenar salida al sistema
        String exitCall = "li $v0, 10\nsyscall";
        this.text.append(exitCall);

        //Concatenera sección .data y .text al final
        this.codigoMips.append(data);
        this.codigoMips.append(text);
        gestorRegistros.mostrarRegistros();
    }
    //Procesar cada linea
    public void procesarLinea(String line) {
        //Si es un string
        if(line.startsWith("string:")){
            procString(line);
        //Si es una etiqueta de funcion
        } else if (line.matches("^_[a-zA-Z0-9_]+:$")) {
            text.append(line + "\n");
        } else if(line.startsWith("int:")) {
            procDecAsing(line);
        }
        else if (line.matches("^t[0-9]+=.*$")) {
            procTemp(line);
        }
    };
    //Procesar cada línea independiente
    public void procString(String line) {
        String[] partes = line.split("=", 2);
        String nombre = partes[0].replace("string:", "").trim(); // Tomar el nombre y quitar "string:"
        String valor = (partes.length > 1) ? "\"" + partes[1].trim() + "\"" : "\"\""; // Envolver en comillas

        String newline = nombre + ":  .asciiz  " + valor + "\n";  
        data.append(newline);
    }
    //Procesar una Declaración o asignacion de enteros de momento
    public void procDecAsing(String line) {
        String[] partes = line.split(":|="); // Divide por ":" o "="
        
        if (partes.length < 3) return; // Evitar errores en líneas incorrectas

        String nombre = partes[1].trim(); // Obtiene el nombre de la variable
        String valor = partes[2].trim();  // Obtiene el valor
        
        //Declaracion de espacio en memoria
        // Solo procesa si el valor es un número (ignora registros temporales como t0, t1...)
        if (valor.matches("\\d+")) {
            String newline = nombre + ": .word " + valor + "\n";
            data.append(newline);
        } else { //Procesar cuando el valor = t0, t1, ... (Una asignación de memoria) "int:_resultado_=t0"
            //Inicializar registro en .data
            String newlineData = nombre + ": .word " + 0 + "\n"; //Inicializar el espacio en 0
            data.append(newlineData); //Se declara la memoria en el .data, manejar repetidos!!!
            String reg = gestorRegistros.obtenerRegistroPorEtiqueta(valor); //Obtener el registro asociado al temp
            String newlineText = "sw " + reg +", "+ nombre + "\n";
            text.append(newlineText);
            //Liberar el registro 
            gestorRegistros.liberarRegistro(reg);
        }
    }
    
    public void procTemp(String line){
        System.out.println("Se Proceso un temporal");
        System.out.println(line);
        String[] partes = line.split("="); // Divide por "="
        String etiqueta = partes[0].trim(); // "t0", "t1",
        String expresion = partes[1].trim(); //Expresion
        
        // Detectar operador (+, -, *, /, %)
        Pattern pattern = Pattern.compile("(.+?)([+\\-*/%])(.+)");
        Matcher matcher = pattern.matcher(expresion);
        if (matcher.matches()) {
            String operando1 = matcher.group(1).trim(); // Primer operando
            String operador = matcher.group(2).trim();  // Operador
            String operando2 = matcher.group(3).trim(); // Segundo operando

            //System.out.println("Etiqueta Temporal: " + etiqueta);
            //System.out.println("Operando 1: " + operando1);
            //System.out.println("Operador: " + operador);
            //System.out.println("Operando 2: " + operando2);
            traducirTemp(etiqueta, operando1, operando2, operador);
        } else {
            System.out.println("Formato inválido: " + line);
        }
    }
    public void traducirTemp(String temporal, String op1, String op2, String operador) {
        String reg1 = obtenerRegistro(op1);
        String reg2 = obtenerRegistro(op2);
        String regRes = gestorRegistros.asignarRegistroTemp(temporal);

        if (regRes == null) {
            System.out.println("Error: No hay registros disponibles.");
            return;
        }

        generarOperacionMIPS(regRes, reg1, reg2, operador);

        // Liberar registros temporales si no son el resultado final
        gestorRegistros.liberarRegistro(reg1);
        gestorRegistros.liberarRegistro(reg2);
    }

    //Obtener registro sea de un numero, temporal o direccion en memoria
    private String obtenerRegistro(String operando) {
        String registro;

        if (operando.matches("^-?\\d+$")) { // Si es un número
            registro = gestorRegistros.asignarRegistro();
            //Escribit la carga de un numero (l1 t$, #)
            text.append("li ").append(registro).append(", ").append(operando).append("\n");
        } else if (operando.matches("^t\\d+$")) { // Si es un temporal
            registro = gestorRegistros.obtenerRegistroPorEtiqueta(operando);
        } else { // Si es un identificador
            registro = gestorRegistros.asignarRegistroTemp(operando);
            //Escribit la carga de un numero (lw t$, _nombreVariable)
            text.append("lw ").append(registro).append(", ").append(operando).append("\n");
        }

        return registro;
    }

    //Generar operación mips correspondiente
    private void generarOperacionMIPS(String regRes, String reg1, String reg2, String operador) {
        switch (operador) {
            case "+":
                text.append("add ").append(regRes).append(", ").append(reg1).append(", ").append(reg2).append("\n");
                break;
            case "-":
                text.append("sub ").append(regRes).append(", ").append(reg1).append(", ").append(reg2).append("\n");
                break;
            case "*":
                text.append("mul ").append(regRes).append(", ").append(reg1).append(", ").append(reg2).append("\n");
                break;
            case "/":
                text.append("div ").append(reg1).append(", ").append(reg2).append("\n")
                    .append("mflo ").append(regRes).append("\n");
                break;
            case "%":
                text.append("div ").append(reg1).append(", ").append(reg2).append("\n")
                    .append("mfhi ").append(regRes).append("\n");
                break;
            default:
                System.out.println("Error: Operador no reconocido.");
        }
    }

    //Imprimir codigo momentaneamente 
    public String getCodigoMIPS() {
        System.out.println(codigoMips.toString());
        return codigoMips.toString();
    }
    
}
