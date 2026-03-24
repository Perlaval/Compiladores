import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.List;
import lexico.Lexico;
import lexico.ErrorLexico;
import lexico.Token;
import sintactico.Sintactico;
import sintactico.ErrorSintactico;

public class Main {
    public static void main(String[] args) {


        /*try{
            String codigoFuente = Files.readString(Paths.get("src/resources/prueba.txt"));
            Lexico analisisLexico = new Lexico(codigoFuente);
            analisisLexico.analizador();
            analisisLexico.ejecutador();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo" + e.getMessage());
        }catch (ErrorLexico e){
            System.out.println(e.getMessage());
        }*/

        if (args.length == 0) {
            System.out.println("Debe indicar un archivo fuente.");
            return;
        }

        try {

            String codigoFuente = Files.readString(Paths.get(args[0]));

            Lexico analisisLexico = new Lexico(codigoFuente);
            analisisLexico.analizador();
            analisisLexico.ejecutador();

            // obtengo los tokens
            List<Token> tokens = analisisLexico.getTokens();
            // esto usado para probar como va funcionando
            /*
            System.out.println("Lista de tokens para usar con el sintactico");
            for (Token t: tokens){
                System.out.println(t.getTipo());
            }*/



            Sintactico analisisSintactico = new Sintactico(tokens);
            analisisSintactico.analizador();
            System.out.println("CORRECTO: ANALISIS SINTACTICO");

        } catch (IOException e) {
            System.out.println("Error al leer el archivo " + e.getMessage());
        } catch (ErrorLexico e) {
            System.out.println(e.getMessage());
        } catch (ErrorSintactico e) {
            System.out.println(e.getMessage());
        }
    }


}


