package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.tipos.Tipo;

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
        return null;
    }
}
