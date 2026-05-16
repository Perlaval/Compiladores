package semantico.nodos;

import semantico.ErrorSemantico;

public class NodoEncadenadoRec extends Nodo{

    private NodoLlamadaMetodo nodoLlamadaMetodo;
    private NodoAccesoVar nodoAccesoVar;

    public NodoEncadenadoRec(NodoLlamadaMetodo nodoLlamadaMetodo) {
        this.nodoLlamadaMetodo = nodoLlamadaMetodo;
        this.tipoSintetizado = nodoLlamadaMetodo.tipoSintetizado;
    }

    public NodoEncadenadoRec(NodoAccesoVar nodoAccesoVar) {
        this.nodoAccesoVar = nodoAccesoVar;
        this.tipoSintetizado = nodoAccesoVar.tipoSintetizado;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }

    public NodoLlamadaMetodo getNodoLlamadaMetodo() {
        return nodoLlamadaMetodo;
    }

    public void setNodoLlamadaMetodo(NodoLlamadaMetodo nodoLlamadaMetodo) {
        this.nodoLlamadaMetodo = nodoLlamadaMetodo;
    }

    public NodoAccesoVar getNodoAccesoVar() {
        return nodoAccesoVar;
    }

    public void setNodoAccesoVar(NodoAccesoVar nodoAccesoVar) {
        this.nodoAccesoVar = nodoAccesoVar;
    }
}
