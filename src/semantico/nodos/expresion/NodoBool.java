package semantico.nodos.expresion;

import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoBool extends NodoLiteral {
    public NodoBool(int nroLinea, int nroColumna, String lexema /* "prTrue" | "prFalse" */) {
        super(nroLinea, nroColumna, lexema);
        this.tipoSintetizado = new TipoPrimitivo("tBool");

    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
