package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;

public class NodoSentenciaSimple extends NodoSentencia{

    private NodoExpresion nodoExpresion;

    public NodoSentenciaSimple(Token token, NodoExpresion nodoExpresion) {
        this.nroLinea = token.getFila();
        this.nroColumna = token.getColumna();
        this.lexema = token.getLexema();
        this.nodoExpresion = nodoExpresion;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
