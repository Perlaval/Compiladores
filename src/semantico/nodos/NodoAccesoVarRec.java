package semantico.nodos;

import semantico.tipos.Tipo;

public class NodoAccesoVarRec extends Nodo{

    public NodoAccesoVarRec(Nodo izq, Nodo der) {
        super(izq, der);
    }


    @Override
    public Tipo chequear() {
        return null;
    }
}
