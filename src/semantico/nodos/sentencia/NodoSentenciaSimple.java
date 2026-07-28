package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;


public class NodoSentenciaSimple extends NodoSentencia{

    private NodoExpresion nodoExpresion;

    public NodoSentenciaSimple(Token token, NodoExpresion nodoExpresion) {
        super(token);
        this.nodoExpresion = nodoExpresion;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    /*@Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        return null;
    }*/

    @Override
    public void accept(Visitor visitor) throws ErrorSemantico {
        visitor.visit(this);
    }
}
