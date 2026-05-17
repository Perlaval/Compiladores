package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoExpresionUnario extends NodoExpresion {
    protected Token operador;
    protected NodoExpresion exprIzq;;
    private NodoOperando nodoOperando;

    //1. ExpresionUnario -> OpUnario ExpresionUnario
    public NodoExpresionUnario(Token operador, NodoExpresion nodoExpresionUnario) {
        this.operador = operador; //oprador tiene el num linea, num columna, lexema
        this.exprIzq = nodoExpresionUnario;
    }

    //2. ExpresionUnario -> Operando
    public NodoExpresionUnario(NodoOperando nodoOperando) {
        this.nodoOperando = nodoOperando;
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
