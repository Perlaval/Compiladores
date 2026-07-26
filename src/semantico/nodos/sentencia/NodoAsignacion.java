package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAcceso;
import semantico.tipos.Tipo;

public class NodoAsignacion extends NodoSentencia{


    private NodoAcceso nodoAcceso;
    private NodoExpresion nodoExpresion;
    //private NodoAccesoSelfSimple nodoAccesoSelfSimple;

    //1. Asignacion -> AccesoVarSimple = Expresion
    //2. Asignacion -> AccesoSelfSimple = Expresion
    public NodoAsignacion(Token tAsig, NodoAcceso nodoAcceso, NodoExpresion nodoExpresion) {
        super(tAsig);
        this.nodoAcceso = nodoAcceso;
        this.nodoExpresion = nodoExpresion;
    }

    public NodoAcceso getNodoAcceso() {
        return nodoAcceso;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
