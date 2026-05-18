package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoExpresionUnario extends NodoExpresion {
    protected Token operador;
    protected NodoExpresion exprIzq;;
    private NodoOperando nodoOperando;

    //1. ExpresionUnario -> OpUnario ExpresionUnario
    public NodoExpresionUnario(Token operador, NodoExpresion nodoExpresionUnario) {
        this.nroLinea = operador.getFila();
        this.nroColumna = operador.getColumna();
        this.operador = operador; //oprador tiene el num linea, num columna, lexema
        this.exprIzq = nodoExpresionUnario;
    }

    //2. ExpresionUnario -> Operando
    public NodoExpresionUnario(NodoOperando nodoOperando) {
        this.nodoOperando = nodoOperando;
    }

    public Token getOperador() {
        return operador;
    }

    public NodoExpresion getExprIzq() {
        return exprIzq;
    }

    public NodoOperando getNodoOperando() {
        return nodoOperando;
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
