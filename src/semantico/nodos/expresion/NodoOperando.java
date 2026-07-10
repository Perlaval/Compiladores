package semantico.nodos.expresion;

import semantico.TablaSimbolos;
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
        System.out.println("Creo NodoOperando con: "
                + (nodoLiteral == null ? "NULL" : nodoLiteral.getClass().getSimpleName()));
        this.nodoLiteral = nodoLiteral;
    }

    public NodoOperando(NodoPrimario nodoPrimario, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoPrimario = nodoPrimario;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) {

        if (nodoLiteral != null) {
            return nodoLiteral.chequear(ts);
        }
/*
        if (nodoPrimario != null) {
            Tipo tipo = nodoPrimario.chequear(ts);

            if (nodoEncadenadoOpt != null) {
                tipo = nodoEncadenadoOpt.chequear(ts, tipo);
            }

            return tipo;
        }
*/
        return null;
    }
}
