package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.encadenables.primario.NodoPrimario;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;
import semantico.registros.RegistroParametro;
import semantico.tipos.Tipo;

public class NodoLlamadaMetodoEstatico extends NodoPrimario {
    //LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
    private NodoLlamadaMetodo nodoLL;
    //private NodoId nodoId;

    public NodoLlamadaMetodoEstatico(Token token, NodoLlamadaMetodo nodoLL){
        super(token); //token = idClass
        this.nodoLL = nodoLL;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // recido idClass . metodo

        // primero busco ese ese idClass, debe existir
        String claseActual = token.getLexema();
        RegistroClase clase = ts.getClase(claseActual);
        if (clase == null){
            throw new ErrorSemantico(token, "La clase: "+claseActual+" no ha sido declarada");
        }

        // obengo el nombre del metodo
        String metodo = nodoLL.getToken().getLexema();
        RegistroMetodo metodoActual = clase.getListaMetodos().get(metodo);
        if (metodoActual == null){
            throw new ErrorSemantico(token, "El metodo: "+metodo+" no existe en la clase: "+claseActual);
        }

        // ahora verifico que ese metodo sea estatico
        if (!metodoActual.esEstatico){
            throw new ErrorSemantico(token, "Se esperaba un metodo estatico");
        }

        // si es estatico verifio parametros y arg (al igual que con llamadametodo)
        if (metodoActual.getListaParametros().size() != nodoLL.getListaArg().size()) {
            throw new ErrorSemantico(token, "La cantidad de parametros recibidos no coincide con los esperados");
        }
        // si si coincide verifico uno por uno que tengan el mismo tipo
        // param[0] == listaArg[0]
        int i = 0;
        for (RegistroParametro parametro : metodoActual.getListaParametros().values()) {
            // obtengo el tipo de ese parametro
            Tipo tipoParam = parametro.getTipo();
            Tipo tipoArgActual = nodoLL.getListaArg().get(i).chequear(ts);

            if (!tipoParam.getNombreTipo().equals(tipoArgActual.getNombreTipo())) {
                throw new ErrorSemantico(token, "En los parametros se esperaba un tipo: " + tipoParam.getNombreTipo() +
                        " y se obtuvo: " + tipoArgActual.getNombreTipo());
            }
            i++;
        }

        // la funcion continuar cadena se encuentra en NodoEncadenable
        return continuarCadena(ts, metodoActual.getTipoRetorno());
    }

    /*@Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        //1. chequear que el metodo pertenece a la clase
        if (!ts.existeMetodo(token.getLexema(), nodoLL.getToken().getLexema())) throw new ErrorSemantico(token, "Error Semantico, el metodo "
                + nodoLL.getToken().getLexema()
                + " no ha sido declarado en la clase "
                + token.getLexema());

        //Si el metodo pertenece a la clase
        Tipo tipoRetornoMet = this.nodoLL.chequear(ts);

        return tipoRetornoMet;


    }*/

    public NodoLlamadaMetodo getNodoLL() {
        return nodoLL;
    }

}
