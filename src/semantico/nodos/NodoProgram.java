package semantico.nodos;

import lexico.Token;

import java.util.ArrayList;

public class NodoProgram extends Nodo{

    private NodoStart nodoStart;
    private ArrayList<NodoDefinicion> listaDefiniciones;

    public NodoProgram(Token tProgram, ArrayList<NodoDefinicion> listaDefiniciones, NodoStart nodoStart) {
        this.nroLinea = tProgram.getFila();
        this.nroColumna = tProgram.getColumna();
        this.lexema = tProgram.getLexema();
        this.nodoStart = nodoStart;
        this.listaDefiniciones = listaDefiniciones;
    }

    public NodoStart getNodoStart() {
        return nodoStart;
    }

    public ArrayList<NodoDefinicion> getListaDefiniciones() {
        return listaDefiniciones;
    }
}
