package semantico.nodos.expresion;

import lexico.Token;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoStr extends NodoLiteral{
    public NodoStr(Token token) {
        super(token);
    }


    @Override
    public Tipo chequear(TablaSimbolos ts) {
        return new TipoPrimitivo("tStr");
    }
}
