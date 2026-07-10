package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.sentencia.NodoSentencia;
import semantico.tipos.Tipo;

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

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // para llegar a nodoRet hago esto
        // Primero chequeo declaraciones de variables locales
        for (NodoDeclaracion decl : listaDecVarLocal) {
            decl.chequear(ts);
        }

        // Después chequeo las sentencias
        /*
        System.out.println("Bloque del metodo actual: "
                + ts.getMetodoActual().getNombre()
                + " cantidad sentencias: "
                + listaSent.size());*/
        for (NodoSentencia sentencia : listaSent) {
            sentencia.chequear(ts);
        }
        return null;
    }
}
