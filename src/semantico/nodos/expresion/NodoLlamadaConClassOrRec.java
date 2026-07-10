package semantico.nodos.expresion;

import semantico.TablaSimbolos;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoLlamadaConClassOrRec extends NodoExpresion {

    //1.
    private NodoId nodoId;
    private ArrayList<NodoExpresion> listaArgumentosActuales;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    //2.
    private Tipo tipo;
    private NodoExpresion nodoExpresion;

    public NodoLlamadaConClassOrRec(NodoId nodoId, ArrayList<NodoExpresion> listaArg, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nroLinea = nodoId.getNroLinea();
        this.nroColumna = nodoId.getNroColumna();
        this.nodoId = nodoId;
        this.listaArgumentosActuales = listaArg;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    public NodoLlamadaConClassOrRec(Tipo tipo, NodoExpresion nodoExpresion) {
        this.tipo = tipo;
        this.nodoExpresion = nodoExpresion;
    }

    public NodoId getNodoId() {
        return nodoId;
    }

    public ArrayList<NodoExpresion> getListaArgumentosActuales() {
        return listaArgumentosActuales;
    }

    public NodoEncadenadoOpt getNodoEncadenadoOpt() {
        return nodoEncadenadoOpt;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts){

        return null;
    }
}
