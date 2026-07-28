package semantico;
import excepciones.ErrorException;
import lexico.Token;

public class ErrorSemantico extends ErrorException{
    public ErrorSemantico(Token token, String message) {
        super("ERROR: SEMANTICO\n| NUMERO DE LINEA (NUMERO DE COLUMNA) | DESCRIPCION: |\n" + "| LINEA  " + token.getFila() + " (COLUMNA " + token.getColumna() + ") | " + message + " |");
    }
}