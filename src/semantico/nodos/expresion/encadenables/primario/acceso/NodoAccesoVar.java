package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
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
        return null;
    }

}
