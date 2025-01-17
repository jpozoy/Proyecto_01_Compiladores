/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1;

import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.Symbol;

/**
 *
 * @author pozoj
 */
public class Simbolo {
    String name;  // Lexema
    String type;  // Tipo (int, float, etc.)
    int line;     // Línea de declaración
    int column;   // Columna de declaración
    boolean isFunction; // Indica si es función
    String returnType;  // Tipo de retorno (si es función)
    List<Simbolo> parameters; // Argumentos y sus tipos (si es función)

    public Simbolo(String name, String type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
        this.isFunction = false;
        this.returnType = null;
        this.parameters = new ArrayList<>();
    }

    public void setFunction(String returnType, List<Simbolo> parameters) {
        this.isFunction = true;
        this.returnType = returnType;
        this.parameters = parameters;
    }
    @Override
    public String toString() {
        if (isFunction) {
            return String.format("Funcion: %s, Tipo Retorno: %s, Parametros: %s, Linea: %d, Columna: %d", 
                name, returnType, parameters, line, column);
        } else {
            return String.format("Variable: %s, Tipo: %s, Linea: %d, Columna: %d", 
                name, type, line, column);
        }
    }
}
