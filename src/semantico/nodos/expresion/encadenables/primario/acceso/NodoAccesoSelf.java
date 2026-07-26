package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoAccesoSelf extends NodoAcceso{
    //encadenadoOpt -> llamadaMetodo | accesoVar
    //private final NodoEncadenado encadenado;

    public NodoAccesoSelf(Token token) {
        super(token); //token.getLexema = "self"
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
