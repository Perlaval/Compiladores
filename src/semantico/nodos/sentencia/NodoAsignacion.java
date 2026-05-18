package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;

public class NodoAsignacion extends NodoSentencia{


    private NodoSentencia nodoAcceso;
    private NodoExpresion nodoExpresion;
    //private NodoAccesoSelfSimple nodoAccesoSelfSimple;

    //1. Asignacion -> AccesoVarSimple = Expresion
    //2. Asignacion -> AccesoSelfSimple = Expresion
    public NodoAsignacion(Token tAsig, NodoSentencia nodoAcceso, NodoExpresion nodoExpresion) {
        this.nroLinea = tAsig.getFila();
        this.nroColumna = tAsig.getColumna();
        this.lexema = tAsig.getLexema();
        this.nodoAcceso = nodoAcceso;
        this.nodoExpresion = nodoExpresion;
    }

    public NodoSentencia getNodoAcceso() {
        return nodoAcceso;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public void chequear() throws ErrorSemantico {}
}
