package semantico.nodos.expresion.encadenables.primario.Nnew;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoNewObjeto extends NodoNew {

    //private NodoId nodoId;
    private ArrayList<NodoExpresion> listaArgumentosActuales;

    public NodoNewObjeto(Token token, ArrayList<NodoExpresion> listaArgumentosActuales) {
        super(token);
        //this.nodoId = nodoId;
        this.listaArgumentosActuales = listaArgumentosActuales;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
