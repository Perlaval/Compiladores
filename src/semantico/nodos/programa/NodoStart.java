package semantico.nodos.programa;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.nodos.declaraciones.NodoBloqueMetodo;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;

public class NodoStart extends Nodo {

    private NodoBloqueMetodo nodoBloqueMetodo;

    public NodoStart(Token tStart, NodoBloqueMetodo nodoBloqueMetodo) {
        super(tStart);
        this.nodoBloqueMetodo = nodoBloqueMetodo;
    }

    public NodoBloqueMetodo getNodoBloqueMetodo() {
        return nodoBloqueMetodo;
    }

    /*@Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // recorrido para probar nodo ret
        //System.out.println("Chequeo start");

        ts.setMetodoActual(ts.getMetodoActual());
        nodoBloqueMetodo.chequear(ts);
        return null;
    }*/

    public void accept(Visitor visitor) throws ErrorSemantico {
        visitor.visit(this);
    }

}
