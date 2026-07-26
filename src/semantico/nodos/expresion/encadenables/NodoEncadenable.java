package semantico.nodos.expresion.encadenables;

import lexico.Token;
import semantico.nodos.expresion.NodoExpresion;

public abstract class NodoEncadenable extends NodoExpresion {

    protected NodoEncadenable proxEncadenado;

    protected NodoEncadenable(Token token) {
        super(token);
    }

    public void setProxEncadenado(NodoEncadenable proxEncadenado){
        this.proxEncadenado = proxEncadenado;
    }

    public NodoEncadenable getProxEncadenado() { return proxEncadenado;}

}
