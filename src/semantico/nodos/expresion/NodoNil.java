package semantico.nodos.expresion;

import lexico.Token;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoNil extends NodoLiteral{

    public NodoNil(Token token) {
        super(token);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) {
        return null;
    }
}
