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
        if (!tipo.esTipoPrimitivo()) throw new ErrorSemantico(token, "Error Semántico, tipo esperado: int, str, bool - tipo declarado: " + tipo.getNombreTipo());

        // esto nose si hay que sacarlo, mejor chequearlo en generacion de codigo
        if (!dimension.chequear(ts).getNombreTipo().equals("Int")) throw new ErrorSemantico(token, "Error Semántico, la dimension del arreglo que desea declarar debe ser de tipo Int");

        return tipo;
    }
}
