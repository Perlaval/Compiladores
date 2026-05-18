package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoLlamadaMetodoEstatico extends NodoExpresion {

    private NodoId nodoId;
    private String nombreClase;
    private NodoLlamadaMetodo nodoLL;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    /*public NodoLlamadaMetodoEstatico(NodoId nodoId, NodoLlamadaMetodo nodoLL){
        this.nodoId = nodoId;
        this.nodoLL = nodoLL;
    }*/

    public NodoLlamadaMetodoEstatico(NodoId nodoId, NodoLlamadaMetodo nodoLL, NodoEncadenadoOpt encadenadoOpt){
        this.nroLinea = nodoId.getNroLinea();
        this.nroColumna = nodoId.getNroColumna();
        this.nombreClase = nodoId.getLexema();
        this.nodoId = nodoId;
        this.nodoLL = nodoLL;
        this.nodoEncadenadoOpt = encadenadoOpt;
    }

    public NodoId getNodoId() {
        return nodoId;
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public NodoLlamadaMetodo getNodoLL() {
        return nodoLL;
    }

    public NodoEncadenadoOpt getNodoEncadenadoOpt() {
        return nodoEncadenadoOpt;
    }

    @Override
    public Tipo chequear() throws ErrorSemantico {
        return null;
    }
}
