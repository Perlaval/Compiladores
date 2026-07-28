package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.nodos.sentencia.NodoSentencia;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;

import java.util.ArrayList;

public class NodoBloqueMetodo extends Nodo {//hereda de nodoDeclaracion?

    private ArrayList<NodoDeclaracion> listaDecVarLocal;
    private ArrayList<NodoSentencia> listaSent;

    public NodoBloqueMetodo(Token tBloqueM, ArrayList<NodoDeclaracion> listaDecVarLocal, ArrayList<NodoSentencia> listaSent) {
        super(tBloqueM);
        this.listaDecVarLocal = listaDecVarLocal;
        this.listaSent = listaSent;
    }

    public ArrayList<NodoDeclaracion> getListaDecVarLocal() {
        return listaDecVarLocal;
    }

    public ArrayList<NodoSentencia> getListaSent() {
        return listaSent;
    }

    /*@Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // para llegar a nodoRet hago esto
        // Primero chequeo declaraciones de variables locales
        for (NodoDeclaracion decl : listaDecVarLocal) {
            //decl.chequear(ts);
        }

        // Después chequeo las sentencias

        System.out.println("Bloque del metodo actual: "
                + ts.getMetodoActual().getNombre()
                + " cantidad sentencias: "
                + listaSent.size());
        for (NodoSentencia sentencia : listaSent) {
            sentencia.chequear(ts);
        }
        return null;
    }*/

    public void accept(Visitor visitor) throws ErrorSemantico {
        visitor.visit(this);
    }


}
