package semantico.nodos.expresion.encadenables.primario.Nnew;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public class NodoNewArreglo extends NodoNew {

    private Tipo tipo;
    private NodoExpresion dimension;

    public NodoNewArreglo(Token token, Tipo tipo, NodoExpresion dimension) {
        super(token);
        this.tipo = tipo;
        this.dimension = dimension;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
