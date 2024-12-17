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

public class Proyecto1 {
    // GENERAR LEXER: C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\dist\lib>java -jar jflex-full-1.9.1.jar "C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\src\proyecto1\scanner.flex"
    
    public static void main(String[] args) throws Exception{
//        generarLexer("C:/Users/gerar/OneDrive/AAA TEC/Verano 2024/Proyecto_01_Compiladores/src/proyecto1/scanner.flex");
//        generarCup("C:/Users/gerar/OneDrive/AAA TEC/Verano 2024/Proyecto_01_Compiladores/src/proyecto1/cup.cup");
        // java -jar "C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\dist\lib\java-cup-11b.jar" "C:\Users\gerar\OneDrive\AAA TEC\Verano 2024\Proyecto_01_Compiladores\src\proyecto1\cup.cup"
        probarLexer();
    }

    
    public static void generarLexer(String path) throws IOException, Exception {
        String[] arr = {path};
        jflex.Main.generate(arr);
    }
    public static void generarCup(String path) throws IOException, Exception {
        String[] arr = {path};
        java_cup.Main.main(arr);
    }
    
    public static void probarLexer(){
        try {
            // Asegúrate de que la ruta al archivo de entrada esté correcta.
            String input = "elfo navidad d _verano_  _hola_ 98 7.3 true 'c'"; // Prueba de ejemplo, o usa un archivo de texto

            // Crea un StringReader con el texto de entrada (puedes reemplazarlo con un archivo si lo prefieres)
            StringReader reader = new StringReader(input);

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
}