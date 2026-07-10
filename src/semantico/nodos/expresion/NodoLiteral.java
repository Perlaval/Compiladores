package semantico.nodos.expresion;

import lexico.Token;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public abstract class NodoLiteral extends NodoExpresion {

    public NodoLiteral(int nroLinea, int nroColumna, String lexema) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.lexema = lexema;

    }



    @Override
    public abstract Tipo chequear(TablaSimbolos ts);
}
