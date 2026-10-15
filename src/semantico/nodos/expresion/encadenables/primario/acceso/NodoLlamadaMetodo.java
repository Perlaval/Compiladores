package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;
import semantico.tipos.TipoPrimitivo;

import java.util.ArrayList;

public class NodoLlamadaMetodo extends NodoAcceso {

    //private final NodoId nodoId;
    private final ArrayList<NodoExpresion> listaArg;
    //private final NodoEncadenado nodoEncadenado;

    public NodoLlamadaMetodo(Token token, ArrayList<NodoExpresion> listaArg /*, NodoEncadenado nodoEncadenado*/) {
        super(token); // token.getLexema = idMetodo - LlamadaMetodo: idMetodo(arg1,arg2,..)
        //this.nodoId = nodoId;
        this.listaArg = listaArg;
        //this.nodoEncadenado = nodoEncadenado;
    }

    /*public NodoId getNodoId() {
        return nodoId;
    }*/

    public ArrayList<NodoExpresion> getListaArg() {
        return listaArg;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // deberia fijarme si ese metodo pertenece a la clase actual
        // opodria tener self.motor.calcular();
        // entonces debo verificar que en la clase de motor eszte ese metodo y devovler el tipo de ese metodo

        RegistroClase claseActual = ts.getClaseActual();
        String nombreMetodo = token.getLexema();

        // obtengo el metodo
        RegistroMetodo metodoActual = claseActual.getListaMetodos().get(nombreMetodo);

        // si no existe error
        if (metodoActual == null) {
            throw new ErrorSemantico(token, "El metodo: "
                    + nombreMetodo + " no existe en la clase: " + claseActual.getNombre());
        }

        // VERIFICAR QUE LOS PARAMETROS COINCIDAN CON LA FUNCION (en cantidad y tipos)
        // la cant de parametros debe ser igual a la lista de argumentos actuales que recibo
        //System.out.println("Cantidad de aprametros del metodo: " + metodoActual.getListaParametros().size());
        //System.out.println("La lista de argumentos actuales es: " + listaArg.size());
        verificarParametrosMetodo(ts, metodoActual, listaArg, nombreMetodo);

        // veo si tiene encadenado en la funcion continuar cadena, si es asi avanzo hasta llegar a un tipo primitivo
        // la funcion continuar cadena se encuentra en NodoEncadenable
        return continuarCadena(ts, metodoActual.getTipoRetorno());
    }

    // lo llamo desde nodoEncadenable
    public Tipo tipoMetodoArray(TablaSimbolos ts, TipoArreglo tipoArreglo) throws ErrorSemantico {
        String nombreMetodo = token.getLexema();
        Tipo tipoRetorno;

        switch (nombreMetodo){
            case "hasNext":
                if (!listaArg.isEmpty()){
                    throw new ErrorSemantico(token, "El metodo hasNext() no recibe parametros");
                }
                tipoRetorno = new TipoPrimitivo("tBool");
                break;

            case "next": // PUEDE SER INT STR O BOOL
                if (!listaArg.isEmpty()){
                    throw new ErrorSemantico(token, "El metodo next() no recibe parametros");
                }
                tipoRetorno = tipoArreglo.getTipoInterno();
                break;
            case "length":
                if (!listaArg.isEmpty()){
                    throw new ErrorSemantico(token, "El metodo length() no recibe parametros");
                }
                tipoRetorno = new TipoPrimitivo("tInt");
                break;
            default:
                throw new ErrorSemantico(token, "La clase Array no posee el metodo: "+nombreMetodo);
        }

        return continuarCadena(ts, tipoRetorno);
    }
}
