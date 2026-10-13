package semantico.visitor;

import lexico.Token;
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
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

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
            def.accept(this); //aca se va actualizando la clase actual a medida que se recorre el ast
        }
        ts.setMetodoActual(null);
        ts.setClaseActual(null);
        nodo.getNodoStart().accept(this);
        //nodoStart.chequear(ts);
        //return null;

    }

    @Override
    public void visit(NodoStart nodo) throws ErrorSemantico { //REvisar

        // recorrido para probar nodo ret
        //System.out.println("Chequeo start");
        //ts.setClaseActual(null);
        //ts.setMetodoActual(ts.getMetodoActual());
        //System.out.println("Metodo actual en start ?? " +ts.getMetodoActual());
        //System.out.println("Clase actual en start ?? "+ts.getClaseActual().nombre);

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
        ts.setMetodoActual(nodo.getMetodoActual());

        // seteo quien ejecuta el metodo para verificar la visibilidad de los atributos
        ts.setClaseDelMetodoActual(ts.claseActual);

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

        // acceso = expresion

        Tipo tipoAcceso = nodo.getNodoAcceso().chequear(ts);
        Tipo tipoExpresion = nodo.getNodoExpresion().chequear(ts);

        if (tipoAcceso.esTipoArreglo()) {
            // el lado izquierdo es un arreglo: el derecho también tiene que serlo
            if (!tipoExpresion.esTipoArreglo()) {
                throw new ErrorSemantico(nodo.getToken(), "Tipos incompatibles en la asignación. "
                        + "Se esperaba un valor de tipo " + tipoAcceso.getNombreTipo()
                        + " pero se obtuvo un valor de tipo " + tipoExpresion.getNombreTipo());
            }

            TipoArreglo arrAcceso = (TipoArreglo) tipoAcceso;
            TipoArreglo arrExpresion = (TipoArreglo) tipoExpresion;

            // comparo el tipo interno de ambos arreglos
            Tipo internoAcceso = arrAcceso.getTipoInterno();
            Tipo internoExpresion = arrExpresion.getTipoInterno();

            if (!internoAcceso.getNombreTipo().equals(internoExpresion.getNombreTipo())) {
                throw new ErrorSemantico(nodo.getToken(), "Tipos incompatibles en la asignación. "
                        + "Se esperaba un arreglo de tipo " + internoAcceso.getNombreTipo()
                        + " pero se obtuvo un arreglo de tipo " + internoExpresion.getNombreTipo());
            }

        } else {
            // caso normal, ninguno de los dos es arreglo
            if (!tipoAcceso.getNombreTipo().equals(tipoExpresion.getNombreTipo())) {
                throw new ErrorSemantico(nodo.getToken(), "Tipos incompatibles en la asignación. "
                        + "Se esperaba un valor de tipo " + tipoAcceso.getNombreTipo()
                        + " pero se obtuvo un valor de tipo " + tipoExpresion.getNombreTipo());
            }
        }

        /*
        //System.out.println("En nodo asignacion, tipo acceso");
        Tipo tipoAcceso = nodo.getNodoAcceso().chequear(ts);
        // si tipo acceso es de tipo arreglo veo el tipo interno
        if (tipoAcceso.esTipoArreglo()){
            TipoArreglo tipoArr = (TipoArreglo) tipoAcceso;
            Tipo tipoInterno = tipoArr.getTipoInterno();
        }

        Tipo tipoExpresion = nodo.getNodoExpresion().chequear(ts);
        System.out.println("Tipo expresion: "+tipoExpresion.getNombreTipo());

        if (!tipoAcceso.getNombreTipo().equals(tipoExpresion.getNombreTipo())) throw new ErrorSemantico(nodo.getToken(), "Tipos incompatibles en la asignación. "
                + "Se esperaba un valor de tipo "
                + tipoAcceso.getNombreTipo()
                + " pero se obtuvo un valor de tipo "
                + tipoExpresion.getNombreTipo());


         */
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

        // NodoFor(tFor, tipoVar, tVariable, tIterador, cuerpoFor);
        // tipoVar es tipo de idVariable1
        // tVariable es idVariable1
        // iIterador idVariable2
        // cuerpo for es sentencia


        // for (<type> <idVariable1> in <idVariable2>) do
        //<sentencia>

        // <idVariable2> tipo iterator, objeto que implementa la interfaz iterator
        // como solo array es de tipo iterator, verifico que tipo de idVariable2 sea Array
        Token idVariable2 = nodo.getNodoIterador();
        RegistroVariable varIterator2 = ts.getVariable(idVariable2.getLexema());
        if (varIterator2 == null){
            throw new ErrorSemantico(nodo.getNodoIterador(), "Variable: '"+idVariable2.getLexema()+"' no declarada");
        }
        Tipo tipoIdVariable2 = varIterator2.tipo;
        if (!tipoIdVariable2.esTipoArreglo()){
            throw new ErrorSemantico(nodo.getNodoIterador(), "EL iterador del for debe ser de tipo Array");
        }
        TipoArreglo tipoArr = (TipoArreglo) tipoIdVariable2;
        Tipo tipoInterno = tipoArr.getTipoInterno();
        // el for se repite mientras Iterator.hasNext() == true
        // en cada iteracion <idVariable1> se asigna al resultado de la llamada al metodo next()
        // tipo de idvariable debe ser igual a tipo retorno iterator.next()
        Tipo tipoIdVariable1 = nodo.getTipoVar();
        if (!tipoIdVariable1.equals(tipoInterno)){
            // si no son iguales error
            throw new ErrorSemantico(nodo.getNodoVariable(), "La variable '"+nodo.getNodoVariable().getLexema()+"' debe ser del tipo '"+tipoInterno.getNombreTipo()
            +"' y es de tipo "+tipoIdVariable1.getNombreTipo());
        }
        // el iterador se inicializa antes de la primera iteracion del bucle
        // el iterador se puede modificar dentor del cuerpo del bucle (pero no es necesario para que funcione)

        // registro temporal de la idVariable1, que solamente vive mientras vive el for
        String variable1 = nodo.getNodoVariable().getLexema();

        // chequear si puedo pisar esa variable, es decir no verifico que exista ni nada, la creo y elimino durante el for
        RegistroVariable varAnterior = ts.metodoActual.listaVarLocales.get(variable1);

        RegistroVariable varFor = ts.crearRegVar(variable1, tipoIdVariable1); // la creo y luego la elimino
        varFor.setTokenVarLocal(nodo.getNodoVariable()); // no le seteo pos porque no es un lugar fijo, se resuelve en ada iteracion del for

        ts.metodoActual.listaVarLocales.put(variable1, varFor);

        // ahora voy con el cuerpo del bucle
        // con un try porque puede romperse lo que venga dsp
        try {
            nodo.getCuerpo().accept(this);
        } finally {
            // vuelvo al estado anterior
            if (varAnterior != null){
                ts.metodoActual.listaVarLocales.put(variable1, varAnterior);
            }
            else {
                ts.metodoActual.listaVarLocales.remove(variable1);
            }
        }

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
        if (!tipoCond.getNombreTipo().equals("tBool")) {
            throw new ErrorSemantico(nodo.getNodoExpresion().getToken(), "La condicion debe ser de tipo bool");
        }
        nodo.getNodoSentencia().accept(this);

    }
}
