package semantico.visitors;

import semantico.nodos.declaraciones.NodoDeclaracion;

public interface Visitor<T> {
    T visit(NodoDeclaracion nodo);
}
