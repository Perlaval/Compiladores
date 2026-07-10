package semantico.nodos;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoEncadenadoOpt extends Nodo{

    private NodoEncadenado nodoEncadenado;

    public NodoEncadenadoOpt(NodoEncadenado nodoEncadenado) {
        this.nodoEncadenado = nodoEncadenado;
        this.tipoSintetizado = nodoEncadenado.tipoSintetizado;
    }

    public Tipo chequear() {

        return null;
    }

    public NodoEncadenado getNodoEncadenado() {
        return nodoEncadenado;
    }


    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
