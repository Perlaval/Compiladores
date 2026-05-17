package semantico.nodos.sentencia;

import semantico.ErrorSemantico;

public class NodoAccesoVarSimple extends NodoSentencia{

    private NodoVarEncadenado id;
    private NodoAccesoVarSimpleRec nodoAccesoVarSimpleRec;

    //1. AccesoVarSimple -> id AccesoVarSimpleRec
    public NodoAccesoVarSimple(NodoVarEncadenado id, NodoAccesoVarSimpleRec nodoAccesoVarSimpleRec) {
        this.id = id;
        this.nodoAccesoVarSimpleRec = nodoAccesoVarSimpleRec;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
