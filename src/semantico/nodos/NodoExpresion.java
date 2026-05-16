package semantico.nodos;

import semantico.ErrorSemantico;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

public class NodoExpresion extends Nodo{



    @Override
    public void chequear() {

    }

    public void chequear(int fila, int columna, boolean fromAcceso) throws ErrorSemantico {
        if (fromAcceso){
            if (this.tipoSintetizado.getNombreTipo() != "tInt"){
                throw new ErrorSemantico(fila, columna, "El tipo de la expresion para acceder a un elemento del arreglo deber ser Int -> id[Expresion]");
            }
        }
    }
}
