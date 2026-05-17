package semantico.nodos;

import semantico.nodos.expresion.NodoAccesoVar;
import semantico.nodos.expresion.NodoLlamadaMetodo;
import semantico.tipos.Tipo;

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

    public Tipo chequear() {

        return null;
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
