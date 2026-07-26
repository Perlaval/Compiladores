package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.sentencia.NodoSentencia;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class NodoMetodo extends Nodo{

    private NodoBloqueMetodo nodoBloqueMetodo;
    private ArrayList<NodoDeclaracion> listaArgumentos;
    // agrego el registro al metodo en el que estoy en la ts
    private RegistroMetodo metodoActual;

    public NodoMetodo(Token tMetodo, ArrayList<NodoDeclaracion> listaArg, NodoBloqueMetodo nodoBloqueMetodo, RegistroMetodo metodoActual) {
        this.nroLinea = tMetodo.getFila();
        this.nroColumna = tMetodo.getColumna();
        this.lexema = tMetodo.getLexema();
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
            //System.out.println("retorno: "+metodoActual.getTipoRetorno().getNombreTipo());
        }
        //System.out.println("retorno: "+metodoActual.getTipoRetorno().getNombreTipo());
        ts.setMetodoActual(metodoActual);

        nodoBloqueMetodo.chequear(ts);
        return null;
    }
}
