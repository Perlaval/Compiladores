package semantico.nodos;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoEncadenado extends Nodo{

    private NodoEncadenadoRec nodoEncadenadoRec;

    public NodoEncadenado(NodoEncadenadoRec nodoEncadenadoRec) {
        this.nodoEncadenadoRec = nodoEncadenadoRec;
        this.tipoSintetizado = nodoEncadenadoRec.tipoSintetizado;
    }

    public Tipo chequear() {

        return null;
    }

    public NodoEncadenadoRec getNodoEncadenadoRec() {
        return nodoEncadenadoRec;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
