package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public class NodoListaExpresionesOpt extends Nodo {

    private NodoListaExpresiones nodoListaExpresiones;

    public NodoListaExpresionesOpt(NodoListaExpresiones nodoListaExpresiones) {
        this.nodoListaExpresiones = nodoListaExpresiones;
    }


    public Tipo chequear() throws ErrorSemantico {
        return null;
    }

    /*@Override
    public void chequear() throws ErrorSemantico {

    }*/
}
