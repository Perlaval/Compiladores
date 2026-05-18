package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public abstract class NodoExpresion extends Nodo {

    public abstract Tipo chequear() throws ErrorSemantico;

    /*public void chequear(int fila, int columna, boolean fromAcceso) throws ErrorSemantico {
        if (fromAcceso){
            if (this.tipoSintetizado.getNombreTipo() != "tInt"){
                throw new ErrorSemantico(fila, columna, "El tipo de la expresion para acceder a un elemento del arreglo deber ser Int -> id[Expresion]");
            }
        }
    }*/
}
