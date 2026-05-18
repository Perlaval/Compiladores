package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoLlamadaConClassOr extends NodoExpresion {

    NodoLlamadaConClassOrRec nodoLlamadaConClassOrRec;

    public NodoLlamadaConClassOr(Token tnew, NodoLlamadaConClassOrRec nodoLlamadaConClassOrRec) {
        this.nroLinea = tnew.getFila();
        this.nroColumna = tnew.getColumna();
        this.lexema = tnew.getLexema();
        this.nodoLlamadaConClassOrRec = nodoLlamadaConClassOrRec;
    }

    public NodoLlamadaConClassOrRec getNodoLlamadaConClassOrRec() {
        return nodoLlamadaConClassOrRec;
    }

    @Override
    public Tipo chequear(){

        return null;
    }
}
