package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoStart extends Nodo{

    private NodoBloqueMetodo nodoBloqueMetodo;

    public NodoStart(Token tStart, NodoBloqueMetodo nodoBloqueMetodo) {
        this.nroLinea = tStart.getFila();
        this.nroColumna = tStart.getColumna();
        this.lexema = tStart.getLexema();
        this.nodoBloqueMetodo = nodoBloqueMetodo;
    }

    public NodoBloqueMetodo getBloqueMetodo() {
        return nodoBloqueMetodo;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // recorrido para probar nodo ret
        //System.out.println("Chequeo start");

        ts.setMetodoActual(ts.getMetodoActual());
        nodoBloqueMetodo.chequear(ts);
        return null;
    }
}
