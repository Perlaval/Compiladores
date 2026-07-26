package semantico.nodos.expresion;

import lexico.Token;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoBool extends NodoLiteral {
    public NodoBool(Token token) {
        super(token);
        //this.tipoSintetizado = new TipoPrimitivo("tBool");

    }

    @Override
    public Tipo chequear(TablaSimbolos ts) {
        return new TipoPrimitivo("tBool");
    }
}
