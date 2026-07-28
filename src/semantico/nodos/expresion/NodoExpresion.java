package semantico.nodos.expresion;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public abstract class NodoExpresion extends Nodo {


    protected NodoExpresion(Token token) {
        super(token);
    }

    /*public void chequear(int fila, int columna, boolean fromAcceso) throws ErrorSemantico {
        if (fromAcceso){
            if (this.tipoSintetizado.getNombreTipo() != "tInt"){
                throw new ErrorSemantico(fila, columna, "El tipo de la expresion para acceder a un elemento del arreglo deber ser Int -> id[Expresion]");
            }
        }
    }*/
    public abstract Tipo chequear(TablaSimbolos ts) throws ErrorSemantico;
}
