package semantico.nodos;

import lexico.Token;

import java.util.ArrayList;

public class NodoImpl extends NodoDefinicion{

    private String implClase;
    private ArrayList<NodoMetodo> listaMiembros;

    public NodoImpl(Token tImpl, String implClase, ArrayList<NodoMetodo> listaMiembros) {
        this.nroLinea = tImpl.getFila();
        this.nroColumna = tImpl.getColumna();
        this.lexema = tImpl.getLexema();
        this.implClase = implClase;
        this.listaMiembros = listaMiembros;
    }

    public String getImplClase() {
        return implClase;
    }

    public ArrayList<NodoMetodo> getListaMiembros() {
        return listaMiembros;
    }
}
