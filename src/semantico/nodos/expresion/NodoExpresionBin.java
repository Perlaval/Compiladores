package semantico.nodos.expresion;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import sintactico.Operador;

public class NodoExpresionBin extends NodoExpresion {

    //protected Token operador;
    protected NodoExpresion exprIzq;
    protected NodoExpresion exprDer;


    public NodoExpresionBin(Token operador, NodoExpresion exprIzq, NodoExpresion exprDer) {
        super(operador);
        this.exprIzq = exprIzq;
        this.exprDer = exprDer;

    }

    public NodoExpresion getExprIzq() {
        return exprIzq;
    }

    public NodoExpresion getExprDer() {
        return exprDer;
    }


    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // me llega una expresion con dos lados mas el operador
        // debo chequedar que los tipos de coincidan

        // tipos de operadores:
        /*
        == | !=
        < | > | <= | >=
        + | -
        + | - | ++ | -- | !
        * | /
         */
        Tipo tipoIzq = exprIzq.chequear(ts);
        Tipo tipoDer = exprDer.chequear(ts);

        // operador mul: Si o si ambos tInt
        // operadores comparacion: deben ser int si o si
        // operador ad: deben ser int si o si
        if (Operador.esOpMul(token) ||Operador.esOpComp(token) || Operador.esOpAd(token)){
            if (!tipoIzq.getNombreTipo().equals("tInt") || !tipoDer.getNombreTipo().equals("tInt") ){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "Para el operador: "+token.getLexema()+" ambos lados de la expresion deben ser de tipo Int");
            }
            return tipoIzq;
        }
        // operador igual: == y !=
        // ambos lados deben ser iguales
        if (Operador.esOpIgual(token)){
            if (!tipoIzq.getNombreTipo().equals(tipoDer.getNombreTipo())){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "Para el operador: "+token.getLexema()+" se deben comparar dos tipos iguales");
            }
            return tipoIzq;
        }
        // + | - | ++ | -- | !

        return null;
    }
}
