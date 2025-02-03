package proyecto1;

import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.Symbol;

// Clase que representa un símbolo en la tabla de símbolos, con su nombre, tipo, valor, etc.
public class Simbolo {
    String name;  // Lexema
    String value; // Valor (si es constante)
    String type;  // Tipo (int, float, etc.)
    int line;     // Línea de declaración
    int column;   // Columna de declaración
    boolean isFunction; // Indica si es función
    String returnType;  // Tipo de retorno (si es función)
    List<Simbolo> parameters; // Argumentos y sus tipos (si es función)
    boolean isArray; // Indica si es un arreglo
    int arraySize; // Tamaño del arreglo    

    public Simbolo(String name, String type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
        this.isFunction = false;
        this.returnType = null;
        this.value = null; 
        this.parameters = new ArrayList<>();
    }
    public List<Simbolo> getParameters() {
        return parameters;
    }
    public void setFunction(String returnType, List<Simbolo> parameters) {
        this.isFunction = true;
        this.returnType = returnType;
        this.type = returnType;
        this.parameters = parameters;
    }

    public void setArray() {
        this.isArray = true;
    }
    public boolean isArray() {
        return isArray;
    }
    public void setArraySize(int size) {
        this.arraySize = size;
    }
    public boolean isFunction() {
        return isFunction;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
    @Override
    public String toString() {
        if (isFunction) {
            return String.format("Funcion: %s, Tipo Retorno: %s, Parametros: %s, Linea: %d, Columna: %d", 
                name, returnType, parameters, line, column);
        } else {
            return String.format("Variable: %s, Tipo: %s, Linea: %d, Columna: %d, Valor: %s", 
                name, type, line, column, value);
        }
    }
}
