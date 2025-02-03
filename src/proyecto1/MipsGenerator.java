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
    // Lista temporal para almacenar líneas de pop
    private List<String> popLines;
    
    //Encontrar y reservar estructuras estaticas: Strings, Arrays
    
    public MipsGenerator(String codigoIntermedio) {
            this.codigoIntermedio = codigoIntermedio;
            this.codigoMips = new StringBuilder();
            this.data = new StringBuilder(".data\n");
            this.text = new StringBuilder(".text\nmain:\n");
            this.gestorRegistros = new GestorRegistros();
            this.popLines = new ArrayList<>();
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
        //Procesar parametros si hay en la lista y ya no se encuentra más el patron de parametro
        if(!popLines.isEmpty() && !line.matches("^_[a-zA-Z0-9_]+_=pop$")) {
            System.out.println("Se van a procesar los parametros");
            procPops();
        }
        //Procesar la declaración de string:
        if(line.startsWith("string:")){
            procString(line);
        }
        //Si es una etiquea L1, L2, L3....
        else if (line.startsWith("^L\\d+:$")){
            text.append(line + "\n");
        }
        //Procesar un salto "goto"
        else if (line.startsWith("goto")){
            text.append(line);
        }
        //Procesar una etiqueta de funcion
        else if (line.matches("^_[a-zA-Z0-9_]+:$")) {
            text.append(line + "\n");
        }
        //Procesar declaracion de booleano 
        else if (line.startsWith("bool:")) {
            procDecBool(line);
        }
        //Procesar declaracion y asignación de enteros
        else if(line.startsWith("int:")) {
            procDecAsing(line);
        } 
        //Procesar la obtención de retorno
        else if (line.matches("t\\d+=\\$ret")) {
           //System.out.println("Se obtuvo un return: " + line);
           procGetReturn(line);
        }
        //Procesar Temporal
        else if (line.matches("^t[0-9]+=.*$")) {
            procTemp(line);
        //Procesar al recibir un de un parametro
        } 
        else if (line.matches("^_[a-zA-Z0-9_]+_=pop$")) {
            popLines.add(line);
        }  
        //Procesar llamada de función
        else if (line.matches("^call _[a-zA-Z0-9_]+_$")) {
            procCall(line);
        }
        //Procesar pase de parametros
        else if (line.startsWith("push")) {
            procPush(line);
        }
        //Procesar un return
        else if (line.startsWith("return")) {
            procReturn(line);
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
            traducirTemp(etiqueta, operando1, operando2, operador);
        } else {
            System.out.println("Formato inválidos: " + line);
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

    private void procPops() {
        for (int i = popLines.size() - 1; i >= 0; i--) {
            String line = popLines.get(i);
            System.out.println("<<<"+line);
            //Obtener solo la el _identificador_ de la linea
            Pattern pattern = Pattern.compile("^(_[a-zA-Z0-9_]+)_=pop$");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String identificador = matcher.group(1); // Captura solo el identificador
                String registro = gestorRegistros.asignarRegistroTemp(identificador); //Reservar un registro disponible para el
                //Cargar parametro de pila en el registro y liberar el espacio
                String newline = "lw "+registro+", 0($sp)\naddi $sp, $sp, 4\n";
                text.append(newline);
                //Guardar el paramatro en el epacio en memoria del identificador 
                String newlineLoad = "sw "+ registro + ", " + identificador + "\n";
                text.append(newlineLoad);
                gestorRegistros.liberarRegistro(registro);
            } else {
                System.out.println("No coincide el patrón: _nombre_ en procPops");
            }
            
        }
        // Vaciar la lista después de procesar
        popLines.clear();
    }
    
    public void procPush(String line) {
        System.out.println("Se Proceso un push: ###"+line);
        Pattern pattern = Pattern.compile("int:(-?\\d+)"); // Buscar "int:" seguido de números
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            System.out.println(matcher.group(1)); // Imprime "t28"
            String reg = gestorRegistros.asignarRegistro();
            text.append("li ").append(reg).append(", ").append(matcher.group(1)).append("\n");
            String newline = "addi $sp, $sp, -4\nsw " + reg + ", 0($sp)\n";
            text.append(newline);
            //Liberar registro
            gestorRegistros.liberarRegistro(reg);
        }
    }

    public void procCall(String line) {
        System.out.println("Se Proceso una llamada a funcion: "+line);
        Pattern pattern = Pattern.compile("call (_[a-zA-Z0-9_]+_)");
        Matcher matcher = pattern.matcher(line);
        
        if (matcher.find()) {
            String nombreFuncion = matcher.group(1); // Extrae el nombre de la función
            System.out.println("Nombre de la función: " + nombreFuncion);
            String newline = "jal " + nombreFuncion + "\n";
            text.append(newline);
        } else {
            System.out.println("No se encontró el patrón.");
        }
    }

    

    public void procReturn(String line) {
        String resultado, reg;
        System.out.println("Se Proceso un return: "+line);
        // Expresión regular para identificar "t#" (donde # es un número)
        Pattern pattern = Pattern.compile("t\\d+");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            resultado = matcher.group();
            reg = gestorRegistros.obtenerRegistroPorEtiqueta(resultado);
            //El retorno se devolvera en el registro $v0
            String newLineText = "add $v0, "+ reg +", $zero\njr $ra\n"; //Mover el registro donde se guardo el retorno, al registro v0
            text.append(newLineText); 
            gestorRegistros.liberarRegistro(reg); //Liberar el registro pasado a $v0
        }
    }
    
    public void procGetReturn(String line) {
        Pattern pattern = Pattern.compile("(t\\d+)=([\\$a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String temp = matcher.group(1); // Extrae "t7"
            String valor = matcher.group(2); // Extrae "$ret"
            //Asignar un registro al temporal que contiene el return
            String reg = gestorRegistros.asignarRegistroTemp(temp);
            String newline = "move, " + reg + ", $v0\n";
            text.append(newline);

            System.out.println("Registro: " + temp);
            System.out.println("Valor: " + valor);
        } else {
            System.out.println("No coincide con el patrón: Return");
        }
    }

    public void procDecBool(String line) {
         // Expresión regular
        String regex = "bool:([a-zA-Z0-9_]+)=([true|false]+)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            String identificador = matcher.group(1);  // El nombre del identificador
            String valor = matcher.group(2);  // El valor (true o false)
            String newlineData = "";
            String newlineText = "";
            
            System.out.println("Identificador: " + identificador);
            System.out.println("Valor: " + valor);
        } else {
            System.out.println("No se encontró el patrón: Booleano");
        }
    }

    //Imprimir codigo momentaneamente 
    public String getCodigoMIPS() {
        System.out.println(codigoMips.toString());
        return codigoMips.toString();
    }
    
}
