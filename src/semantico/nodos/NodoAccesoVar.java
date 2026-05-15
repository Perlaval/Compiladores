package semantico.nodos;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoAccesoVar extends Nodo{

    public NodoAccesoVar() {
        super();
    }

    @Override
    public Tipo chequear() {
        return null;
    }
}
