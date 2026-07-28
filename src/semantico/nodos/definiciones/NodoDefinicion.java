package semantico.nodos.definiciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.visitor.Visitor;

public abstract class NodoDefinicion extends Nodo {

    protected NodoDefinicion(Token token) {
        super(token);
    }
    public abstract void accept(Visitor visitor) throws ErrorSemantico;

}
