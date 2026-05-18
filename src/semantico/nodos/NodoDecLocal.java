package semantico.nodos;

import lexico.Token;
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;

public class NodoDecLocal extends NodoDeclaracion{


    public NodoDecLocal(Token tdeclaracion) {
        super(tdeclaracion);
    }
}
