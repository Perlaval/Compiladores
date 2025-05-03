import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try{
            String codigoFuente = Files.readString(Paths.get("src/resources/prueba.txt"));
            Lexico analisisLexico = new Lexico(codigoFuente);
            analisisLexico.analisis();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo" + e.getMessage());        }

    }
}


