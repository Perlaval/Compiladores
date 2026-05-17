package semantico.nodos.expresion;

import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoOperando extends NodoExpresion {

    NodoExpresion nodoExpresion;
    //El nodoExpresion puede ser: nodoLiteral | nodoPrimario EncadenadoOpt

    //1.
    private NodoLiteral nodoLiteral;

    //2.
    private NodoPrimario nodoPrimario;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoOperando(NodoLiteral nodoLiteral) {
        this.nodoLiteral = nodoLiteral;
    }

    public NodoOperando(NodoPrimario nodoPrimario, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoPrimario = nodoPrimario;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
