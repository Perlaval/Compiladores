package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;

import java.util.ArrayList;

public class NodoBloque extends NodoSentencia{

    private ArrayList<NodoSentencia> listaSentencias;

    public NodoBloque(Token tBloque, ArrayList<NodoSentencia> listaSent) {
        this.nroLinea = tBloque.getFila();
        this.nroColumna = tBloque.getColumna();
        this.lexema = tBloque.getLexema(); // lexema = "{"
        this.listaSentencias = listaSent;
    }

    public ArrayList<NodoSentencia> getListaSent() {
        return listaSentencias;
    }

    @Override
    public void chequear() throws ErrorSemantico {

    }
}
