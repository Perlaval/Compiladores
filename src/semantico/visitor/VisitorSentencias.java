package semantico.visitor;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.declaraciones.NodoBloqueMetodo;
import semantico.nodos.declaraciones.NodoDeclaracion;
import semantico.nodos.definiciones.NodoClase;
import semantico.nodos.definiciones.NodoDefinicion;
import semantico.nodos.definiciones.NodoImpl;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoLlamadaMetodo;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoLlamadaMetodoEstatico;
import semantico.nodos.miembro.NodoMetodo;
import semantico.nodos.programa.NodoProgram;
import semantico.nodos.programa.NodoStart;
import semantico.nodos.sentencia.*;
import semantico.registros.RegistroMetodo;
import semantico.tipos.Tipo;

public class VisitorSentencias implements Visitor{

    private final TablaSimbolos ts;

    public VisitorSentencias(TablaSimbolos ts) {
        this.ts = ts;
    }

    @Override
    public void visit(NodoProgram nodo) throws ErrorSemantico {
        // hago recorrido para probar el nodoret
        // voy a chequear los impl de program
        // primero chequeo todas las definiciones (class e impl)
        for(NodoDefinicion def : nodo.getListaDefiniciones()) {
            def.accept(this);
        }
        nodo.getNodoStart().accept(this);
        //nodoStart.chequear(ts);
        //return null;

    }

    @Override
    public void visit(NodoStart nodo) throws ErrorSemantico {
        // recorrido para probar nodo ret

        ts.setMetodoActual(ts.getMetodoActual());
        //nodoBloqueMetodo.chequear(ts);
        nodo.getNodoBloqueMetodo().accept(this);
        //return null;

    }

    @Override
    public void visit(NodoClase nodo) throws ErrorSemantico {
        //Que deberia devolver el chequear de un nodo declaracion?
        for (NodoDeclaracion listaAtr: nodo.getNodoListaAtributos()){
            listaAtr.chequear(ts);
        }

    }

    @Override
    public void visit(NodoImpl nodo) throws ErrorSemantico {
        // seteo la clase actual para conexto en chequeos posteriores
        ts.claseActual = ts.getClase(nodo.getImplClase());

        for(NodoMetodo metodo : nodo.getListaMiembros()){
            //metodo.chequear(ts);
            metodo.accept(this);
        }

    }

    @Override
    public void visit(NodoMetodo nodo) throws ErrorSemantico {
        if (!nodo.getMetodoActual().isConstructor()){
            //System.out.println("retorno: "+metodoActual.getTipoRetorno().getNombreTipo());
        }
        //System.out.println("retorno: "+metodoActual.getTipoRetorno().getNombreTipo());
        ts.setMetodoActual(nodo.getMetodoActual());

        nodo.getNodoBloqueMetodo().accept(this);
    }

    @Override
    public void visit(NodoBloqueMetodo nodoBloqueMetodo) throws ErrorSemantico {
        // para llegar a nodoRet hago esto
        // Primero chequeo declaraciones de variables locales
        for (NodoDeclaracion decl : nodoBloqueMetodo.getListaDecVarLocal()) {
            decl.chequear(ts);
        }

        // Después chequeo las sentencias
        for (NodoSentencia sentencia : nodoBloqueMetodo.getListaSent()) {
            sentencia.accept(this); //en este caso NodoSentencia es NodoRetorno

        }


    }

    @Override
    public void visit(NodoAsignacion nodo) throws ErrorSemantico {

        Tipo tipoAcceso = nodo.getNodoAcceso().chequear(ts);

        Tipo tipoExpresion = nodo.getNodoExpresion().chequear(ts);

        if (!tipoAcceso.equals(tipoExpresion)) throw new ErrorSemantico(nodo.getToken(), "Error Semantico, tipos incompatibles en la asignación. "
                + "Se esperaba un valor de tipo "
                + tipoAcceso.getNombreTipo()
                + " pero se obtuvo un valor de tipo "
                + tipoExpresion.getNombreTipo());
    }

