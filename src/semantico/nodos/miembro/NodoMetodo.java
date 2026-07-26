package semantico.nodos.miembro;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.Nodo;
import semantico.nodos.declaraciones.NodoBloqueMetodo;
import semantico.nodos.declaraciones.NodoDeclaracion;
import semantico.registros.RegistroMetodo;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoMetodo extends Nodo {

    private final NodoBloqueMetodo nodoBloqueMetodo;
    private ArrayList<NodoDeclaracion> listaArgumentos;
    // agrego el registro al metodo en el que estoy en la ts
    private RegistroMetodo metodoActual;

    public NodoMetodo(Token tMetodo, ArrayList<NodoDeclaracion> listaArg, NodoBloqueMetodo nodoBloqueMetodo, RegistroMetodo metodoActual) {
        super(tMetodo);
        this.listaArgumentos = listaArg;
        this.nodoBloqueMetodo = nodoBloqueMetodo;
        this.metodoActual = metodoActual;
    }

    public NodoBloqueMetodo getNodoBloqueMetodo() {
        return nodoBloqueMetodo;
    }

    public ArrayList<NodoDeclaracion> getListaArgumentos() {
        return listaArgumentos;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        //System.out.println("Chequeo metodo: " + metodoActual.getNombre());
        if (!metodoActual.isConstructor()){
            System.out.println("retorno: "+metodoActual.getTipoRetorno().getNombreTipo());
        }
        //System.out.println("retorno: "+metodoActual.getTipoRetorno().getNombreTipo());
        ts.setMetodoActual(metodoActual);

        nodoBloqueMetodo.chequear(ts);
        return null;
    }
}
