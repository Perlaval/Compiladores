package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.nodos.expresion.NodoExpresion;

public class NodoIf extends NodoSentencia{

    //if (Expresion) SentenciaRec
    //SentenciaRec -> Sentencia(then) RecursivoElse(else)
    private NodoExpresion nodoCondicion;
    private NodoSentencia nodoSentenciaThen;
    private NodoSentencia nodoSentenciaElse;

    public NodoIf(Token token, NodoExpresion nodoCondicion, NodoSentencia nodoSentenciaThen, NodoSentencia nodoSentenciaElse) {
        this.nroLinea = token.getFila();
        this.nroColumna = token.getColumna();
        this.lexema = token.getLexema();
        this.nodoCondicion = nodoCondicion;
        this.nodoSentenciaThen = nodoSentenciaThen;
        this.nodoSentenciaElse = nodoSentenciaElse;
    }

    public NodoExpresion getNodoCondicion() {
        return nodoCondicion;
    }

    public NodoSentencia getNodoSentenciaThen() {
        return nodoSentenciaThen;
    }

    public NodoSentencia getNodoSentenciaElse() {
        return nodoSentenciaElse;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
