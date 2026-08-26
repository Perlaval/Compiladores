package semantico.nodos.expresion;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoStr extends NodoLiteral{
    public NodoStr(Token token) {
        super(token);
    }

    @Override //puede ser tanto tipo primitivo como tipoReferencia
    public Tipo chequear(TablaSimbolos ts) {
        return new TipoPrimitivo("tStr");
    }

}
