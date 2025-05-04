public class ErrorLexico extends ErrorException{
    public ErrorLexico(int numLinea, int numColumna, String message) {
        super("ERROR: LEXICO\n| NÚMERO DE LÍNEA (NÚMERO DE COLUMNA) | DESCRIPCIÓN: |\n" + "| LINEA  " + numLinea + " (COLUMNA " + numColumna + ") | " + message + " |");
    }
}
