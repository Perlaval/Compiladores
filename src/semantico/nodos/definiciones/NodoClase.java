package semantico.nodos.definiciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.declaraciones.NodoDeclaracion;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoClase extends NodoDefinicion {

    ArrayList<NodoDeclaracion> listaAtributos;

    public NodoClase(Token tClase, ArrayList<NodoDeclaracion> listaAtr) {
        super(tClase);
        this.listaAtributos = listaAtr;
    }

    public ArrayList<NodoDeclaracion> getNodoListaAtributos() {
        return listaAtributos;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
