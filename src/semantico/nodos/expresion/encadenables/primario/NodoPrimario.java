package semantico.nodos.expresion.encadenables.primario;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.nodos.expresion.encadenables.Encadenable;
import semantico.nodos.expresion.encadenables.NodoEncadenable;
import semantico.tipos.Tipo;

public abstract class NodoPrimario extends NodoExpresion {
    protected Encadenable proxEncadenado; //es null si continua la cadena

    protected NodoPrimario(Token token) {
        super(token);
    }

    protected Tipo continuarCadena(TablaSimbolos ts, Tipo tipoActual)
            throws ErrorSemantico {
        if (proxEncadenado != null)
            return proxEncadenado.chequear(ts, tipoActual);
        return tipoActual;
    }

}
