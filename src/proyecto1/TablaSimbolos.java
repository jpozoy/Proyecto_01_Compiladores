/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author pozoj
 */
public class TablaSimbolos {
    Stack<Map<String, Simbolos>> scopes = new Stack<>();
    private List<Simbolos> parametrosEnEspera = new ArrayList<>();
    
    
    public TablaSimbolos() {
        // Crear un scope global al inicializar
        abrirScope();
    }
    // Abrir un nuevo scope
    public void abrirScope() {
        scopes.push(new HashMap<>());
        //System.out.println("Se abrio un scope");
    }

    // Cerrar el scope actual
    public void cerrarScope() {
        if (scopes.size() > 1) { // Evitar cerrar el scope global
            // Obtener el scope actual antes de cerrarlo
            Map<String, Simbolos> scopeActual = scopes.peek();
            // Imprimir los símbolos del scope actual
            imprimirScope(scopeActual,"Local");
            // Remover el scope actual de la pila
            scopes.pop();
        } else {
            System.out.println("No se puede cerrar el scope global.");
    }
}

    // Agregar símbolo al scope actual
    public boolean agregarSimbolo(Simbolos simbolo) {
        if (scopes.isEmpty()) {
            throw new IllegalStateException("No hay scopes abiertos");
        }
        Map<String, Simbolos> scopeActual = scopes.peek();
        if (scopeActual.containsKey(simbolo.name)) {
            return false; // Símbolo ya declarado en este scope
        }
        scopeActual.put(simbolo.name, simbolo);
        return true;
    }

    // Buscar símbolo (desde el scope actual hacia los superiores)
    public Simbolos buscar(String nombre) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Map<String, Simbolos> scope = scopes.get(i);
            if (scope.containsKey(nombre)) {
                return scope.get(nombre);
            }
        }
        return null; // No encontrado
    }
    //Imprimir la tabla de simbolos
    public void imprimirTabla() {
        System.out.println("Tabla de Simbolos:");
        for (Map<String, Simbolos> scope : scopes) {
            for (Simbolos simbolo : scope.values()) {
                System.out.println(simbolo);
            }
        }
    }
    // Método para imprimir un scope específico
    public void imprimirScope(Map<String, Simbolos> scope, String scopeName) {
        System.out.println("-------------------------------------");
        System.out.println("Tabla de simbolos del scope: " + scopeName);
        for (Simbolos simbolo : scope.values()) {
            System.out.println(simbolo);
        }
        System.out.println("-------------------------------------");
    }
    public void imprimirScopeGlobal() {
        Map<String, Simbolos> scopeActual = scopes.peek();
        imprimirScope(scopeActual, "Global");
    };
    
    //Gestión de asociación de parametros
    public void agregarParametroEnEspera(Simbolos parametro) {
        //System.out.println("Se proceso un parametro en espera:");
        parametrosEnEspera.add(parametro);
    }

    public List<Simbolos> getParametrosEnEspera() {
        return new ArrayList<>(parametrosEnEspera);
    }

    public void limpiarParametrosEnEspera() {
        parametrosEnEspera.clear();
    }
}
