package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoAtributo extends NodoDeclaracion{
    public NodoAtributo(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
