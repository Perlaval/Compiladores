package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;

public class NodoId extends Nodo{

    public NodoId(int nroLinea, int nroColumna, String lexema) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.lexema = lexema;
        //this.tipoSintetizado = token.getTipo();
    }

    @Override
    public void chequear() {

    }

    public void chequear(boolean fromAcceso) throws ErrorSemantico {
        if (!(fromAcceso && token.tipo.getNombreTipo().equals("tArray"))){
            throw new ErrorSemantico(nroLinea, nroColumna, "La variable " + token.getNombre() + "debe ser de tipoArray");

        }

    }
}
