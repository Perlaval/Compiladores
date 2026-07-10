package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public class NodoAccesoVarSimpleRec extends NodoSentencia{

    //1. AccesoVarSimpleRec -> id2.i3..
    private NodoVarEncadenado nodoVarEncadenado;

    //2. AccesoVarSimpleRec -> [Expresion]
    private NodoExpresion nodoExpresion;

    public NodoAccesoVarSimpleRec(NodoVarEncadenado varEncadenado) {
        this.nodoVarEncadenado = varEncadenado;
    }

    public NodoAccesoVarSimpleRec(NodoExpresion nodoExpresion) {
        this.nodoExpresion = nodoExpresion;
    }

    public NodoVarEncadenado getNodoVarEncadenado() {
        return nodoVarEncadenado;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        return null;
    }
}
