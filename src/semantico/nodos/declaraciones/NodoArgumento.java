package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.visitors.Visitor;

public class NodoArgumento extends NodoDeclaracion {
    public NodoArgumento(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }

    /*@Override
    public <T> T aceptar(Visitor<T> visitor) {
        return visitor.visit(this);
    }*/
}
