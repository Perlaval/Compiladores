package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoAccesoSelf extends NodoExpresion {

    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoAccesoSelf(NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear() throws ErrorSemantico {

        return null;
    }
}
