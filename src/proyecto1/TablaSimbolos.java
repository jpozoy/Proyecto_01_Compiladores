/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author pozoj
 */
public class TablaSimbolos {
    Stack<Map<String, Simbolos>> scopes = new Stack<>();

    // Abrir un nuevo scope
    public void abrirScope() {
        scopes.push(new HashMap<>());
    }

    // Cerrar el scope actual
    public void cerrarScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
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
}
