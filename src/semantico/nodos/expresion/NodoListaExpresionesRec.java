package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public class NodoListaExpresionesRec extends Nodo {

    private NodoListaExpresiones nodoListaExpresiones;

    public NodoListaExpresionesRec(NodoListaExpresiones nodoListaExpresiones) {
        this.nodoListaExpresiones = nodoListaExpresiones;
    }


    public Tipo chequear() throws ErrorSemantico {

        return null;
    }
}
