package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;

public class NodoDecLocal extends NodoDeclaracion{


    public NodoDecLocal(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
