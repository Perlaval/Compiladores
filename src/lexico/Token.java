package lexico;
//clase creada especificamente para el analizador sintactico
public class Token{
    // pongo string tipo en vez de string token solo porque es mas entendible
    // ya que en el sintactico voy a comparar los tpkens, que en realdiad estoy comparando que recibi el tipo esperado
    String tipo;
    String lexema;
    int fila;
    int columna;

    public Token(String tipo, String lexema, int fila, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

}
