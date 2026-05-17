package semantico.nodos.expresion;

import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoExpresionParentizada extends NodoExpresion {

    private NodoExpresion nodoExpresion;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoExpresionParentizada(NodoExpresion nodoExpresion, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoExpresion = nodoExpresion;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear(){

        return null;
    }
}
