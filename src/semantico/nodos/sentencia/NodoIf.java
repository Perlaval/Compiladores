package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;


public class NodoIf extends NodoSentencia{


    //if (Expresion) SentenciaRec
    //SentenciaRec -> Sentencia(then) RecursivoElse(else)
    private NodoExpresion nodoCondicion;
    private NodoSentencia nodoSentenciaThen;
    private NodoSentencia nodoSentenciaElse;

    public NodoIf(Token token, NodoExpresion nodoCondicion, NodoSentencia nodoSentenciaThen, NodoSentencia nodoSentenciaElse) {
        super(token);
        this.nodoCondicion = nodoCondicion;
        this.nodoSentenciaThen = nodoSentenciaThen;
        this.nodoSentenciaElse = nodoSentenciaElse;
    }

    public NodoExpresion getNodoCondicion() {
        return nodoCondicion;
    }

    public NodoSentencia getNodoSentenciaThen() {
        return nodoSentenciaThen;
    }

    public NodoSentencia getNodoSentenciaElse() {
        return nodoSentenciaElse;
    }


    @Override
    public void accept(Visitor visitor) throws ErrorSemantico {
        visitor.visit(this);
    }

}
