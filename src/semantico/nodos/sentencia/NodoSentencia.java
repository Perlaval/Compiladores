package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;

public abstract class NodoSentencia extends Nodo {

    //Guarda la pos de la sentencia dentro del metodo
    protected int pos;

    public abstract void chequear() throws ErrorSemantico;

}
