package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.nodos.expresion.encadenables.primario.NodoPrimario;

public abstract class NodoAcceso extends NodoPrimario {

    protected NodoAcceso(Token token) {
        super(token);
    }

}
