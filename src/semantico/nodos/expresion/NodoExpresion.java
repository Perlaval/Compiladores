package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public abstract class NodoExpresion extends Nodo {

    public abstract Tipo chequear() throws ErrorSemantico;
}
