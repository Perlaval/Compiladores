package semantico.nodos;

import lexico.Token;
import semantico.tipos.Tipo;

public abstract class NodoDeclaracion extends Nodo{

    public NodoDeclaracion(Token tdeclaracion) {
        this.nroLinea = tdeclaracion.getFila();
        this.nroColumna = tdeclaracion.getColumna();
        this.lexema = tdeclaracion.getLexema();
    }


}
