package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

import java.util.ArrayList;
import java.util.List;

public class NodoLlamadaMetodo extends NodoExpresion {

    private NodoId nodoId;
    private ArrayList<NodoExpresion> listaArg;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    /*public NodoLlamadaMetodo(NodoId nodoId, ArrayList<NodoExpresion> listaArg) {
        this.nodoId = nodoId;
        this.listaArg = listaArg;
    }*/

    public NodoLlamadaMetodo(NodoId nodoId, ArrayList<NodoExpresion> listaArg, NodoEncadenadoOpt nodoEncadenadoOpt){
        this.nroLinea = nodoId.getNroLinea();
        this.nroColumna = nodoId.getNroColumna();
        this.lexema = nodoId.getLexema();
        this.nodoId = nodoId;
        this.listaArg = listaArg;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    public NodoId getNodoId() {
        return nodoId;
    }

    public ArrayList<NodoExpresion> getListaArg() {
        return listaArg;
    }

    public NodoEncadenadoOpt getNodoEncadenadoOpt() {
        return nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico { return null; }
}
