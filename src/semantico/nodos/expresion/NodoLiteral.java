package semantico.nodos.expresion;

import lexico.Token;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public abstract class NodoLiteral extends NodoExpresion {

    public NodoLiteral(Token token) {
        super(token);
    }

}
