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
    public static TablaSimbolos tablaActual = null;
    private TablaSimbolos tablaAnterior;
    private Map<String, Simbolo> tabla;
    public static String espaciosPrint = "";
    
    public static List<Simbolo> parametrosEnEspera = new ArrayList<>();
    
    public TablaSimbolos(TablaSimbolos tablaAnterior) {
        tabla = new HashMap<>();
        this.tablaAnterior = tablaAnterior;
    }
    public Simbolo buscarEnTabla(String nombre){
        return tabla.get(nombre);
    }
    public List<Simbolo> getSimbolosTabla () {
        return new ArrayList<>(tabla.values());
    }
    // Abrir un nuevo scope
    public static void abrirScope() {
        tablaActual = new TablaSimbolos(tablaActual);
        
        espaciosPrint = espaciosPrint + "        ";
        System.out.println(espaciosPrint + "Inicio de scope:");
    }

    // Cerrar el scope actual
    public static void cerrarScope() {
        if (tablaActual.tablaAnterior == null) 
            return;

        System.out.println(espaciosPrint + "Fin de scope");
        espaciosPrint = espaciosPrint.replaceFirst("        ", "");    
        
    }

    // Agregar símbolo al scope actual
    public static boolean agregarSimbolo(Simbolo simbolo) {
        if (tablaActual.tabla.get(simbolo.name) != null) {
            throw new IllegalStateException("Error - Identificador duplicado: " +  simbolo.name);
        }
        System.out.println(espaciosPrint + "Se agrego un simbolo: " + simbolo);
        tablaActual.tabla.put(simbolo.name, simbolo);
        return true;
    }

    // Buscar símbolo (desde el scope actual hacia los superiores)
    public static Simbolo buscar(String nombre) {
        for (TablaSimbolos t = tablaActual; t != null; t = t.tablaAnterior) {
            Simbolo simboloEncontrado = t.buscarEnTabla(nombre);
            if (simboloEncontrado != null) {
                return simboloEncontrado;
            }
        }
        return null;
    }
    public static boolean asignarValor(String nombre, String valorNuevo){
        Simbolo simbolo = buscar(nombre);
        if (simbolo == null)
            return false;
        tablaActual.tabla.put(nombre, new Simbolo(valorNuevo, simbolo.type, simbolo.line, simbolo.column));
        return true;
    }

    //Gestión de asociación de parametros
    public static void agregarParametroEnEspera(Simbolo parametro) {
        //System.out.println("Se proceso un parametro en espera:");
        parametrosEnEspera.add(parametro);
    }

    public static List<Simbolo> getParametrosEnEspera() {
        return new ArrayList<>(parametrosEnEspera);
    }

    public static void limpiarParametrosEnEspera() {
        parametrosEnEspera.clear();
    }
}
