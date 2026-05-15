package semantico.nodos;

import lexico.Token;
import semantico.tipos.Tipo;

public class NodoId extends Nodo{

    public NodoId(int nroLinea, int nroColumna, Token token) {
        super(nroLinea, nroColumna, token);
    }

    @Override
    public Tipo chequear() {
        return null;
    }
}
