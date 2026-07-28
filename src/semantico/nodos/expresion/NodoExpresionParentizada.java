package semantico.nodos.expresion;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.encadenables.primario.NodoPrimario;
import semantico.tipos.Tipo;

public class NodoExpresionParentizada extends NodoPrimario {

    private NodoExpresion nodoExpresion;

    public NodoExpresionParentizada(Token token, NodoExpresion nodoExpresion) {
        super(token);
        this.nodoExpresion = nodoExpresion;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts){

        return null;
    }

}
