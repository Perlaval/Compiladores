package semantico.nodos.expresion;

import semantico.tipos.Tipo;

public class NodoLlamadaConClassOr extends NodoExpresion {

    NodoLlamadaConClassOrRec nodoLlamadaConClassOrRec;

    public NodoLlamadaConClassOr(NodoLlamadaConClassOrRec nodoLlamadaConClassOrRec) {
        this.nodoLlamadaConClassOrRec = nodoLlamadaConClassOrRec;
    }

    @Override
    public Tipo chequear(){

        return null;
    }
}
