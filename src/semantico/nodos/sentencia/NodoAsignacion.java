package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;

public class NodoAsignacion extends NodoSentencia{


    private NodoAccesoVarSimple nodoAcceso;
    private NodoExpresion nodoExpresion;
    private NodoAccesoSelfSimple nodoAccesoSelfSimple;

    //1. Asignacion -> AccesoVarSimple = Expresion
    public NodoAsignacion(NodoAccesoVarSimple nodoAcceso, NodoExpresion nodoExpresion) {
        this.nodoAcceso = nodoAcceso;
        this.nodoExpresion = nodoExpresion;
    }

    //2. Asignacion -> AccesoSelfSimple = Expresion
    public NodoAsignacion(NodoAccesoSelfSimple nodoAccesoSelfSimple, NodoExpresion nodoExpresion) {
        this.nodoAccesoSelfSimple = nodoAccesoSelfSimple;
        this.nodoExpresion = nodoExpresion;
    }

    @Override
    public void chequear() throws ErrorSemantico {}
}
