package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.encadenables.primario.NodoPrimario;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;
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

        //1. chequear que el metodo pertenece a la clase
        if (!ts.existeMetodo(token.getLexema(), nodoLL.getToken().getLexema())) throw new ErrorSemantico(token, "Error Semantico, el metodo "
                + nodoLL.getToken().getLexema()
                + " no ha sido declarado en la clase "
                + token.getLexema());

        //Si el metodo pertenece a la clase
        Tipo tipoRetornoMet = this.nodoLL.chequear(ts);

        return tipoRetornoMet;


    }

    public NodoLlamadaMetodo getNodoLL() {
        return nodoLL;
    }

}
