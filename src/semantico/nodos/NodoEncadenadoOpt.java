package semantico.nodos;

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

    public void setNodoEncadenado(NodoEncadenado nodoEncadenado) {
        this.nodoEncadenado = nodoEncadenado;
    }
}
