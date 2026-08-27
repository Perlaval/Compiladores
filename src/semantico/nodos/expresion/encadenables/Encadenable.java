package semantico.nodos.expresion.encadenables;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public interface Encadenable{

    /*protected NodoEncadenable proxEncadenado;

    protected NodoEncadenable(Token token) {
        super(token);
    }

    public void setProxEncadenado(NodoEncadenable proxEncadenado){
        this.proxEncadenado = proxEncadenado;
    }

    public NodoEncadenable getProxEncadenado() { return proxEncadenado;}*/
    Tipo chequear(TablaSimbolos ts, Tipo tipoHeredado) throws ErrorSemantico;

}
