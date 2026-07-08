package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoNil extends NodoLiteral{

    //public NodoNil(int nroLinea, int nroColumna, String lexema /* "prNill" */) {
      //  super(nroLinea, nroColumna, lexema);
        //Tipo sintetizado es null por defecto

    //}


    public NodoNil(Token token) {
        super(token);
    }


    @Override
    public Tipo chequear() {

        return null;
    }
}
