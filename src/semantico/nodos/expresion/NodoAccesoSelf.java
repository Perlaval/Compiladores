package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoAccesoSelf extends NodoExpresion {

    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoAccesoSelf(NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    public NodoEncadenadoOpt getNodoEncadenadoOpt() {
        return nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        return null;
    }
}
