package semantico.nodos;

import semantico.tipos.Tipo;

public class NodoEncadenado extends Nodo{

    private NodoEncadenadoRec nodoEncadenadoRec;

    public NodoEncadenado(NodoEncadenadoRec nodoEncadenadoRec) {
        this.nodoEncadenadoRec = nodoEncadenadoRec;
        this.tipoSintetizado = nodoEncadenadoRec.tipoSintetizado;
    }

    @Override
    public void chequear() {

    }

    public NodoEncadenadoRec getNodoEncadenadoRec() {
        return nodoEncadenadoRec;
    }

    public void setNodoEncadenadoRec(NodoEncadenadoRec nodoEncadenadoRec) {
        this.nodoEncadenadoRec = nodoEncadenadoRec;
    }
}
