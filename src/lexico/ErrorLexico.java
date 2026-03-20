package lexico;
import excepciones.ErrorException;
public class ErrorLexico extends ErrorException{
    public ErrorLexico(int numLinea, int numColumna, String message) {
        super("ERROR: LEXICO\n| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n" + "| LINEA  " + numLinea + " (COLUMNA " + numColumna + ") | " + message + " |");
    }
}
