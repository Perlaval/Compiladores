package semantico.nodos.expresion;

import semantico.nodos.NodoEncadenadoOpt;
import semantico.tipos.Tipo;

public class NodoLlamadaConClassOrRec extends NodoExpresion {

    //1.
    private NodoArgumentosActuales nodoArgumentosActuales;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    //2.
    private NodoTipoPrimitivo nodoTipoPrimitivo;
    private NodoExpresion nodoExpresion;

    public NodoLlamadaConClassOrRec(NodoArgumentosActuales nodoArgumentosActuales, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoArgumentosActuales = nodoArgumentosActuales;
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    public NodoLlamadaConClassOrRec(NodoTipoPrimitivo nodoTipoPrimitivo, NodoExpresion nodoExpresion) {
        this.nodoTipoPrimitivo = nodoTipoPrimitivo;
        this.nodoExpresion = nodoExpresion;
    }

    @Override
    public Tipo chequear(){

        return null;
    }
}
