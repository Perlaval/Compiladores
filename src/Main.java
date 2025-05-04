import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try{
            String codigoFuente = Files.readString(Paths.get("src/resources/prueba.txt"));
            Lexico analisisLexico = new Lexico(codigoFuente);
            analisisLexico.analizador();
            analisisLexico.ejecutador();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo" + e.getMessage());
        }catch (ErrorLexico e){
            System.out.println(e.getMessage());
        }


    }
}


