package semantico.nodos.expresion;

import semantico.TablaSimbolos;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoExpresionParentizada extends NodoExpresion {

    private NodoExpresion nodoExpresion;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoExpresionParentizada(NodoExpresion nodoExpresion, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoExpresion = nodoExpresion;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    public NodoEncadenadoOpt getNodoEncadenadoOpt() {
        return nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts){

        return null;
    }
}
