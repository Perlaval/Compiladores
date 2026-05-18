package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoExpresionBin extends NodoExpresion {

    protected Token operador;
    protected NodoExpresion exprIzq;
    protected NodoExpresion exprDer;

    public NodoExpresionBin(Token operador, NodoExpresion exprIzq, NodoExpresion exprDer) {
        this.nroLinea = operador.getFila();
        this.nroColumna = operador.getColumna();
        this.operador = operador;
        this.exprIzq = exprIzq;
        this.exprDer = exprDer;

    }

    public Token getOperador() {
        return operador;
    }

    public NodoExpresion getExprIzq() {
        return exprIzq;
    }

    public NodoExpresion getExprDer() {
        return exprDer;
    }

    @Override
    public Tipo chequear() {
        return null;
    }
}
