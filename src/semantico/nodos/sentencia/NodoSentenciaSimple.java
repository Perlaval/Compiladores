package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public class NodoSentenciaSimple extends NodoSentencia{

    private NodoExpresion nodoExpresion;

    public NodoSentenciaSimple(Token token, NodoExpresion nodoExpresion) {
        this.nroLinea = token.getFila();
        this.nroColumna = token.getColumna();
        this.lexema = token.getLexema();
        this.nodoExpresion = nodoExpresion;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        return null;
    }
}
