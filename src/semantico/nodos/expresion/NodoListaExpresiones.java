package semantico.nodos.expresion;

import semantico.tipos.Tipo;

public class NodoListaExpresiones extends NodoExpresion {

    private NodoExpresion nodoExpresion;
    private NodoListaExpresionesRec nodoListaExpresionesRec;

    public NodoListaExpresiones(NodoExpresion nodoExpresion, NodoListaExpresionesRec nodoListaExpresionesRec) {
        this.nodoListaExpresionesRec = nodoListaExpresionesRec;
        this.nodoExpresion = nodoExpresion;
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
