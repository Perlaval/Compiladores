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

    }

    @Override
    public void visit(NodoImpl nodo) throws ErrorSemantico {
        //System.out.println("Chequeo impl");

        //System.out.printf("Entre al nodoImpl con la clase: "+nodo.getImplClase()+" ");

        // seteo la clase actual para conexto en chequeos posteriores
        ts.claseActual = ts.getClase(nodo.getImplClase());

        for(NodoMetodo metodo : nodo.getListaMiembros()){
            //metodo.chequear(ts);
            metodo.accept(this);
        }

    }

    @Override
    public void visit(NodoMetodo nodo) throws ErrorSemantico {
        //System.out.println("Chequeo metodo: " + metodoActual.getNombre());
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
        /*
        System.out.println("Bloque del metodo actual: "
                + nodoBloqueMetodo.getToken().getLexema()
                + " cantidad sentencias: "
                + nodoBloqueMetodo.getListaSent().size()); */
        for (NodoSentencia sentencia : nodoBloqueMetodo.getListaSent()) {
            sentencia.accept(this); //en este caso NodoSentencia es NodoRetorno

        }


    }

    @Override
    public void visit(NodoAsignacion nodo) throws ErrorSemantico {


    }

    @Override
    public void visit(NodoBloque nodo) throws ErrorSemantico {

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
        System.out.println("Entre a chequear de ret");
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
            System.out.println("Expresion del ret: " + nodo.getNodoExpresionOpt().getClass().getSimpleName());
            Tipo tipoExpresion = nodo.getNodoExpresionOpt().chequear(ts);
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

    @Override
    public void visit(NodoSentencia nodo) throws ErrorSemantico {

    }

    @Override
    public void visit(NodoSentenciaSimple nodo) throws ErrorSemantico {

    }

    @Override
    public void visit(NodoWhile nodo) throws ErrorSemantico {
        Tipo tipoCond = nodo.getNodoExpresion().chequear(ts);
        if (!tipoCond.equals("tBool"))
            throw new ErrorSemantico(nodo.getNodoExpresion().getToken(), "La condicion debe ser de tipo bool");
        nodo.getNodoSentencia().accept(this);

    }
}
