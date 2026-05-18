package semantico.nodos.sentencia;

import semantico.ErrorSemantico;

public class NodoAccesoVarSimple extends NodoSentencia{

    private NodoVarEncadenado nodoId;
    private NodoAccesoVarSimpleRec nodoAccesoVarSimpleRec;

    //1. AccesoVarSimple -> id AccesoVarSimpleRec
    public NodoAccesoVarSimple(NodoVarEncadenado id, NodoAccesoVarSimpleRec nodoAccesoVarSimpleRec) {
        this.nodoId = id;
        this.nodoAccesoVarSimpleRec = nodoAccesoVarSimpleRec;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }

    public NodoVarEncadenado getNodoId() {
        return nodoId;
    }

    public NodoAccesoVarSimpleRec getNodoAccesoVarSimpleRec() {
        return nodoAccesoVarSimpleRec;
    }
}
