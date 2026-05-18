package semantico.nodos.sentencia;

import lexico.Token;
import semantico.nodos.expresion.NodoId;
import semantico.tipos.Tipo;

public class NodoFor extends NodoSentencia{

    // for ( tipoPrim i in iterador)
    private Tipo tipoVar;
    private NodoId nodoVariable;
    private NodoId nodoIterador;
    private NodoSentencia cuerpo;

    public NodoFor(Token tFor, Tipo tipoVar, NodoId variable, NodoId iterador, NodoSentencia cuerpo) {
        this.nroLinea = tFor.getFila();
        this.nroColumna = tFor.getColumna();
        this.lexema = tFor.getLexema();
        this.tipoVar = tipoVar;
        this.nodoVariable = variable;
        this.nodoIterador = iterador;
        this.cuerpo = cuerpo;
    }

    public Tipo getTipoVar() {
        return tipoVar;
    }

    public NodoId getNodoVariable() {
        return nodoVariable;
    }

    public NodoId getNodoIterador() {
        return nodoIterador;
    }

    public NodoSentencia getCuerpo() {
        return cuerpo;
    }

    @Override
    public void chequear() {

    }
}
