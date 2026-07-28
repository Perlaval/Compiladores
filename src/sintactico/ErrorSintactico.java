package sintactico;
import excepciones.ErrorException;
import lexico.Token;

public class ErrorSintactico extends ErrorException{
    public ErrorSintactico(Token token, String message) {
        super("ERROR: SINTACTICO\n| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n" + "| LINEA  " + token.getFila() + " (COLUMNA " + token.getColumna() + ") | " + message + " |");
    }
}