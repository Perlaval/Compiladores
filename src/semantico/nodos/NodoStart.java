package semantico.nodos;

import lexico.Token;

public class NodoStart extends Nodo{

    private NodoBloqueMetodo nodoBloqueMetodo;

    public NodoStart(Token tStart, NodoBloqueMetodo nodoBloqueMetodo) {
        this.nroLinea = tStart.getFila();
        this.nroColumna = tStart.getColumna();
        this.lexema = tStart.getLexema();
        this.nodoBloqueMetodo = nodoBloqueMetodo;
    }

    public NodoBloqueMetodo getBloqueMetodo() {
        return nodoBloqueMetodo;
    }
}
