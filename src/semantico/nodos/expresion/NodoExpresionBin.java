package semantico.nodos.expresion;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;
import sintactico.Operador;

public class NodoExpresionBin extends NodoExpresion {

    protected Token operador;
    protected NodoExpresion exprIzq;
    protected NodoExpresion exprDer;


    public NodoExpresionBin(Token operador, NodoExpresion exprIzq, NodoExpresion exprDer) {
        super(operador);
        this.exprIzq = exprIzq;
        this.exprDer = exprDer;
        this.operador = operador;

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
        //System.out.println("Vine al chequear de expresion bin con el token: "+token.getLexema());
        //System.out.println("Expresion Izquierda: "+getExprIzq().getToken().getLexema());
        Tipo tipoIzq = exprIzq.chequear(ts);
        //System.out.println("Exp izq " + exprIzq.getToken().getLexema() + " de tipo: " + tipoIzq.getNombreTipo());
        Tipo tipoDer = exprDer.chequear(ts);
        //System.out.println("Exp der " + exprDer.getToken().getLexema() + " de tipo: " + tipoIzq.getNombreTipo());

        // RESOLVER CUANDO ESTOY EN UN IF Y DEVO DEVOLVER QUE LA CONDICION ES BOOL

        // operador mul: Si o si ambos tInt -> devuelvo int
        // operadores comparacion: deben ser int si o si -> devuelvo bool
        // operador ad: deben ser int si o si -> devuelvo bool
        if (Operador.esOpMul(operador) ||Operador.esOpComp(operador) || Operador.esOpAd(operador)) {

            if (!tipoIzq.getNombreTipo().equals("tInt") || !tipoDer.getNombreTipo().equals("tInt")) {
                throw new ErrorSemantico(token, "Para el operador: " + token.getLexema() + " ambos lados de la expresion deben ser de tipo Int");
            }
            if (Operador.esOpComp(operador)) {
                return new TipoPrimitivo("tBool");
            }

            if (Operador.esOpMul(operador) || Operador.esOpAd(operador)){
                return new TipoPrimitivo("tInt");
            }
        }

        // operador igual: == y !=
        // ambos lados deben ser iguales
        if (Operador.esOpIgual(operador)){
            if (!tipoIzq.getNombreTipo().equals(tipoDer.getNombreTipo())){
                throw new ErrorSemantico(token, "Para el operador: "+token.getLexema()+" se deben comparar dos tipos iguales");
            }
            return new TipoPrimitivo("tBool"); // devuelvo tbool
        }
        // op unario:  + | - | ++ | -- | !
        // + y - tomados en opAd
        if (Operador.esOpUnario(operador)){
            // ! exige bool y devuelve bool
            if (!tipoIzq.getNombreTipo().equals("tBool") || !tipoDer.getNombreTipo().equals("tBool")){
                throw new ErrorSemantico(token, "Para el operador: "+token.getLexema()+" ambos lados de la expresion deben ser de tipo bool");
            }
        }

        // && || -> ambos lados bool y devuelvo bool
        if (operador.getLexema().equals("&&") || operador.getLexema().equals("||")){
            if (!tipoIzq.getNombreTipo().equals("tBool") || !tipoDer.getNombreTipo().equals("tBool")){
                throw new ErrorSemantico(token, "Para el operador: "+token.getLexema()+" ambos lados de la expresion deben ser de tipo bool");
            }
            //System.out.println("Entre aca el operador es: "+operador);
            return new TipoPrimitivo("tBool");

        }

        return null;
    }

}
