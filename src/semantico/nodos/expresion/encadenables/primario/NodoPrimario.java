package semantico.nodos.expresion.encadenables.primario;

import lexico.Token;
import semantico.nodos.expresion.encadenables.NodoEncadenable;

public abstract class NodoPrimario extends NodoEncadenable {

    protected NodoPrimario(Token token) {
        super(token);
    }

}
