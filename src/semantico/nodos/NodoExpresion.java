package semantico.nodos;

import semantico.ErrorSemantico;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

public class NodoExpresion extends Nodo{
    @Override
    public Tipo chequear() {
        return null;
    }

    public Tipo chequear(boolean fromAcceso) throws ErrorSemantico {
        if (fromAcceso){
            if (this.tipoSintetizado.getNombreTipo() != "tInt"){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "El tipo de la expresion para acceder a un elemento del arreglo deber ser Int -> id[Expresion]");
            }
        }
        return new TipoArreglo(this.tipoSintetizado);
    }
}
