package semantico.nodos;

import semantico.ErrorSemantico;

public class NodoLlamadaMetodoEstatico extends Nodo{

    private NodoId nodoId;
    private NodoLlamadaMetodo nodoLL;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoLlamadaMetodoEstatico(NodoId nodoId, NodoLlamadaMetodo nodoLL){
        this.nodoId = nodoId;
        this.nodoLL = nodoLL;
    }

    public NodoLlamadaMetodoEstatico(NodoId nodoId, NodoLlamadaMetodo nodoLL, NodoEncadenadoOpt encadenadoOpt){
        this.nodoId = nodoId;
        this.nodoLL = nodoLL;
        this.nodoEncadenadoOpt = encadenadoOpt;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
