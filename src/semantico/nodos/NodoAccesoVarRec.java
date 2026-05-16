package semantico.nodos;

import semantico.ErrorSemantico;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

public class NodoAccesoVarRec extends Nodo{

    private boolean encadenado = false;
    private NodoExpresion nodoExpresion;
    private NodoEncadenadoOpt nodoEncadenadoOpt;

    public NodoAccesoVarRec(NodoExpresion nodoExpresion, NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
        this.nodoExpresion = nodoExpresion;
        this.tipoSintetizado = nodoEncadenadoOpt.tipoSintetizado;
    }

    public NodoAccesoVarRec(NodoExpresion nodoExpresion) {
        this.nodoExpresion = nodoExpresion;
    }

    public NodoAccesoVarRec(NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    @Override
    public void chequear() {

    }

    public void chequear(Tipo tipoContexto) throws ErrorSemantico {
        if (nodoExpresion != null){
            //1. verifico si el tipo del id es tArray
            if (tipoContexto.getNombreTipo() == "tArray"){
                TipoArreglo tipoArreglo = (TipoArreglo) tipoContexto;

                //2. Si encadenadoOpt != null
                //Chequeo que el tipoInterno del arreglo sea solo Int o Str pq solo estos tienen metodos que pueden ser llamados con encadenado.
                if (nodoEncadenadoOpt != null){

                    if (tipoArreglo.getTipoInterno().getNombreTipo() !=  "tStr"){
                        throw new ErrorSemantico(nodoEncadenadoOpt.nroLinea, nodoEncadenadoOpt.nroColumna, "El tipo interno que ha sido declarado en el arreglo no soporta encadenado");

                    }
                    //3. Si encadenadoOpt no es null entonces ya viene con encadenado
                    //Esta variable me sirve para NodoAccesoVar
                    encadenado = true;
                }
                else {
                    if (!tipoArreglo.getTipoInterno().esTipoPrimitivo()){
                        throw new ErrorSemantico(nodoExpresion.nroLinea, nodoExpresion.nroColumna, "El arreglo solo soporta tipo primitivo");
                    }
                }

            }
            //Asigno tipo sintetizado de encadenadoOpt
            setTipoSintetizado(nodoEncadenadoOpt.getTipoSintetizado());
        }
    }

    public NodoExpresion getNodoExpresion() {
        return nodoExpresion;
    }

    public void setNodoExpresion(NodoExpresion nodoExpresion) {
        this.nodoExpresion = nodoExpresion;
    }

    public NodoEncadenadoOpt getNodoEncadenadoOpt() {
        return nodoEncadenadoOpt;
    }

    public void setNodoEncadenadoOpt(NodoEncadenadoOpt nodoEncadenadoOpt) {
        this.nodoEncadenadoOpt = nodoEncadenadoOpt;
    }

    public boolean isEncadenado() {
        return encadenado;
    }

    public void setEncadenado(boolean encadenado) {
        this.encadenado = encadenado;
    }


}
