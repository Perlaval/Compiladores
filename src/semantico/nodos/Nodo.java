package semantico.nodos;

import lexico.Token;
import semantico.tipos.Tipo;

public abstract class Nodo {

    private int nroLinea;
    private int nroColumna;
    //Agrego token pero no se si sirve tener este atr
    public Token token;
    public Tipo tipoSintetizado;
    public Nodo nodoIzq;
    public Nodo nodoDer;

    /*//esto lo pongo por las dudas pero no se va aqui
    public Tipo tipoEstatico;
    public Tipo tipoDinamico;*/

    public Nodo(int nroLinea, int nroColumna, Token token) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.token = token;
    }

    public Nodo(Nodo izq, Nodo der) {
        this.nodoIzq = izq;
        this.nodoDer = der;

    }

    public Nodo() {

    }

    public Tipo getTipoSintetizado() {
        return tipoSintetizado;
    }

    public void setTipoSintetizado(Tipo tipoSintetizado) {
        this.tipoSintetizado = tipoSintetizado;
    }

    public abstract Tipo chequear();
}
