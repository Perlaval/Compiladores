package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.registros.*;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

public class NodoAccesoArreglo extends NodoAcceso{

    //private final boolean encadenable = true;
    //private final NodoId id;
    private final NodoExpresion indice;
     // En accesoVarSimple el arreglo no acepta encadenado


    public NodoAccesoArreglo(Token token, NodoExpresion indice) {
        super(token); // token.getLexema = id
        //this.id = id;
        this.indice = indice;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // que el token sea de tipo Array
        // el token puede ser un atributo, una var local, un parametro, una cceso encadenado
        // que el nodoExpresion sea de tipo Int y < a la length
        // devuelvo el tipo interno del token (tipoArreglo.tipo)4

        // obtengo el tipo del token
        RegistroClase claseActual = ts.claseActual;
        String id = token.getLexema(); // esta es la variable a buscar

        // parametro o var local
        RegistroMetodo metodoActual = ts.getMetodoActual();

        if (metodoActual != null){
            // si no es atributo puede estar en los parametros del metodo
            // PARAMETROS METODO ------------------------------------------------------------------
            RegistroParametro parametro = metodoActual.getListaParametros().get(id);
            if (parametro != null){
                return verificarArreglo(token, parametro.getTipo(), ts);
            }
            // Puede ser var local del metodo
            // VAR LOCAL METODO --------------------------------------------------------------------
            RegistroVariable variableLocal = metodoActual.getListaVarLocales().get(id);
            if (variableLocal != null){
                return verificarArreglo(token, variableLocal.getTipo(), ts);
            }
        }

        //ATRIBUTO ----------------------------------------------------------------------------
        RegistroAtributo atributo = claseActual.getListaAtributos().get(id);
        if (atributo != null){
            // verifico que sea de tipo Array
            //Tipo tipo = atributo.getTipo();
            return verificarArreglo(token, atributo.getTipo(), ts);

        }

        // Puede estar en start
        if (ts.bloqueStart != null && metodoActual == null){
            RegistroVariable varEnStart = ts.bloqueStart.listaVariables.get(id);
            if (varEnStart != null){
                return verificarArreglo(token, varEnStart.getTipo(), ts);
            }
        }

        // si no es ni atributo de la clase, ni parametro del metodo ni var local del metodo -> ERROR
        if (ts.bloqueStart != null && metodoActual == null){
            throw new ErrorSemantico(token, "Id: '"+id+"' no declarado en start");
        }
        else {
            throw new ErrorSemantico(token, "Id: '"+id+"' no declarado en la clase: "+claseActual.nombre);
        }

       // return null;
    }

    public Tipo verificarArreglo(Token id, Tipo tipo, TablaSimbolos ts) throws ErrorSemantico {
        if (!tipo.esTipoArreglo()){
            throw new ErrorSemantico(token, "El id '"+id.getLexema()+"' debe ser un Array y es de tipo "+tipo.getNombreTipo());
        }
        // si es array veo la expresion debe ser de tipo int
        Tipo tipoIndice = indice.chequear(ts);

        // si el indice no es int -> ERROR
        if (!tipoIndice.getNombreTipo().equals("tInt")){
            throw new ErrorSemantico(token, "El indice del arreglo '"+id.getLexema()+"' debe ser de tipo tInt y es de tipo "+tipoIndice.getNombreTipo());
        }

        // en generacion de codigo voy a verificar la longitud, porque lo que este dentor del
        // arreglo puede ser simple como un int, o una expresion mas compleja

        // paso los chequeos devuelvo el tipo del arreglo definido (el tipo interno)

        TipoArreglo tipoArray = (TipoArreglo) tipo; // obtengo el tipo
        //Tipo tipoInterno = tipoArray.getTipoInterno();

        return tipoArray.getTipoInterno(); // retorno  el tipo del array definido
    }

}
