package semantico.nodos.expresion;

import lexico.Token;
import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

public class NodoBool extends NodoLiteral {
    // NodoBool(int nroLinea, int nroColumna, String lexema /* "prTrue" | "prFalse" */) {
     //   super(nroLinea, nroColumna, lexema);
      //  this.tipoSintetizado = new TipoPrimitivo("tBool");

    //}
    public NodoBool(Token token){
        super();
    }

    @Override
    public Tipo chequear() {

        return null;
    }
}
