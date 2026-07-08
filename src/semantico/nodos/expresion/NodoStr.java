package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoStr extends NodoLiteral{
    public NodoStr(int nroLinea, int nroColumna, String lexema /* "literal_cadena" */) {
        super(nroLinea, nroColumna, lexema);
        this.tipoSintetizado = new TipoPrimitivo("tStr");
    }
    public NodoStr(Token token){
        super(token);
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
