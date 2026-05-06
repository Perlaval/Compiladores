package semantico;
import excepciones.ErrorException;
public class ErrorSemantico extends ErrorException{
    public ErrorSemantico(int numLinea, int numColumna, String message) {
        super("ERROR: SEMANTICO\n| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n" + "| LINEA  " + numLinea + " (COLUMNA " + numColumna + ") | " + message + " |");
    }
}