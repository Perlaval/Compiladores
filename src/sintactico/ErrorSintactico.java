package sintactico;
import excepciones.ErrorException;

public class ErrorSintactico extends ErrorException{
    public ErrorSintactico(int numLinea, int numColumna, String message) {
        super("ERROR: SINTACTICO\n| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n" + "| LINEA  " + numLinea + " (COLUMNA " + numColumna + ") | " + message + " |");
    }
}