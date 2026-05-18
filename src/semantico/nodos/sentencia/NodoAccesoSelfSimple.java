package semantico.nodos.sentencia;

import semantico.ErrorSemantico;

public class NodoAccesoSelfSimple extends NodoSentencia{

    // AccesoSelfSimple -> self ListaEncadenadoSimple
    private NodoVarEncadenado nodoSelf;
    private NodoVarEncadenado nodoVarEncadenado;

    public NodoAccesoSelfSimple(NodoVarEncadenado nodoSelf, NodoVarEncadenado nodoVarEncadenado) {
        this.nodoSelf = nodoSelf;
        this.nodoVarEncadenado = nodoVarEncadenado;
    }

    public NodoVarEncadenado getNodoSelf() {
        return nodoSelf;
    }

    public NodoVarEncadenado getNodoVarEncadenado() {
        return nodoVarEncadenado;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
