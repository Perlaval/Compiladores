package semantico.nodos.expresion.encadenables.primario.Nnew;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

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
        if (!tipo.esTipoPrimitivo()) throw new ErrorSemantico(token, "Tipo esperado: int, str, bool - tipo declarado: " + tipo.getNombreTipo());

        if (!dimension.chequear(ts).getNombreTipo().equals("tInt")) throw new ErrorSemantico(token, "La dimension del arreglo que desea declarar debe ser de tipo Int");

        return new TipoArreglo(tipo);
    }
}
