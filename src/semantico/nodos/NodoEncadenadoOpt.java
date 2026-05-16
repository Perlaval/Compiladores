package semantico.nodos;

import semantico.tipos.Tipo;

public class NodoEncadenadoOpt extends Nodo{

    private NodoEncadenado nodoEncadenado;

    public NodoEncadenadoOpt(NodoEncadenado nodoEncadenado) {
        this.nodoEncadenado = nodoEncadenado;
        this.tipoSintetizado = nodoEncadenado.tipoSintetizado;
    }

    @Override
    public void chequear() {

    }

    public NodoEncadenado getNodoEncadenado() {
        return nodoEncadenado;
    }

    public void setNodoEncadenado(NodoEncadenado nodoEncadenado) {
        this.nodoEncadenado = nodoEncadenado;
    }
}
