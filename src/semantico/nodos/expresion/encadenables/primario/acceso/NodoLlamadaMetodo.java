package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.nodos.expresion.encadenables.Encadenable;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoLlamadaMetodo extends NodoAcceso implements Encadenable {

    //private final NodoId nodoId;
    private final ArrayList<NodoExpresion> listaArg;
    private Encadenable proxEncadenado;
    //private final NodoEncadenado nodoEncadenado;

    public NodoLlamadaMetodo(Token token, ArrayList<NodoExpresion> listaArg /*, NodoEncadenado nodoEncadenado*/) {
        super(token); // token.getLexema = idMetodo - LlamadaMetodo: idMetodo(arg1,arg2,..)
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

    //Como inicio de cadena (dentro de primario)
    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }

    //Como eslabón (viene de un encadenado)
    @Override
    public Tipo chequear(TablaSimbolos ts, Tipo tipoHeredado) throws ErrorSemantico {
        return null;
    }

    protected Tipo continuarCadena(TablaSimbolos ts, Tipo tipoActual)
            throws ErrorSemantico {
        if (proxEncadenado != null)
            return proxEncadenado.chequear(ts, tipoActual);
        return tipoActual;
    }
}
