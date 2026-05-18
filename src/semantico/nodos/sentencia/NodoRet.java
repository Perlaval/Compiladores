package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;

public class NodoRet extends NodoSentencia{

    private NodoExpresion nodoExpresionOpt;

    public NodoRet(Token tRet, NodoExpresion nodoExpresionOpt) {
        this.nroLinea = tRet.getFila();
        this.nroColumna = tRet.getColumna();
        this.lexema = tRet.getLexema();
        this.nodoExpresionOpt = nodoExpresionOpt;
    }

    public NodoExpresion getNodoExpresionOpt() {
        return nodoExpresionOpt;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
