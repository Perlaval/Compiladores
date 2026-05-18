package semantico.nodos;

import lexico.Token;

import java.util.ArrayList;

public class NodoClase extends NodoDefinicion{

    ArrayList<NodoDeclaracion> listaAtributos;

    public NodoClase(Token tClase, ArrayList<NodoDeclaracion> listaAtr) {
        this.nroLinea = tClase.getFila();
        this.nroColumna = tClase.getColumna();
        this.lexema = tClase.getLexema();
        this.listaAtributos = listaAtr;
    }

    public ArrayList<NodoDeclaracion> getNodoListaAtributos() {
        return listaAtributos;
    }
}
