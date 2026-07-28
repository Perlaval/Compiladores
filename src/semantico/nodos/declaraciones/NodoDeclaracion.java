package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;

public abstract class NodoDeclaracion extends Nodo {

    public NodoDeclaracion(Token tDeclaracion) {
        super(tDeclaracion);
    }
    public abstract Tipo chequear(TablaSimbolos ts) throws ErrorSemantico;
}
