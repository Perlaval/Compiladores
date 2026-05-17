package semantico.nodos.expresion;

import semantico.nodos.Nodo;
import semantico.tipos.Tipo;

public class NodoTipoPrimitivo extends Nodo {

    public NodoTipoPrimitivo(int nroLinea, int nroColumna, String lexema) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.lexema = lexema;
        //this.tipoSintetizado = token.getTipo();
    }

    public Tipo chequear(){
        //devuelve el tipo

        return null;
    }
}
