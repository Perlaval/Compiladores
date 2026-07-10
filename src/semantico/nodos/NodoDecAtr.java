package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoDecAtr extends NodoDeclaracion{
    public NodoDecAtr(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
