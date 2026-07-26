package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.nodos.Nodo;

public abstract class NodoDeclaracion extends Nodo {

    public NodoDeclaracion(Token tDeclaracion) {
        super(tDeclaracion);
    }

}
