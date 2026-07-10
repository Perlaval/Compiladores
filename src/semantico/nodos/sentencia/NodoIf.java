package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public class NodoIf extends NodoSentencia{

    //if (Expresion) SentenciaRec
    //SentenciaRec -> Sentencia(then) RecursivoElse(else)
    private NodoExpresion nodoCondicion;
    private NodoSentencia nodoSentenciaThen;
    private NodoSentencia nodoSentenciaElse;

    public NodoIf(Token token, NodoExpresion nodoCondicion, NodoSentencia nodoSentenciaThen, NodoSentencia nodoSentenciaElse) {
        this.nroLinea = token.getFila();
        this.nroColumna = token.getColumna();
        this.lexema = token.getLexema();
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
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // el resultado de la condicion debe ser de tipo bool
        Tipo tipoCondicion = nodoCondicion.chequear(ts);
        if (!tipoCondicion.getNombreTipo().equals("tBool")){
            throw new ErrorSemantico(nroLinea, nroColumna, "La condicionn debe ser de tipo Bool");
        }
        return null;
    }
}
