package semantico.nodos.programa;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.nodos.definiciones.NodoDefinicion;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoProgram extends Nodo {

    private NodoStart nodoStart;
    private ArrayList<NodoDefinicion> listaDefiniciones;

    public NodoProgram(Token tProgram, ArrayList<NodoDefinicion> listaDefiniciones, NodoStart nodoStart) {
        super(tProgram);
        this.nodoStart = nodoStart;
        this.listaDefiniciones = listaDefiniciones;
    }

    public NodoStart getNodoStart() {
        return nodoStart;
    }

    public ArrayList<NodoDefinicion> getListaDefiniciones() {
        return listaDefiniciones;
    }


    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // hago recorrido para probar el nodoret
        //System.out.println("Chequeo program");
        // voy a chequear los impl de program
        // primero chequeo todas las definiciones (class e impl)
        for(NodoDefinicion def : listaDefiniciones) {
            def.chequear(ts);
        }
        nodoStart.chequear(ts);
        return null;
    }
}
