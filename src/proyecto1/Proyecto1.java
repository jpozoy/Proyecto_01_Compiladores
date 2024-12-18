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
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;


public class Proyecto1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\nMenu Principal");
            System.out.println("1. Generar Lexer.java");
            System.out.println("2. Generar Parser.java y sym.java");
            System.out.println("3. Procesar archivo de entrada y escribir tokens");
            System.out.println("4. Eliminar archivos generados (Lexer,parser,sym)");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    //System.out.print("Ingrese la ruta del archivo scanner.flex: ");
                    //String rutaLexer = scanner.nextLine();
                    try {
                        generarLexer("src/proyecto1/scanner.flex");
                        System.out.println("Lexer.java generado correctamente.");
                    } catch (Exception e) {
                        System.err.println("Error al generar Lexer.java: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.print("Ingresa la ruta del archivo parser.cup: ");
                    //String rutaParser = scanner.nextLine();
                    try {
                        generarCup("src/proyecto1/cup.cup");
                        System.out.println("parser.java y sym.java generados correctamente.");
                    } catch (Exception e) {
                        System.err.println("Error al generar Parser.java y sym.java: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("Ingresa la ruta del archivo de entrada: ");
                    String rutaEntrada = scanner.nextLine();
                    System.out.print("Ingresa la ruta del archivo de salida: ");
                    String rutaSalida = scanner.nextLine();
                    try {
                        escribirTokens(rutaEntrada, rutaSalida);
                        System.out.println("Tokens procesados y guardados en " + rutaSalida);
                    } catch (Exception e) {
                        System.err.println("Error al procesar el archivo de entrada: " + e.getMessage());
                    }
                    break;
                case "4":
                    try {
                        eliminarArchivos("src/proyecto1/Lexer.java", "src/proyecto1/parser.java", "src/proyecto1/sym.java");
                        System.out.println("Archivos generados eliminados correctamente.");
                    } catch (Exception e) {
                        System.err.println("Error al eliminar archivos: " + e.getMessage());
                    }
                    break;
                case "5":
                    continuar = false;
                    System.out.println("Saliendo del programa...");
                    break;

                default:
                    System.out.println("Opcion no valida. Intenta nuevamente.");
            }
        }

        scanner.close();
    }

    public static void generarLexer(String path) throws Exception {
        String[] arr = {path};
        jflex.Main.generate(arr);
    }

    public static void generarCup(String path) {
    try {
        //
        String outputDir = "src/proyecto1";

        // Ruta al archivo CUP
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo CUP no existe: " + path);
        }
        // Opciones para java_cup con la ruta del directorio de salida
        String[] arr = {"-parser", "parser", "-symbols", "sym", "-destdir", outputDir, path};
        java_cup.Main.main(arr);

        System.out.println("Parser y símbolos generados correctamente en: " + outputDir);
    } catch (FileNotFoundException e) {
        System.err.println("Error: " + e.getMessage());
    } catch (Exception e) {
        System.err.println("Error al generar CUP: " + e.getMessage());
        }
   }

    //Metodo para escribir los tokens en un archvo de salida
    public static void escribirTokens(String rutaArchivo, String rutaSalida) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaSalida))) {
            FileReader reader = new FileReader(rutaArchivo);
            Lexer scanner = new Lexer(reader);
            Symbol token;

            while ((token = scanner.next_token()).sym != sym.EOF) {
                writer.write("Tipo: " + token.sym + ", Lexema: " + token.value +
                             ", Linea: " + token.left + ", Columna: " + token.right);
                writer.newLine();
            }

            System.out.println("Tokens escritos correctamente en " + rutaSalida);
        }
    }
    
    // Método para eliminar los archivos generados
    public static void eliminarArchivos(String... archivos) throws IOException {
        for (String archivo : archivos) {
            Path path = Paths.get(archivo);
            try {
                Files.delete(path);
                System.out.println("Archivo eliminado: " + archivo);
            } catch (IOException e) {
                System.err.println("Error al eliminar archivo: " + archivo + " - " + e.getMessage());
            }
        }
    }
}
