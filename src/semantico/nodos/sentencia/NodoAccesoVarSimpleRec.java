package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;

public class NodoAccesoVarSimpleRec extends NodoSentencia{

    //1. AccesoVarSimpleRec -> id2.i3..
    private NodoVarEncadenado varEncadenado;

    //2. AccesoVarSimpleRec -> [Expresion]
    private NodoExpresion nodoExpresion;

    public NodoAccesoVarSimpleRec(NodoVarEncadenado varEncadenado) {
        this.varEncadenado = varEncadenado;
    }

    public NodoAccesoVarSimpleRec(NodoExpresion nodoExpresion) {
        this.nodoExpresion = nodoExpresion;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
