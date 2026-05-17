package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public class NodoArgumentosActuales extends Nodo {

    private NodoListaExpresionesOpt nodoListaExpresionesOpt;

    public NodoArgumentosActuales(NodoListaExpresionesOpt nodoListaExpresionesOpt) {
        this.nodoListaExpresionesOpt = nodoListaExpresionesOpt;
    }

    public Tipo chequear() throws ErrorSemantico {

        return null;
    }
}
