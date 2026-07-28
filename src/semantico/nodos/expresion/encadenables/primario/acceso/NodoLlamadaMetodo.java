package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoLlamadaMetodo extends NodoAcceso {

    //private final NodoId nodoId;
    private final ArrayList<NodoExpresion> listaArg;
    //private final NodoEncadenado nodoEncadenado;

    public NodoLlamadaMetodo(Token token, ArrayList<NodoExpresion> listaArg /*, NodoEncadenado nodoEncadenado*/) {
        super(token); // token.getLexema = "(" - LlamadaMetodo: idMetodo(arg1,arg2,..)
        //this.nodoId = nodoId;
        this.listaArg = listaArg;
        //this.nodoEncadenado = nodoEncadenado;
    }

    /*public NodoId getNodoId() {
        return nodoId;
    }*/

    public ArrayList<NodoExpresion> getListaArg() {
        return listaArg;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }

}
