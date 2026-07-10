package semantico.nodos.expresion;

import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoPrimario extends NodoExpresion {

    //1. Primario -> ExpresionParentizada
    //private NodoExpresionParentizada nodoExpresionParentizada;

    //2. Primario -> AccesoSelf
    //private NodoAccesoSelf nodoAccesoSelf;

    //3. Primario -> AccesoVar
    //private NodoAccesoVar nodoAccesoVar;

    //4. Primario -> LlamadaMetodo
    //private NodoLlamadaMetodo llamadaMetodo;

    //5. Primario -> LlamadaMetodoEstatico - FALTA

    //6. Primario -> LlamadaConClassOr
    //private NodoLlamadaConClassOr nodoLlamadaConClassOr;

    private NodoExpresion nodoExpresion;

    public NodoPrimario(NodoExpresion nodoExpresion) {
        this.nodoExpresion = nodoExpresion;
    }

    /*public NodoPrimario(NodoAccesoSelf nodoAccesoSelf) {
        this.nodoAccesoSelf = nodoAccesoSelf;
    }

    public NodoPrimario(NodoAccesoVar nodoAccesoVar) {
        this.nodoAccesoVar = nodoAccesoVar;
    }

    public NodoPrimario(NodoLlamadaMetodo llamadaMetodo) {
        this.llamadaMetodo = llamadaMetodo;
    }

    public NodoPrimario(NodoLlamadaConClassOr nodoLlamadaConClassOr) {
        this.nodoLlamadaConClassOr = nodoLlamadaConClassOr;
    }*/

    @Override
    public Tipo chequear(TablaSimbolos ts) {

        return null;
    }
}
