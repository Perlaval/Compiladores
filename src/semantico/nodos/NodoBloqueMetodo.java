package semantico.nodos;

import lexico.Token;
import semantico.nodos.sentencia.NodoSentencia;

import java.util.ArrayList;

public class NodoBloqueMetodo extends Nodo{

    private ArrayList<NodoDeclaracion> listaDecVarLocal;
    private ArrayList<NodoSentencia> listaSent;

    public NodoBloqueMetodo(Token tBloqueM, ArrayList<NodoDeclaracion> listaDecVarLocal, ArrayList<NodoSentencia> listaSent) {
        this.nroLinea = tBloqueM.getFila();
        this.nroColumna = tBloqueM.getColumna();
        this.lexema = tBloqueM.getLexema();
        this.listaDecVarLocal = listaDecVarLocal;
        this.listaSent = listaSent;
    }

    public ArrayList<NodoDeclaracion> getListaDecVarLocal() {
        return listaDecVarLocal;
    }

    public ArrayList<NodoSentencia> getListaSent() {
        return listaSent;
    }
}
