package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.registros.*;
import semantico.tipos.Tipo;

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

        // si estoy aca estoy verificando por ejemplo un if de un impl
        // por lo tanto busco en la clase de ese impl si tengo esa variable

        RegistroClase claseActual = ts.getClaseActual();
        String id = token.getLexema(); //expresion a buscar en la ts
        RegistroAtributo atributo = claseActual.getListaAtributos().get(id);

        if (claseActual.getListaAtributos().containsKey(id)){
            // obtengo el tipo del atributo
            Tipo tipoAtributo = atributo.getTipo();
            //System.out.printf("Tipo val: "+tipoAtributo.getNombreTipo());
            return tipoAtributo;
        }
        // si no es atributo puede estar en los parametros del metodo que tiene el if
        RegistroMetodo metodoActual = ts.getMetodoActual();
        RegistroParametro parametro = metodoActual.getListaParametros().get(id);
        if (metodoActual.getListaParametros().containsKey(id)){
            Tipo tipoParam = parametro.getTipo();
            return tipoParam;
        }

        // Puede ser var local del metodo
        RegistroVariable variableLocal = metodoActual.getListaVarLocales().get(id);
        if (metodoActual.getListaVarLocales().containsKey(id)){
            Tipo tipoVarLocal = variableLocal.getTipo();
            return tipoVarLocal;
        }

        // si no es ni atributo de la clase, ni parametro del metodo ni var local del metodo -> ERROR
        else {
            throw new ErrorSemantico(token, "Id: '"+id+"' no declarado en la clase: "+claseActual.nombre);
        }
       // return null;
    }

}
