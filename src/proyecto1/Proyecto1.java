package proyecto1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java_cup.runtime.Symbol.*;
import java_cup.internal_error;
import java_cup.runtime.Symbol;
import jflex.exceptions.SilentExit;
import proyecto1.Lexer;
import proyecto1.sym;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader; 
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


public class Proyecto1 {
    // GENERAR LEXER: C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\dist\lib>java -jar jflex-full-1.9.1.jar "C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\src\proyecto1\scanner.flex"
    
    public static void main(String[] args) throws Exception{
        Path rutaArchivo = Paths.get("src", "pruebas", "p1.txt");
//        generarLexer("C:/Users/gerar/OneDrive/AAA TEC/Verano 2024/Proyecto_01_Compiladores/src/proyecto1/scanner.flex");
//        generarCup("C:/Users/gerar/OneDrive/AAA TEC/Verano 2024/Proyecto_01_Compiladores/src/proyecto1/cup.cup");
        // java -jar "C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\dist\lib\java-cup-11b.jar" "C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\src\proyecto1\cup.cup"
        //probarLexer("../pruebas/p1.txt");
         // Verifica si el archivo existe antes de probar el lexer
        if (!Files.exists(rutaArchivo)) {
            System.err.println("El archivo no existe: " + rutaArchivo.toAbsolutePath());
            return;
        }

        try {
            probarLexer(rutaArchivo.toString());
            // Escribir tokens en archivo
            escribirTokens(rutaArchivo.toString(), "src/pruebas/salida.txt");
        } catch (Exception e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
        }
    }

    
    public static void generarLexer(String path) throws IOException, Exception {
        String[] arr = {path};
        jflex.Main.generate(arr);
    }
    public static void generarCup(String path) throws IOException, Exception {
        String[] arr = {path};
        java_cup.Main.main(arr);
    }
    
    public static void probarLexer(String rutaArchivo){
        try {
            // Asegúrate de que la ruta al archivo de entrada esté correcta.
            //String input = "elfo navidad d _verano_  _hola_ 98 7.3 true 'c'"; // Prueba de ejemplo, o usa un archivo de texto

            // Crea un StringReader con el texto de entrada (puedes reemplazarlo con un archivo si lo prefieres)
            FileReader reader = new FileReader(rutaArchivo);

            // Crea el analizador léxico (scanner) usando el lexer generado
            Lexer scanner = new Lexer(reader); // Esto debería coincidir con el nombre de tu lexer generado

            // Lee los tokens generados
            Symbol token;
            while ((token = scanner.next_token()).sym != sym.EOF) {
                System.out.println("Token: " + token.sym + " - " + token.value);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }
    
    public static void escribirTokens(String rutaArchivo, String rutaSalida) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida))) {
        FileReader reader = new FileReader(rutaArchivo);
        Lexer scanner = new Lexer(reader); 

        Symbol token;
        // Escribir cada token en el archivo
        while ((token = scanner.next_token()).sym != sym.EOF) {
            String tipoToken = sym.terminalNames[token.sym]; 
            String lexema = token.value != null ? token.value.toString() : "null";
            int linea = token.left + 1; // se le suma 1 `yyline`
            int columna = token.right + 1; // se le suma 1 a `yycolumn`

            writer.write("Tipo de Token: " + tipoToken + ", Lexema: " + lexema +
                         ", Línea: " + linea + ", Columna: " + columna);
            writer.newLine();
        }
        System.out.println("Los tokens se han guardado correctamente en " + rutaSalida);
    } catch (IOException e) {
        System.err.println("Error al escribir en el archivo: " + e.getMessage());
    } catch (Exception e) {
        System.err.println("Error al analizar los tokens: " + e.getMessage());
    }
  }
}