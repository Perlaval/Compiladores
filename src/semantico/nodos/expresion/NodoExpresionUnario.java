package semantico.nodos.expresion;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;
import sintactico.Operador;

public class NodoExpresionUnario extends NodoExpresion {
    protected Token operador;
    protected NodoExpresion exp;;
    //private NodoOperando nodoOperando;

    //1. ExpresionUnario -> OpUnario ExpresionUnario
    public NodoExpresionUnario(Token operador, NodoExpresion nodoExpresionUnario) {
        super(operador);
        this.exp = nodoExpresionUnario;
        this.operador = operador;
    }

    //2. ExpresionUnario -> Operando
    /*public NodoExpresionUnario(NodoOperando nodoOperando) {
        this.nodoOperando = nodoOperando;
    }*/

    public NodoExpresion getExprIzq() {
        return exp;
    }


    // ( + - ++ -- ! )
    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        //System.out.println("Expresion izq de expresion unario es: "+exp.getToken().getLexema());
        Tipo tipo = exp.chequear(ts);
        //System.out.println("de tipo: "+tipo.getNombreTipo());
        //System.out.println("Estoy en expresionUnario cone el operador: "+operador);


        if (Operador.esOpUnario(operador)){
            if (operador.getLexema().equals("!")){
                if (!tipo.getNombreTipo().equals("tBool")){
                    throw new ErrorSemantico(token, "El operador: "+operador.getLexema()+" espera un tipo bool");
                }
                return new TipoPrimitivo("tBool");
            }
            else{
                // tiene que ser int + - ++ --
                if (!tipo.getNombreTipo().equals("tInt")){
                    throw new ErrorSemantico(token, "El operador: "+operador.getLexema()+" espera un tipo bool");
                }
                return new TipoPrimitivo("tInt");
            }
        }

        return null;
    }

}
