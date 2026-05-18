package semantico.nodos;

import lexico.Token;
import semantico.nodos.sentencia.NodoSentencia;

import java.util.ArrayList;

public class NodoMetodo extends Nodo{

    private NodoBloqueMetodo nodoBloqueMetodo;
    private ArrayList<NodoDeclaracion> listaArgumentos;

    public NodoMetodo(Token tMetodo, ArrayList<NodoDeclaracion> listaArg, NodoBloqueMetodo nodoBloqueMetodo) {
        this.nroLinea = tMetodo.getFila();
        this.nroColumna = tMetodo.getColumna();
        this.lexema = tMetodo.getLexema();
        this.listaArgumentos = listaArg;
        this.nodoBloqueMetodo = nodoBloqueMetodo;
    }

    public NodoBloqueMetodo getNodoBloqueMetodo() {
        return nodoBloqueMetodo;
    }

    public ArrayList<NodoDeclaracion> getListaArgumentos() {
        return listaArgumentos;
    }
}
