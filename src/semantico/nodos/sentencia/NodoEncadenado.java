package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public abstract class NodoEncadenado extends Nodo {

    protected NodoEncadenado proxEncadenado;

    public abstract Tipo chequear(Tipo tipo) throws ErrorSemantico;
}
