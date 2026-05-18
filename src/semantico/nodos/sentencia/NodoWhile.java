package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;

public class NodoWhile extends NodoSentencia{

    private NodoExpresion nodoExpresion;
    private NodoSentencia nodoSentencia;

    public NodoWhile(Token tWhile, NodoExpresion nodoExpresion, NodoSentencia nodoSentencia) {
        this.nroLinea = tWhile.getFila();
        this.nroColumna = tWhile.getColumna();
        this.lexema = tWhile.getLexema();
        this.nodoExpresion = nodoExpresion;
        this.nodoSentencia = nodoSentencia;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    public NodoSentencia getNodoSentencia() {
        return nodoSentencia;
    }

    @Override
    public void chequear() throws ErrorSemantico {}
}
