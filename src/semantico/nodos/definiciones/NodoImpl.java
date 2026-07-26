package semantico.nodos.definiciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.miembro.NodoMetodo;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoImpl extends NodoDefinicion {

    private String implClase;
    private ArrayList<NodoMetodo> listaMiembros;

    public NodoImpl(Token tImpl, String implClase, ArrayList<NodoMetodo> listaMiembros) {
        super(tImpl);
        this.implClase = implClase;
        this.listaMiembros = listaMiembros;
    }

    public String getImplClase() {
        return implClase;
    }

    public ArrayList<NodoMetodo> getListaMiembros() {
        return listaMiembros;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        //System.out.println("Chequeo impl");

        for(NodoMetodo metodo : listaMiembros){
            metodo.chequear(ts);
        }
        return null;
    }
}
