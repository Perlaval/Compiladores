package semantico.nodos;

import semantico.ErrorSemantico;

import java.util.List;

public class NodoLlamadaMetodo extends Nodo{

    private NodoId nodoId;
    private List<NodoExpresion> listaArg;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoLlamadaMetodo(NodoId nodoId, List<NodoExpresion> listaArg) {
        this.nodoId = nodoId;
        this.listaArg = listaArg;
    }

    public NodoLlamadaMetodo(NodoId nodoId, List<NodoExpresion> listaArg, NodoEncadenadoOpt nodoEncadenadoOpt){
        this.nodoId = nodoId;
        this.listaArg = listaArg;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
