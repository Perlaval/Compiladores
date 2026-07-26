package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public class NodoWhile extends NodoSentencia{

    private NodoExpresion nodoExpresion;
    private NodoSentencia nodoSentencia;

    public NodoWhile(Token tWhile, NodoExpresion nodoExpresion, NodoSentencia nodoSentencia) {
        super(tWhile);
        this.nodoExpresion = nodoExpresion;
        this.nodoSentencia = nodoSentencia;
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    public NodoSentencia getNodoSentencia() {
        return nodoSentencia;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
