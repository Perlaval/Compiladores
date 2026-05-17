package semantico.nodos.expresion;

import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoNum extends NodoLiteral{
    public NodoNum(int nroLinea, int nroColumna, String lexema /* "literal_entero" */) {
        super(nroLinea, nroColumna, lexema);
        this.tipoSintetizado = new TipoPrimitivo("tInt");
    }

    @Override
    public void setTipoSintetizado(Tipo tipoSintetizado) {
        super.setTipoSintetizado(tipoSintetizado);
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
