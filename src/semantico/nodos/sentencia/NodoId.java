package semantico.nodos.sentencia;

import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

public class NodoId extends NodoExpresion {

    public NodoId(int nroLinea, int nroColumna, String lexema) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.lexema = lexema;
        //this.tipoSintetizado = token.getTipo();
    }

    @Override
    public Tipo chequear() {

        return null;
    }

    /*public void chequear(boolean fromAcceso) throws ErrorSemantico {
        if (!(fromAcceso && token.tipo.getNombreTipo().equals("tArray"))){
            throw new ErrorSemantico(nroLinea, nroColumna, "La variable " + token.getNombre() + "debe ser de tipoArray");

        }

    }*/
}
