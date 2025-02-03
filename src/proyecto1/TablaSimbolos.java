package proyecto1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * La clase TablaSimbolos representa una tabla de símbolos para un compilador,
 * que permite gestionar diferentes scopes (ámbitos) y almacenar símbolos con sus
 * respectivos valores y tipos.
 */
public class TablaSimbolos {
    /**
     * La tabla de símbolos actual.
     */
    public static TablaSimbolos tablaActual = new TablaSimbolos(null);

    /**
     * La tabla de símbolos anterior (scope superior).
     */
    private TablaSimbolos tablaAnterior;

    /**
     * Mapa que almacena los símbolos en el scope actual.
     */
    private Map<String, Simbolo> tabla;

    /**
     * Espacios utilizados para la impresión de la tabla de símbolos.
     */
    public static String espaciosPrint = "";

    /**
     * Lista de parámetros en espera de ser procesados.
     */
    public static List<Simbolo> parametrosEnEspera = new ArrayList<>();

    /**
     * Constructor que inicializa la tabla de símbolos con la tabla anterior.
     * 
     * @param tablaAnterior La tabla de símbolos del scope superior.
     */
    public TablaSimbolos(TablaSimbolos tablaAnterior) {
        tabla = new HashMap<>();
        this.tablaAnterior = tablaAnterior;
    }

    /**
     * Busca un símbolo en la tabla de símbolos actual.
     * 
     * @param nombre El nombre del símbolo a buscar.
     * @return El símbolo encontrado, o null si no se encuentra.
     */
    public Simbolo buscarEnTabla(String nombre) {
        return tabla.get(nombre);
    }

    /**
     * Obtiene una lista de todos los símbolos en la tabla de símbolos actual.
     * 
     * @return Una lista de símbolos.
     */
    public List<Simbolo> getSimbolosTabla() {
        return new ArrayList<>(tabla.values());
    }

    /**
     * Abre un nuevo scope (ámbito) creando una nueva tabla de símbolos.
     */
    public static void abrirScope() {
        tablaActual = new TablaSimbolos(tablaActual);
        espaciosPrint = espaciosPrint + "        ";
        Proyecto1.tablaSimbolos += espaciosPrint + "Inicio de scope:\n";
    }

    /**
     * Cierra el scope (ámbito) actual y vuelve al scope superior.
     */
    public static void cerrarScope() {
        if (tablaActual.tablaAnterior == null)
            return;
        tablaActual = tablaActual.tablaAnterior;
        Proyecto1.tablaSimbolos += espaciosPrint + "Fin de scope\n";
        espaciosPrint = espaciosPrint.replaceFirst("        ", "");
    }

    /**
     * Agrega un símbolo al scope (ámbito) actual.
     * 
     * @param simbolo El símbolo a agregar.
     * @return true si el símbolo se agregó correctamente, false en caso contrario.
     */
    public static boolean agregarSimbolo(Simbolo simbolo) {
        Simbolo simboloRevisar = tablaActual.tabla.get(simbolo.name);
        if (tablaActual.tabla.get(simbolo.name) != null && simboloRevisar.line != simbolo.line && simboloRevisar.column != simbolo.column) {
            System.err.println("Error - Identificador duplicado: " + simbolo.name + " en linea " + simbolo.line + " ya existe en linea: " + simboloRevisar.line);
        }
        Proyecto1.tablaSimbolos += espaciosPrint + "Se agrego un simbolo: " + simbolo + "\n";
        tablaActual.tabla.put(simbolo.name, simbolo);
        return true;
    }

    /**
     * Busca un símbolo desde el scope (ámbito) actual hacia los superiores.
     * 
     * @param nombre El nombre del símbolo a buscar.
     * @return El símbolo encontrado, o null si no se encuentra.
     */
    public static Simbolo buscar(String nombre) {
        for (TablaSimbolos t = tablaActual; t != null; t = t.tablaAnterior) {
            Simbolo simboloEncontrado = t.buscarEnTabla(nombre);
            if (simboloEncontrado != null) {
                return simboloEncontrado;
            }
        }
        return null;
    }

    /**
     * Asigna un nuevo valor a un símbolo existente.
     * 
     * @param nombre El nombre del símbolo.
     * @param valorNuevo El nuevo valor a asignar.
     * @return true si el valor se asignó correctamente, false en caso contrario.
     */
    public static boolean asignarValor(String nombre, String valorNuevo) {
        Simbolo simbolo = buscar(nombre);
        if (simbolo == null)
            return false;
        tablaActual.tabla.put(nombre, new Simbolo(valorNuevo, simbolo.type, simbolo.line, simbolo.column));
        return true;
    }

    /**
     * Agrega un parámetro en espera de ser procesado.
     * 
     * @param parametro El parámetro a agregar.
     */
    public static void agregarParametroEnEspera(Simbolo parametro) {
        parametrosEnEspera.add(parametro);
    }

    /**
     * Obtiene una lista de los parámetros en espera de ser procesados.
     * 
     * @return Una lista de parámetros.
     */
    public static List<Simbolo> getParametrosEnEspera() {
        return new ArrayList<>(parametrosEnEspera);
    }

    /**
     * Limpia la lista de parámetros en espera.
     */
    public static void limpiarParametrosEnEspera() {
        parametrosEnEspera.clear();
    }
}
