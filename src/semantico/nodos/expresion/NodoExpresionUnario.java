package semantico.nodos.expresion;

import lexico.Token;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoExpresionUnario extends NodoExpresion {
    //protected Token operador;
    protected NodoExpresion exprIzq;;
    //private NodoOperando nodoOperando;

    //1. ExpresionUnario -> OpUnario ExpresionUnario
    public NodoExpresionUnario(Token operador, NodoExpresion nodoExpresionUnario) {
        super(operador);
        this.exprIzq = nodoExpresionUnario;
    }

    //2. ExpresionUnario -> Operando
    /*public NodoExpresionUnario(NodoOperando nodoOperando) {
        this.nodoOperando = nodoOperando;
    }*/

    public NodoExpresion getExprIzq() {
        return exprIzq;
    }



    @Override
    public Tipo chequear(TablaSimbolos ts) {
        //return nodoOperando.chequear(ts);
        return null;
    }
}
