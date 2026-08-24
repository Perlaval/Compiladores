package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoVariableLocal extends NodoDeclaracion{


    public NodoVariableLocal(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        //System.out.println("Entre al chequear de nodo variable local de declaracion");
        return null;
    }

}
