package semantico.nodos.expresion;

import semantico.ErrorSemantico;
import semantico.registros.RegistroAtributo;
import semantico.tipos.Tipo;

public class NodoAccesoVar extends NodoExpresion {

    private NodoId nodoId;
    private NodoAccesoVarRec nodoAccesoVarRec;

    public NodoAccesoVar(NodoId nodoId, NodoAccesoVarRec nodoAccesoVarRec) {
        this.nodoId = nodoId;
        this.nodoAccesoVarRec = nodoAccesoVarRec;
        this.tipoSintetizado = nodoId.getTipoSintetizado();
    }

    public NodoId getNodoId() {
        return nodoId;
    }

   public NodoAccesoVarRec getNodoAccesoVarRec() {
        return nodoAccesoVarRec;
    }



    @Override
    public Tipo chequear() throws ErrorSemantico {
        //1. No trae tipo heredado pq no viene de encadenado
        if (this.tipoHeredado == null){
            //1. AccesoVarRec -> [Expresion] EncadenadOpt
            if (nodoAccesoVarRec.getNodoExpresion() != null && this.nodoId.getToken().getTipo().getNombreTipo() != "tArray"){
                throw new ErrorSemantico(nodoId.getNroLinea(), nodoId.getNroColumna(), "El tipo de la variable " + token.getNombre() + "debe ser tArray");
            } else {
            //2. El tipoHeredado es un tipo primitivo
            if (nodoAccesoVarRec != null && nodoAccesoVarRec.isEncadenado()){
                throw new ErrorSemantico(nodoId.getNroLinea(), nodoId.getNroColumna(), "No se pueden encadenar ids con tipos primitivos: " + nodoId.getToken().getNombre());
            }
            }

        }
        //2. Viene de encadenado
        else{
            //Si tipoHeredado es null entonces estoy tratando de hacer eso id1.id2
            // Donde id1 es unq variable de tipoPrimitivo por lo que no se deberia poder encadenar con otro id
            // ENCADENADO PARA ACCESOVAR:
            // caso 1: <idArray>[tInt].llamadaMetodo -> con tipoInterno = tStr
            // caso 2: <idclass>.<idclass>...<idclass>.<tInt | tBool | tSrt>

            //2.1 Hay que comprobar que el tipoHeredado no es array pq esto esta mal= casa.habitaciones[1]
            if (nodoAccesoVarRec.getTipoSintetizado().getNombreTipo() == "tArray"){
                throw new ErrorSemantico(nodoId.getNroLinea(), nodoId.getNroColumna(), "La expresion no esta permitida - error desde NodoAccesoVar metodo chequear");
            }

            System.out.println("TIPO HEREDADO: " + nodoAccesoVarRec.getTipoSintetizado().getNombreTipo());
            //2.2 Hay que comprobar que id es una variable (visible) (local o heredada) de la clase del tipoContexto
            //Estaria en el caso: id.id.id donde cada los primeros los primeros 2 ids deben ser si o si de tipoReferencia
            RegistroAtributo variable = tipoHeredado.listaAtributos.get(nodoId.getToken().getNombre());
            if (variable == null) {
                throw new ErrorSemantico(nodoId.getNroLinea(), nodoId.getNroColumna(), nodoId.getToken().getNombre() + "no es un atributo de la clase" + tipoHeredado.getNombre());
            }
            else {
                if (!variable.isVisibilidad()){
                    throw new ErrorSemantico(nodoId.getNroLinea(), nodoId.getNroColumna(), nodoId.getToken().getNombre() + "no es un atributo -visible- de la clase" + tipoHeredado.getNombre());
                }
            }





        }

        return null;
    }

}
