package semantico.visitor;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.declaraciones.NodoBloqueMetodo;
import semantico.nodos.declaraciones.NodoDeclaracion;
import semantico.nodos.definiciones.NodoClase;
import semantico.nodos.definiciones.NodoDefinicion;
import semantico.nodos.definiciones.NodoImpl;
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
        //System.out.println("Chequeo program");
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
        //System.out.println("Chequeo start");

        ts.setMetodoActual(ts.getMetodoActual());
        //System.out.println("Metodo actual en start ?? "+ts.getMetodoActual().nombre);
        //nodoBloqueMetodo.chequear(ts);
        nodo.getNodoBloqueMetodo().accept(this);
        //return null;

    }

    @Override
    public void visit(NodoClase nodo) throws ErrorSemantico {
        // verificar si hace falta algun chequeo de clase adicional, sino dejarlo vacio
    }

    @Override
    public void visit(NodoImpl nodo) throws ErrorSemantico {
        //System.out.println("Chequeo impl");

        //System.out.println("Entre al nodoImpl con la clase: "+nodo.getImplClase());

        // seteo la clase actual para conexto en chequeos posteriores
        ts.claseActual = ts.getClase(nodo.getImplClase());

        for(NodoMetodo metodo : nodo.getListaMiembros()){
            //System.out.println("Entro al for de nodoImpl");
            //metodo.chequear(ts);
            metodo.accept(this);
        }

    }

    @Override
    public void visit(NodoMetodo nodo) throws ErrorSemantico {
        //System.out.println("Chequeo metodo: " + metodoActual.getNombre());
        if (!nodo.getMetodoActual().isConstructor()){
           // System.out.println("Entro a nodo metodo constructor");
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
        /*
        System.out.println("Bloque del metodo actual: "
                + nodoBloqueMetodo.getToken().getLexema()
                + " cantidad sentencias: "
                + nodoBloqueMetodo.getListaSent().size()); */
        for (NodoSentencia sentencia : nodoBloqueMetodo.getListaSent()) {
            sentencia.accept(this); //en este caso NodoSentencia es NodoRetorno
            //sentencia.chequear(ts);
        }
        //return null;

    }

    @Override
    public void visit(NodoAsignacion nodo) throws ErrorSemantico {
        // nodo asignacion tiene: nodo acceso = nodo expresion
        // reviso ambos tipos y verifico que sean ==

        Tipo tipoAcceso = nodo.getNodoAcceso().chequear(ts);
        String acceso = tipoAcceso.getNombreTipo();
        Tipo tipoExpresion = nodo.getNodoExpresion().chequear(ts);
        String expresion = tipoExpresion.getNombreTipo();

        //System.out.println("Nodo acceso tipo: "+tipoAcceso.getNombreTipo());
        //System.out.println("Nodo Expresion tipo: "+tipoExpresion.getNombreTipo());

        if (!acceso.equals(expresion)){ // deben ser mismos tipos
            throw new ErrorSemantico(nodo.getToken(), "En la asignacion se esperaba un tipo: "+acceso+" y se recibio: "+expresion);
            //throw new ErrorSemantico(nodo.getToken(), "Ambos lados de la asignacion deben tener el mismo tipo");
        }

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

    }

    @Override
    public void visit(NodoIf nodo) throws ErrorSemantico {
        // chequear() en la expresión retorna el tipo
        //System.out.println("Nodo: "+nodo.getToken().getLexema());
        Tipo tipoCond = nodo.getNodoCondicion().chequear(ts);

        //System.out.println("Estoy en un bloque if del impl de la calse: "+ts.getClaseActual().nombre);
        //System.out.println("Condición: " + nodo.getNodoCondicion().getClass().getName());
        //System.out.println("Tipo: " + tipoCond.getNombreTipo());


        if (!tipoCond.getNombreTipo().equals("tBool"))
            throw new ErrorSemantico (nodo.getNodoCondicion().getToken(), "La condicion del if debe ser de tipo bool");

        nodo.getNodoSentenciaThen().accept(this);
        if (nodo.getNodoSentenciaElse() != null)
            nodo.getNodoSentenciaElse().accept(this);
    }

    @Override
    public void visit(NodoRetorno nodo) throws ErrorSemantico {
        //System.out.println("Entre a chequear de ret");
        RegistroMetodo metodo = ts.getMetodoActual();
        //System.out.println("EL metodo actuaql es: "+metodo.getNombre());
        Tipo tipoRet = metodo.getTipoRetorno();
        //System.out.println("Su retorno es: "+tipoRet.getNombreTipo());

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
            //System.out.println("Expresion del ret: " + nodo.getNodoExpresionOpt().getClass().getSimpleName());
            Tipo tipoExpresion = nodo.getNodoExpresionOpt().chequear(ts);
            //System.out.println(nodo.getNodoExpresionOpt());
            if (tipoExpresion == null){
                throw new ErrorSemantico(nodo.getToken(),
                        "El metodo: "+ts.getMetodoActual().getNombre()+", deberia retornar: "+ts.getMetodoActual().getTipoRetorno().getNombreTipo());
            }
            //System.out.println("Retorno de la expresion: "+tipoExpresion.getNombreTipo());
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