    @Override
    public void visit(NodoBloque nodo) throws ErrorSemantico {
        // tengo token y ListaSentencia
        // debo chequear todas esas sentencias
        for (NodoSentencia sentencia : nodo.getListaSent()) {
            sentencia.accept(this);
            //sentencia.chequear(ts);
        }
    }

    @Override
    public void visit(NodoFor nodo) throws ErrorSemantico {
        // for (<type> <idVariable1> in <idVariable2>) do
        //<sentencia>

        // <idVariable2> tipo iterator, objeto que implementa la interfaz iterator
        // el for se repite mientras Iterator.hasNext() == true
        // en cada iteracion <idVariable1> se asigna al resultado de la llamada al metodo next()
        // tipo de idvariable debe ser igual a tipo retorno iterator.next()
        // el iterador se inicializa antes de la primera iteracion del bucle
        // el iterador se puede modificar dentor del cuerpo del bucle (pero no es necesario para que funcione)


    }

    @Override
    public void visit(NodoIf nodo) throws ErrorSemantico {
        // chequear() en la expresión retorna el tipo
        Tipo tipoCond = nodo.getNodoCondicion().chequear(ts);


        if (!tipoCond.getNombreTipo().equals("tBool"))
            throw new ErrorSemantico (nodo.getNodoCondicion().getToken(), "La condicion del if debe ser de tipo bool");

        nodo.getNodoSentenciaThen().accept(this);
        if (nodo.getNodoSentenciaElse() != null)
            nodo.getNodoSentenciaElse().accept(this);
    }

    @Override
    public void visit(NodoRetorno nodo) throws ErrorSemantico {
        RegistroMetodo metodo = ts.getMetodoActual();
        Tipo tipoRet = metodo.getTipoRetorno();

        // ret;
        if (nodo.getNodoExpresionOpt() == null){
            // verifico que el retorno sea void
            if (!tipoRet.getNombreTipo().equals("Void")){
                throw new ErrorSemantico(nodo.getToken(),
                        "El metodo: "+metodo.getNombre()+ " deberia retornar: "+metodo.getTipoRetorno().getNombreTipo());
            }

        }
        else {
            // el ret de expresionOpt debe coincidir con tipoRet
            Tipo tipoExpresion = nodo.getNodoExpresionOpt().chequear(ts);
            if (tipoExpresion == null){
                throw new ErrorSemantico(nodo.getToken(),
                        "El metodo: "+ts.getMetodoActual().getNombre()+", deberia retornar: "+ts.getMetodoActual().getTipoRetorno().getNombreTipo());
            }
            // si no devuelve null debe devolver el mismo tipo
            if (!tipoRet.getNombreTipo().equals(tipoExpresion.getNombreTipo())){
                throw new ErrorSemantico(nodo.getToken(),
                        "Se esperaba un retorno: "+ts.getMetodoActual().getTipoRetorno().getNombreTipo()+", y se recibio: "
                                +tipoExpresion.getNombreTipo());
            }
        }
    }

    /* Es abstracto NodoSentencia, con el accept de nodoSentencia lo redirijo a la sentencia que es solicitada
    @Override
    public void visit(NodoSentencia nodo) throws ErrorSemantico {

    } */

    @Override
    public void visit(NodoSentenciaSimple nodo) throws ErrorSemantico {
        // ( Expresion )
        // debo chequear esa expresion
        nodo.getNodoExpresion().chequear(ts);
    }

    @Override
    public void visit(NodoWhile nodo) throws ErrorSemantico {
        Tipo tipoCond = nodo.getNodoExpresion().chequear(ts);
        if (!tipoCond.equals("tBool"))
            throw new ErrorSemantico(nodo.getNodoExpresion().getToken(), "La condicion debe ser de tipo bool");
        nodo.getNodoSentencia().accept(this);

    }
}
