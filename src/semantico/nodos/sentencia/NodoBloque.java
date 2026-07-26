package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoBloque extends NodoSentencia{

    private ArrayList<NodoSentencia> listaSentencias;

    public NodoBloque(Token tBloque, ArrayList<NodoSentencia> listaSent) {
        super(tBloque);
        this.listaSentencias = listaSent;
    }

    public ArrayList<NodoSentencia> getListaSent() {
        return listaSentencias;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        return null;
    }
}
