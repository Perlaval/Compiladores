package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.encadenables.primario.NodoPrimario;
import semantico.tipos.Tipo;

public class NodoLlamadaMetodoEstatico extends NodoPrimario {

    private NodoLlamadaMetodo nodoLL;
    //private NodoId nodoId;

    public NodoLlamadaMetodoEstatico(Token token, NodoLlamadaMetodo nodoLL){
        super(token);
        this.nodoLL = nodoLL;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }

    public NodoLlamadaMetodo getNodoLL() {
        return nodoLL;
    }
}
