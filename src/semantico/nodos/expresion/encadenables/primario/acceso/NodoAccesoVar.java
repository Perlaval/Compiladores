package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.encadenables.NodoEncadenable;
import semantico.registros.*;
import semantico.tipos.Tipo;
import semantico.tipos.TipoReferencia;

public class NodoAccesoVar extends NodoAcceso{

    //private final NodoId id;

    public NodoAccesoVar(Token token /*NodoId id*/) {
        super(token); // token.getLexema = "." - AccesoVar: id.encadenado
        //this.id = id;
    }

    /*public NodoId getNodoId() {
        return id;
    }*/

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // busco el id en esa clase
        // verifico que exista
        // obtengo el tipo
        // devuelvo ese tipo

        // la funcion continuar cadena se encuentra en NodoEncadenable

        // por lo tanto busco en la clase de ese impl si tengo esa variable

        RegistroClase claseActual = ts.getClaseActual();
        String id = token.getLexema(); //expresion a buscar en la ts
        //System.out.println("El id que estoy buscando en nodoAccesoVar es: "+id);

        //ATRIBUTO ----------------------------------------------------------------------------
        RegistroAtributo atributo = claseActual.getListaAtributos().get(id);
        if (atributo != null){
            return continuarCadena(ts,atributo.getTipo());
        }

        // si no es atributo puede estar en los parametros del metodo
        // PARAMETROS METODO ------------------------------------------------------------------
        RegistroMetodo metodoActual = ts.getMetodoActual();
        RegistroParametro parametro = metodoActual.getListaParametros().get(id);
        if (parametro != null){
            return continuarCadena(ts, parametro.getTipo());
        }

        // Puede ser var local del metodo
        // VAR LOCAL METODO --------------------------------------------------------------------
        RegistroVariable variableLocal = metodoActual.getListaVarLocales().get(id);
        if (variableLocal != null){
            return continuarCadena(ts, variableLocal.getTipo());
        }

        // si no es ni atributo de la clase, ni parametro del metodo ni var local del metodo -> ERROR
        else {
            throw new ErrorSemantico(token, "Id: '"+id+"' no declarado en la clase: "+claseActual.nombre);
        }
       // return null;
    }

}
