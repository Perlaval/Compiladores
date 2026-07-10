package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.NodoDeclaracion;
import semantico.tipos.Tipo;

public class NodoDecArg extends NodoDeclaracion {
    public NodoDecArg(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
