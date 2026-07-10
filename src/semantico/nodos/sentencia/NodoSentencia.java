package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public abstract class NodoSentencia extends Nodo {

    //Guarda la pos de la sentencia dentro del metodo
    protected int pos;

    public abstract Tipo chequear(TablaSimbolos ts) throws ErrorSemantico;

}
