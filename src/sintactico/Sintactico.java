package sintactico;

import lexico.ErrorLexico;
import lexico.Token;
import lexico.Lexico;
import semantico.Ast;
import semantico.ValidarDeclaracion;
import semantico.nodos.*;
import semantico.nodos.NodoEncadenado;
import semantico.nodos.expresion.*;
import semantico.nodos.sentencia.*;
import semantico.tipos.*;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.registros.*;

import java.lang.reflect.Array;
import java.util.ArrayList;


// analizador sintactico
public class Sintactico {
    //private List<Token> listaTokens; //Lista de tokens que obtuve del lexico
    private Lexico lexico;
    private int puntero;
    private Token token;
    private Token next;
    private boolean lookahead = false;
    TablaSimbolos ts = new TablaSimbolos();
    Ast ast;

    //constructor
    /*public Sintactico(List<Token> listaTokens){
        this.listaTokens = listaTokens;
        this.puntero = 0;
        this.token = listaTokens.get(0);
    }*/
    public Sintactico(Lexico lexico){
        this.lexico = lexico;

    }

    //clase
    public void analizador() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        this.token = lexico.analizador();
        // Program -> ListaDefiniciones Start
        NodoProgram program = program(); //program es la raiz de mi ast
        // si sale de program es porque hizo match con $ entonces devolver Exito!

        ast = new Ast(program);

    }

    // Gramatica ----------------------------------------------------------------------------------------------

    //Program -> ListaDefiniciones Start
    private NodoProgram program() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tProgram = token;
        ArrayList<NodoDefinicion> listaDefiniciones = listaDefiniciones(new ArrayList<NodoDefinicion>());
        //salgo de LS, voy a imprimir las clases:
        ts.imprimirClases();
        // si es lambda va directo a start
        RegistroStart metodoStart = new RegistroStart();
        NodoStart start = start();
        System.out.println("token final: "+ token.getTipo());
        match("EOF"); // ver si tiene que ser $ o EOF
        return new NodoProgram(tProgram, listaDefiniciones, start);

    }

    // Start -> start BloqueMetodo
    private NodoStart start() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // matcheo start asi avanza
        //match("prStart"); // esto tmb verificar porque nose si start era una palabra reservada (pregintar a profe)
        if (token.getLexema().equals("start")){
            Token tStart = token;
            // deberia matchear idMetVar, porque start al no ser reservada la toma como idMetVar
            match("idMetVar"); //consumo start y voy a bloque
            // ahora mi clase actual es el metodo start
            NodoBloqueMetodo bloqueMetodo = bloqueMetodo();
            return new NodoStart(tStart, bloqueMetodo);
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba start y se enontro "+token.getTipo());
        }

    }

    // ListaDefiniciones -> Clase ListaDefiniciones | Implementacion ListaDefiniciones | lambda
    private ArrayList<NodoDefinicion> listaDefiniciones(ArrayList<NodoDefinicion> listaDef) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es recursiva, por lo que voy a agregar un while, mientras lea la palabra reservada class o impl, tiene que volver a entrar
        if (token.getTipo().equals("prClass") || token.getTipo().equals("prImpl")){
            if (token.getTipo().equals("prClass")){
                listaDef.add(clase());
                return listaDefiniciones(listaDef);
            }
            else {
                listaDef.add(impl());
                return listaDefiniciones(listaDef);
            }
        }
        return listaDef;
    }

    // Class -> class idClass HerenciaOpt { listaAtributos }
    private NodoClase clase() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("prClass");
        // si el id esta en las clases predefinidas -> error
        if (ts.isNombreClasePredefinida(token.getLexema())){
            throw new ErrorSemantico(token.getFila(), token.getColumna(), "La clase: "+token.getLexema()+" No se puede redefinir, es una clase predefinida");
        }
        if (token.getTipo().equals("idClass")){
            Token id = token; // guardo el token para guardarlo en la ts, porque cuando matcheo avanzo entonces lo pierdo
            match("idClass");
            RegistroClase clase;
            if (ts.noEstaTs(id.getLexema())){
                // no esta guardada la clase en la TS
                if (token.getTipo().equals("dosPuntos")){ // tiene herencia
                    String superClase = herenciaOpt();
                    clase = ts.crearRegClase(id.getLexema(), superClase);
                }
                else {
                    clase = ts.crearRegClase(id.getLexema(), ""); // si no tiene herencia dejamos la cadena vacia, para no poner null y que se rompa

                }
                ts.tablaClases.put(clase.getNombre(), clase);
            }
            else { // si ya esta en la TS verifico que no haya redefinicion de herencia
                // veo si tiene herencia, si tiene debe ser la misma
                clase = ts.getClase(id.getLexema());
                System.out.println("Entre aca con: "+clase.getNombre());
                if (token.getTipo().equals("dosPuntos")){
                    String superClase = herenciaOpt();
                    if (!clase.heredaDe.equals(superClase)){
                        throw new ErrorSemantico(token.getFila(), token.getColumna(), "Redefinicion de herencia para la clase: "+clase.getNombre());
                    }
                }

            }
            // contexto para atributos
            ts.claseActual = clase;
            match("llaveAbre");
            ArrayList<NodoDeclaracion> listaAtr = listaAtributos(new ArrayList<NodoDeclaracion>()); // si lo que viene es } es porque era lambda
            // imprimo los atributos de esa clase
            System.out.println("Atributos de la clase: "+ ts.claseActual.listaAtributos.toString());
            match("llaveCierra");

            // salgo de la clase actual
            ts.claseActual = null;
            return new NodoClase(id, listaAtr);
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idClass y se recibio: "+token.getTipo());
        }
            /*
            match("idClass");
            RegistroClase clase;

            if (ts.tablaClases.containsKey(id.getLexema())){
                // verifico la herencia que debe ser la misma
                clase = ts.getClase(id.getLexema());
                if (token.getTipo().equals("dosPuntos")){ // tiene herencia
                    String superClase = herenciaOpt(); // obtengo esa herencia y la comparo con la que ya tengo en mi ts
                    if (!clase.heredaDe.equals(superClase)){ // si son iguales esta todo correcto
                        throw new ErrorSemantico(token.getFila(), token.getColumna(), "Redefinicion herencia inconsistente");
                    }
                }
            }
            else {
                clase = new RegistroClase(id.getLexema());
                //clase.setNombre(id.getLexema());
                ts.tablaClases.put(clase.getNombre(), clase);
                if (token.getTipo().equals("dosPuntos")){
                    // obtengo la superClase
                    String superClase = herenciaOpt();
                    // verifico que no este haciendo esto A : A
                    if (clase.getNombre().equals(superClase)){
                        throw new ErrorSemantico(token.getFila(), token.getColumna(), "No puede heredar de la misma clase");
                    }
                    else {
                        // verifico herencia circular
                        if (ts.tablaClases.containsKey(superClase)){
                            // obtengo esa clase y me fijo su herencia
                            RegistroClase claseHerencia = ts.getClase(superClase);
                            if (claseHerencia.getHeredaDe().equals(clase.getNombre())){
                                throw new ErrorSemantico(token.getFila(), token.getColumna(), "Error, herencia circular");
                            }
                        }
                        else {

                            clase.setHeredaDe(superClase);
                        }
                    }
                }
                // si no me vienen : es porque no tiene herencia y hereda de object
                else {
                    clase.setHeredaDe("Object");
                }
                System.out.println("Nombre clase: "+clase.getNombre());
                System.out.println("hereda de: "+ clase.getHeredaDe());
            //}
            // contexto para atributos
            ts.claseActual = clase;
            match("llaveAbre");
            listaAtributos(); // si lo que viene es } es porque era lambda
            // imprimo los atributos de esa clase
            System.out.println("Atributos de la clase: "+ ts.claseActual.listaAtributos.toString());
            match("llaveCierra");

            // salgo de la clase actual
            ts.claseActual = null;
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idClass y se recibio: "+token.getTipo());
        } */
        // chequear cuando se intenta declarar una clase cuyo nombre es un tipo primitivo

    }

    // HerenciaOpt -> Herencia | lambda
    // aca como puede ser opcional si va a herencia o no, necesito los primeros y siguientes
    private String herenciaOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico{
        // Sig(HerenciaOpt) = { { }
        // si el token que viene no esta en los primeros de herencia es porque o vino {, entonces aca no hace nada, o vino algo mal
        // entonces verifico con los primeros
        String heredaDe = null;
        if (token.getTipo().equals("dosPuntos")) {
             heredaDe = herencia();
        }
        return heredaDe;
    }

    // ListaAtributos -> Atributo ListaAtributos | lambda
    private ArrayList<NodoDeclaracion> listaAtributos(ArrayList<NodoDeclaracion> listaAtr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = token.getTipo();
        // si lo que viene no esta en los primeros de Atributo es porque listaAtributos es lambda entonces aca no hace nada
        // es recursiva, por lo tanto siempre que venga alguno de los primeros de A vuelvo a entrar
        // como puede no tener prPub, tambien puedo ir directamente a Tipo
        if (tipo.equals("prPub") | tipo.equals("tStr") | tipo.equals("tBool") | tipo.equals("tInt") | tipo.equals("idClass")) { //| tipo.equals("Array")
            ArrayList<NodoDeclaracion> listaAtrNueva = atributo(listaAtr);
            // actualizo el tipo
            //tipo = token.getTipo();
            return listaAtributos(listaAtrNueva);
        }
        return listaAtr;
    }

    // Impl -> impl idClass { ListaMiembros }
    private NodoImpl impl() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tImpl = token;
        match("prImpl");
        if (ts.isNombreClasePredefinida(token.getLexema())){
            throw new ErrorSemantico(token.getFila(), token.getColumna(), "La clase: "+token.getLexema()+" No se puede redefinir, es una clase predefinida");
        }
        if (token.getTipo().equals("idClass")){
            if (ts.noEstaTs(token.getLexema())){
                // no esta esa clase, la agrego
                RegistroClase clase = new RegistroClase(token.getLexema());
                ts.tablaClases.put(clase.getNombre(), clase);
                ts.claseActual = clase;
            }
            // obtengo la clase actual para guardarle los metodos
            RegistroClase clase = ts.getClase(token.getLexema());
            ts.claseActual = clase;
            match("idClass");
            // entonces ahora voy a ir a lista miembros con la clase actual
            match("llaveAbre");
            ArrayList<NodoMetodo> listaMiembros = listaMiembros(new ArrayList<NodoMetodo>());
            match("llaveCierra");
            // salgo de este impl, vuelvo la clase actual a null
            ts.claseActual = null;
            ts.metodoActual = null;
            return new NodoImpl(tImpl, clase.nombre, listaMiembros);

        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idClass");
        }

    /* // entonces ahora voy a ir a lista miembros con la clase actual
        match("llaveAbre");
        ArrayList<NodoMetodo> listaMiembros = listaMiembros(new ArrayList<NodoMetodo>());
        match("llaveCierra");
        // salgo de este impl, vuelvo la clase actual a null
        ts.claseActual = null;
        ts.metodoActual = null;*/

    }

    // ListaMiembros -> Miembro ListaMiembros | lambda
    private ArrayList<NodoMetodo> listaMiembros(ArrayList<NodoMetodo> listaMet) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si lo que viene esta en los primeros de miembro es porque lista miembro no es lambda
        // Prim(E) = { st, . , lambda}
        if (esPrimeroMiembro(token.getTipo()) | token.getTipo().equals("prFn")){
            listaMet.add(miembro());
            return listaMiembros(listaMet);
        }
        return listaMet;
    }

    // Herencia -> : Tipo
    private String herencia() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("dosPuntos");
        String superClase;
        Tipo tipoSuperClase;
        // verifica si o si que lo que se recibe es un idClass
        // es un error heredar o redefinir: Int, Str, Bool
        
        // ordenar eso
        if (token.getTipo().equals("idClass") || token.getLexema().equals("Int") || token.getLexema().equals("Str") || token.getLexema().equals("Bool")
            || token.getTipo().equals("Iterator") || token.getTipo().equals("IO") || token.getTipo().equals("Array")){
            if (ts.herenciaValida(token.getLexema())){
                tipoSuperClase = tipo(); // como es idClass va a ir a TipoReferencia
                superClase = tipoSuperClase.getNombreTipo();
            }
            else {
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "Es un error heredar de la clase: "+token.getLexema());
            }
        }
        else {
            throw new ErrorSemantico(token.getFila(), token.getColumna(), "Se esperaba un id de clase y se encontro: "+token.getLexema());
        }
        /*
        ts.herenciaValida(token.getLexema())){ // ver tmb para los otros casos (Int, Bool, Iterator, etc)
            tipoSuperClase = tipo(); // como es idClass va a ir a TipoReferencia
            superClase = tipoSuperClase.getNombreTipo();
        }*/

        //tipo();
        return superClase;
    }

    // Miembro -> Metodo | Constructor
    private NodoMetodo miembro() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // tengo dos opciones o voy a metodo o voy a constructor
        // cuando no tengo st puede venir fn
        if (token.getTipo().equals("prSt") | token.getTipo().equals("prFn")){
            return metodo();
        }
        else {
            // si va a constructor verifico si esa clase ya tiene constructor, si es asi largo error
            if (ts.claseActual.constructor != null){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "El constructor de la clase: " + ts.claseActual.nombre + " ya ha sido declarado");
            }
            return constructor();

        }
    }

    // Metodo -> FormaMetodoOpt fn TipoMetodoOpt idMetAt ArgumentosFormales BloqueMetodo
    private NodoMetodo metodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        boolean estatico = formaMetodoOpt(); // true = estatico, false = no estatico
        match("prFn");
        // si el tipo de retorno es vacio es porque es void
        Tipo tipo = tipoMetodoOpt(); // va a guardar en la ts el tipo de retorno de la funcion
        // aca es donde guardo el metodo con forma, tipo nombre
        Token tMetodo = token;
        if (token.getTipo().equals("idMetVar")){

            if (!ts.validarNombre(ValidarDeclaracion.Definicion.METODO, token.getLexema())){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "El nombre: "
                        +token.getLexema()+" no es válido ya que representa un tipo especial");
            }

            // analizar los casos de redefinicion, de metodos heredados
            // en la misma clase no puedo tener dos metodos con el mismo nombre
            if (ts.claseActual.listaMetodos.containsKey(token.getLexema())){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "Ya existe un metodo con el nombre: "
                        +token.getLexema()+" en el impl de la clase: "+ts.claseActual.getNombre());
            }
            // caso base agregar el metodo a la lista de metodos de la clase
            RegistroMetodo metodo = ts.crearRegMetodo(token.getLexema(),estatico,tipo);
            match("idMetVar");
            ts.claseActual.listaMetodos.put(metodo.getNombre(), metodo);
            // seteo el metodo actual para los parametros y varlocales
            ts.metodoActual = metodo;
            System.out.println("Metodo: "+metodo.getNombre());
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idMetVar y se recibio: "+token.getTipo());
        }
        // ya guarde el metodo en la ts.claseactual.listametodos, ahora voy a sus parametros y varlocales
        // voy a argumentos formales con el metodoactual
        ArrayList<NodoDeclaracion> listaArg = argumentosFormales(new ArrayList<NodoDeclaracion>()); //voy a guardar en la hash de listaParametros todos los argumentos
        // salgo de argumentos imprimo a ver que guardo

        // voy a ir a bloque metodo con el metodoactual
        //bloqueMetodo();
        ts.metodoActual.imprimirMetodo(ts.metodoActual, ts.claseActual);
        return new NodoMetodo(tMetodo, listaArg, bloqueMetodo());

    }

    // formaMetodoOpt -> formaMetodo | lambda
    private boolean formaMetodoOpt() throws ErrorSintactico, ErrorLexico {
        // si el token que viene esta en los primeros de formaMetodo tengo que entrar
        // si viene otra cosa no hace nada y si no viene nada no entra y es valido
        if (token.getTipo().equals("prSt")){
            return formaMetodo();
        }
        return false; // si no entra es porque no es estatico, devuelo false
    }

    // TipoMetodoOpt -> TipoMetodo | lambda
    private Tipo tipoMetodoOpt() throws ErrorSintactico, ErrorLexico {
        // si el tokoen esta en los primeros de tipoMetodo entro
        if (esPrimeroTipoMetodo(token.getTipo())){
            return tipoMetodo();
        }
        return new TipoVoid();
    }

    // ArgumentosFormales -> ( ListaArgumentosFormalesOpt )
    private ArrayList<NodoDeclaracion> argumentosFormales(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("parAbre");
        ArrayList<NodoDeclaracion> listaArgNueva = listaArgumentosFormalesOpt(listaArg);
        match("parCierra");
        return listaArgNueva;
    }

    // Constructor -> . ArgumentosFormales BloqueMetodo
    private NodoMetodo constructor() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tConst = token;
        match("pto");
        //ts.claseActual.inConstructor = true;
        ts.claseActual.constructor = new Constructor();
        ts.metodoActual = ts.claseActual.constructor;
        //System.out.println("Voy a arg formales con el constructor: "+ts.metodoActual);
        ArrayList<NodoDeclaracion> listaArg = argumentosFormales(new ArrayList<NodoDeclaracion>());
        //System.out.println("Voy al bloque metodo del constructor de la clase: "+ts.claseActual.constructor.getNombre()+" con el token: "+token.getTipo());
        //bloqueMetodo();
        ts.metodoActual.imprimirMetodo(ts.metodoActual, ts.claseActual);
        return new NodoMetodo(token, listaArg, bloqueMetodo());
        //ts.claseActual.constructor.active = true;
        //ts.claseActual.inConstructor = false;
    }

    // Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar ;
    private ArrayList<NodoDeclaracion> atributo(ArrayList<NodoDeclaracion> listaAtr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // guardo en la TS el atributo con la posicion, visibilidad, tipo, nombre
        boolean vis = visibilidadOpt(); // si no entra a visibilidad es de tipo priv
        Tipo tipo = tipo();
        ArrayList<NodoDeclaracion> listaAtrNueva = listaDeclaracionVar(vis, tipo, listaAtr);
        // salgo de aca y voy a haber guardado por ej para: Int a,b
        // pos visibilidad tipo nombre
        //| 0 | pub | Int a | b |
        match("ptoComa");
        return listaAtrNueva;
    }

    // VisibilidadOpt -> Visibilidad | lambda
    private boolean visibilidadOpt() throws ErrorSintactico, ErrorLexico {
        // si lo que viene esta en los primeros de visibilidad entro
        boolean vis = false;
        if (token.getTipo().equals("prPub")){
            return visibilidad();
        }
        return vis;
    }

    // Tipo -> TipoPrimitivo | TipoReferencia | TipoArreglo
    private Tipo tipo() throws ErrorSintactico, ErrorLexico {
        // si lo que viene esta en los primeros de tipo primitivo entro ahi
        //String hereda = null;
        //String tipo;
        Tipo tipo = null;
        if (esPrimeroTipoPrimitivo(token.getTipo())){
            return tipoPrimitivo();
        }
        else {
            if (token.getTipo().equals("idClass")){
                if (token.getLexema().equals("Array")){
                    return tipoArreglo();
                }
                else{
                    return tipoReferencia();
                }

            }
            //else {
                //if (token.getTipo().equals("prArray")){
                //    return tipoArreglo();
                //}
            //}
        }
        //return hereda;
        return tipo;
    }

    // ListaDeclaracionVar -> idMetAt ListaDeclaracionesVarRec
    // misma funcion para guardar las variables de los atributos y las variables locales de un metodo
    private ArrayList<NodoDeclaracion> listaDeclaracionVar(boolean vis, Tipo tipo, ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        //System.out.println("Metodo actual: "+ts.metodoActual.getNombre());
        // obtengo el metodo actual, si no es null es porque estoy en las variables locales de un metodo
        // por lo tanto no pueden repetirse los nombres de los parametros con los de las variables

        if (token.getTipo().equals("idMetVar")){
            // variable del metodo

            if (ts.metodoActual != null){
                // estoy en metodo
                // busco el idmetvar que voy a agregar en la lista de los parametros y si existe largo error, no pueden llamarse igual

                if (ts.metodoActual.listaParametros.containsKey(token.getLexema())){
                    throw new ErrorSemantico(token.getFila(), token.getColumna(),
                            "El nombre de la variable: "+token.getLexema()+ " ya fue asignado para un parametro");
                }
                else{
                    // no existe ese nombre en la lista de parametros por lo tanto creo una nueva variable del metodo
                    RegistroVariable varLocal;
                    varLocal = new RegistroVariable(token.getLexema());
                    varLocal.setTipo(tipo);
                    varLocal.setPos(ts.metodoActual.getProxPosVarLocal());
                    ts.metodoActual.listaVarLocales.put(varLocal.getNombre(), varLocal);
                    listaDec.add(new NodoDecLocal(token));
                    System.out.println("Guardo en la lista de variables del metodo: "+ts.metodoActual.getNombre()+ " la variable: "+varLocal.getNombre());
                }
            }
            // si viene aca es porque estoy en un tipo class, por lo tanto estoy viendo los atributos de la clase
            else {

                RegistroAtributo atributo;
                // atributo de clase
                // verifico que no este guardado ya en la lista de atributos
                if (ts.claseActual.listaAtributos.containsKey(token.getLexema())){
                    // si ya esta, lanzo error semantico
                    throw new ErrorSemantico(token.getFila(), token.getColumna(), "Atributo: '"+token.getLexema()+"' repetido");
                }
                else {
                    atributo = new RegistroAtributo(token.getLexema());
                    atributo.setTipo(tipo);
                    atributo.setVisibilidad(vis);
                    atributo.setPos(ts.claseActual.getProxPosAtributo());
                    ts.claseActual.listaAtributos.put(atributo.getNombre(), atributo);
                    listaDec.add(new NodoDecAtr(token));


                }
            }
            match("idMetVar");

        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idMetVar y se recibio: "+token.getTipo());
        }
        return listaDeclaracionVarRec(vis, tipo, listaDec);

    }

    // ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
    private ArrayList<NodoDeclaracion> listaDeclaracionVarRec(boolean vis, Tipo tipo, ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("coma")){
            match("coma");
            return listaDeclaracionVar(vis, tipo, listaDec);
        }
        return listaDec;
    }

    // BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
    private NodoBloqueMetodo bloqueMetodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tBloqueM = token;
        match("llaveAbre");
        // voy a lista declaracion var local con el metodo actual
        ArrayList<NodoDeclaracion> listaVarLocal = listaDeclaracionVarLocal(new ArrayList<NodoDeclaracion>());
        // voy a hacer un print de las variables locales de un metodo para ver que me devuelve
        //System.out.println(ts.metodoActual.listaVarLocales.toString());
        // tengo que ir a lista sentencia con el retorno del metodo
        //System.out.println("El retorno del metodo: "+ts.metodoActual.getNombre()+" es: "+ts.metodoActual.tipoRetorno.getNombreTipo());
        //Tipo tipoRetorno = ts.metodoActual.tipoRetorno; // si es null es de retorno void
        ArrayList<NodoSentencia> listaSent = listaSentencia(new ArrayList<NodoSentencia>());
        match("llaveCierra");
        return new NodoBloqueMetodo(tBloqueM, listaVarLocal, listaSent);
    }

    // ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal | lambda
    private ArrayList<NodoDeclaracion> listaDeclaracionVarLocal(ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // recursiva
        // si lo que viene esta en los primeros de declaracionVarLocal es porque no es lambda
        if (esPrimeroDeclaracionVarLocal(token.getTipo())){
            declaracionVarLocal(listaDec); // en declaracion variables las voy a guardar en la TS
            return listaDeclaracionVarLocal(listaDec);

        }
        return listaDec;
    }

    // ListaSentencia -> Sentencia ListaSentencia | lambda
    private ArrayList<NodoSentencia> listaSentencia(ArrayList<NodoSentencia> listaSent) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // mientras este en los primeros de sentencia vuelvo a entrar
        if (esPrimeroSentencia(token.getTipo())){
            NodoSentencia sentencia = sentencia();
            if (sentencia != null){
                listaSent.add(sentencia);
                return listaSentencia(listaSent);
            }

        }
        return listaSent;
    }

    // Visibilidad -> pub
    private boolean visibilidad() throws ErrorSintactico, ErrorLexico {
        match("prPub"); //lo manejo con flags para guardar en la TS
        return true;
    }

    // FormaMetodo -> st
    private boolean formaMetodo() throws ErrorSintactico, ErrorLexico {
        match("prSt"); // lo manejo con flags para guardar en la TS
        return true; // es estatico
    }

    // TipoPrimitivo -> Str | Bool | Int
    private Tipo tipoPrimitivo() throws ErrorSintactico, ErrorLexico {
        switch (token.getTipo()){
            case "tStr":
                match("tStr");
                return new TipoPrimitivo("tStr");
            case "tBool":
                match("tBool");
                return new TipoPrimitivo("tBool");
            case "tInt":
                match("tInt");
                return new TipoPrimitivo("tInt");
            default:
                throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un tipo primitivo");
        }

    }
    // TipoReferencia -> idClass
    private Tipo tipoReferencia() throws ErrorSintactico, ErrorLexico {
        String nombre = token.getLexema();
        match("idClass");
        //return hereda;
        return new TipoReferencia(nombre);
    }
    // TipoArray -> Array TipoPrimitivo
    private Tipo tipoArreglo() throws ErrorSintactico, ErrorLexico {
        match("idClass");
        return new TipoArreglo(tipoPrimitivo());
        // return true
    }

    // DeclaracionVarLocal -> Tipo ListaDeclaracionVar ;
    private void declaracionVarLocal(ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // en la hash de ListaVariablesLocales de un metodo voy a guardar:
        // la pos
        Tipo tipo = tipo(); //guardo el tipo
        // agrego vis pero porque lo uso para atributo, ver bien como seria en los metodos
        boolean vis = false;
        listaDeclaracionVar(vis, tipo, listaDec); //guardo el o los nombres de la variable
        match("ptoComa");
    }

    // ListaArgumentosFormalesOpt -> ListaArgumentosFormales | lambda
    private ArrayList<NodoDeclaracion> listaArgumentosFormalesOpt(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // Prim(ListaArgumentosFormales) = {str, Bool, Int, idClass, Array}
        String tipo = token.getTipo();
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
            return listaArgumentosFormales(listaArg);
        }
        return listaArg;
    }

    // ListaArgumentosFormales -> ArgumentoFormal ListaArgumentosFormalesRec
    private ArrayList<NodoDeclaracion> listaArgumentosFormales(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoDeclaracion nodoArgumento = argumentoFormal(); // en argumentoformal va a a agregar a la ts de ese metodo el argumento
        listaArg.add(nodoArgumento);
        return listaArgumentosFormalesRec(listaArg);
    }

    // ListaArgumentosFormalesRec -> , ListaArgumentosFormales | lambda
    private ArrayList<NodoDeclaracion> listaArgumentosFormalesRec(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("coma")){
            match("coma");
            return listaArgumentosFormales(listaArg);
        }
        return listaArg;
    }

    // ArgumentoFormal -> Tipo idMetAt
    private NodoDeclaracion argumentoFormal() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // en la hash de listaParametros voy a guardar
        Tipo tipo = tipo();
        Token tArgu = token;
        if (tipo == null){
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un tipo para el parametro: "+token.getLexema());
        }

        if (token.getTipo().equals("idMetVar")){
            // creo un argumento formal
            RegistroParametro parametro = new RegistroParametro(token.getLexema());
            // no pueden haber dos parametros que se llamen igual para el mismo metodo

            if (ts.metodoActual.listaParametros.containsKey(token.getLexema())){
                throw new ErrorSintactico(token.getFila(), token.getColumna(), "Ya existe un parametro con nombre: " + token.getLexema());
            }
            else {
                // no esta ese parametro lo agrego
                parametro.setPos(ts.metodoActual.getProxPosParametro());
                parametro.setTipo(tipo);
                ts.metodoActual.listaParametros.put(parametro.getNombre(), parametro);
                match("idMetVar");
                return new NodoDecArg(tArgu);
            }
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idMetVar y se recibio"+token.getTipo());
        }


    }
     // TipoMetodo -> Tipo | void
    // Prim(Tipo)= {str, bool int, idClass, Array}
    private Tipo tipoMetodo() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("tStr") || token.getTipo().equals("tBool") || token.getTipo().equals("tInt") ||
                token.getTipo().equals("idClass") || token.getTipo().equals("tArray")) {
            return tipo();
        }
        else {
            match("prVoid");
            return new TipoVoid();
        }

    }

    // Sentencia -> ; | Asignacion | SentenciaSimple ; | if ( Expresion ) SentenciaRec | while ( Expresion ) Sentencia |
    // for ( TipoPrimitivo idMetAt in idMetAt) Sentencia | Bloque | ret ExpresionOpt
    private NodoSentencia sentencia(/*Tipo tipo*/) throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (token.getTipo().equals("ptoComa")){
            match("ptoComa");
        }
        else {// SENTENCIA SIMPLE
            if (token.getTipo().equals("parAbre")){
                NodoSentenciaSimple sentenciaSimple = sentenciaSimple();
                match("ptoComa");
                return sentenciaSimple;
            }
            else {// IF
                if (token.getTipo().equals("prIf")){
                    Token tIf = token;
                    match(("prIf"));
                    match("parAbre");
                    System.out.println("Voy a expresion con: "+token.getTipo());
                    NodoExpresion condicion = expresion(); //devuelvo la condicion
                    match("parCierra");
                    NodoSentenciaRec sentenciaRec = sentenciaRec(); // me devuelve 2 nodos sentencia (then y else del if actual)
                    return new NodoIf(tIf, condicion, sentenciaRec.getSentenciaThen(), sentenciaRec.getSentenciaElse());
                }
                else { // WHILE
                    if (token.getTipo().equals("prWhile")){
                        Token tWhile = token;
                        match("prWhile");
                        match("parAbre");
                        NodoExpresion expresion = expresion();
                        match("parCierra");
                        NodoSentencia sentencia = sentencia();
                        return new NodoWhile(tWhile, expresion, sentencia);
                    }
                    else { // FOR
                        if (token.getTipo().equals("prFor")){
                            Token tFor = token;
                            match("prFor");
                            match("parAbre");
                            Tipo tipoVar = tipoPrimitivo();
                            NodoId variable = new NodoId(token.getFila(), token.getColumna(), token.getLexema());
                            match("idMetVar"); //en chequeo de sentencias se chequea que la variable no haya sido declarada como una variable local / param del metodo actual (no se si tambien atr de la clase actual)
                            match("prIn");
                            // en chequeo verifico que sea de tipo Array
                            NodoId iterador = new NodoId(token.getFila(), token.getColumna(), token.getLexema());
                            match("idMetVar");
                            match("parCierra");
                            match("prDo");
                            NodoSentencia cuerpoFor = sentencia();
                            return new NodoFor(tFor, tipoVar, variable, iterador, cuerpoFor);
                        }
                        else { // BLOQUE
                            if (token.getTipo().equals("llaveAbre")){
                                return bloque();
                            }
                            else { // RET

                                if (token.getTipo().equals("prRet")){
                                    // aca rompo si el tipo del metodo es void
                                    //if (tipo.getNombreTipo().equals("Void")){
                                    //    throw new ErrorSemantico(token.getFila(), token.getColumna(), "El tipo de retorno del metodo es void, no puede haber un ret");
                                    //}
                                    Token tRet = token;
                                    match("prRet");
                                    //expresionOpt();
                                    return new NodoRet(tRet, expresionOpt());

                                }
                                else { //ASIGNACIÓN
                                    // con idMetVar o con self voy a asignacion
                                    if (token.getTipo().equals("idMetVar") | token.getTipo().equals("prSelf")){
                                        return asignacion();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // SentenciaRec -> Sentencia RecursivoElse
    private NodoSentenciaRec sentenciaRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoSentencia nodoThen = sentencia();
        NodoSentencia nodoElse = recursivoElse();
        return new NodoSentenciaRec(nodoThen, nodoElse);
    }

    // RecursivoElse -> else Sentencia | lambda
    private NodoSentencia recursivoElse() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("prElse")){
            match("prElse");
            return sentencia();
        }
        return null;
        // sino es lambda
    }

    // ExpresionOpt -> Expresion | lambda
    private NodoExpresion expresionOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (esPrimeroExpresion(token.getTipo())){
            return expresion();
        }
        return null;
    }

    // SentenciaSimple -> ( Expresion )
    private NodoSentenciaSimple sentenciaSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tokenSS = token;
        match("parAbre");
        NodoExpresion nodoExpresion = expresion();
        match("parCierra");
        return new NodoSentenciaSimple(tokenSS, nodoExpresion);
    }

    //Expresion -> ExpresionOr
    private NodoExpresion expresion() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionOr = expresionOr();
        return nodoExpresionOr;

    }

    //BLoque -> { ListaSentencia }
    private NodoBloque bloque() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tBloque = token;
        match("llaveAbre");
        ArrayList<NodoSentencia> listaSent = listaSentencia(new ArrayList<NodoSentencia>());
        match("llaveCierra");
        return new NodoBloque(tBloque, listaSent);
    }

    //Asignacion -> AccesoVarSimple = Expresion | AccesoSelfSimple = Expresion
    private NodoAsignacion asignacion() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de acceso var simple entro
        // Prim(AccesoVarSimple) = {id}
        if (token.getTipo().equals("idMetVar")){
            NodoAccesoVarSimple nodoAccesoVarSimple = accesoVarSimple();
            Token tAsig = token;
            match("opIgual");
            NodoExpresion nodoExpresion = expresion();
            return new NodoAsignacion(tAsig, nodoAccesoVarSimple, nodoExpresion);
        }
        else {
            // si esta en los primeros de acceso self simple entro
            // Prim(AccesoVarSimple) = {self}
            if (token.getTipo().equals("prSelf")){
                NodoAccesoSelfSimple nodoAccesoSelfSimple = accesoSelfSimple();
                Token tAsig = token;
                match("opIgual");
                NodoExpresion nodoExpresion = expresion();
                return new NodoAsignacion(tAsig, nodoAccesoSelfSimple, nodoExpresion);
            }
        }
        return null;
    }

    // AccesoVarSimple -> id AccesoVarSImpleRec
    private NodoAccesoVarSimple accesoVarSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoVarEncadenado varEncadenado = new NodoVarEncadenado(token.getFila(), token.getColumna(), token.getLexema());
        match("idMetVar");
        NodoVarEncadenado proxEncadenado = null;
        return new NodoAccesoVarSimple(varEncadenado,accesoVarSimpleRec(proxEncadenado));
        //accesoVarSimpleRec puede ser: null | NodoVarEncadenado| nodoExpresion
    }

    // AccesoVarSimpleRec -> ListaEncadenadoSImple | [ Expresion ]
    private NodoAccesoVarSimpleRec accesoVarSimpleRec(NodoVarEncadenado varEncadenado) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de lista enadenado simple entro ahi
        // Prim(ListaEncadenadoSimple) = {. , lambda}
        if (token.getTipo().equals("pto")){
            listaEncadenadoSimple(varEncadenado); //en este metodo anido todos los encadenados a el id principal varEncadendo
            return new NodoAccesoVarSimpleRec(varEncadenado);
        }
        else {
            if (token.getTipo().equals("corcheteAbre")){
                match("corcheteAbre");
                NodoExpresion nodoExpresion = expresion();
                match("corcheteCierra");
                return new NodoAccesoVarSimpleRec(nodoExpresion);
            }
        }
        return null;
    }

    // ListaEncadenadoSimple -> EncadenadoSimpple ListaEncadenadoSimple | lambda
    // En esta clase se hacen los chequeos de tipos del encadenado en la 2da pasada
    private void listaEncadenadoSimple(NodoVarEncadenado varEncadenado) throws ErrorSintactico, ErrorLexico {
        // es recursiva por lo tanto cada vez que viene un primero de encadenado simple vuelvo a entrar
        // Prim(EncadenadoSimple) = {.}
        if (token.getTipo().equals("pto")){
            NodoVarEncadenado nuevaVarEnc = encadenadoSimple();
            if (varEncadenado != null) { //si varEncadenado == null entonces recien voy a setear el varEncadeno de id2
                varEncadenado.setProxEncadenado(nuevaVarEnc);
            }
            //Sino ya pase el id2
            //Aqui deberia chequear la correctitud semnatica del encadenado!
            listaEncadenadoSimple(nuevaVarEnc);
        }
    }

    // AccesoSelfSimple -> self ListaEncadenadoSimple
    private NodoAccesoSelfSimple accesoSelfSimple() throws ErrorSintactico, ErrorLexico {
        NodoVarEncadenado selfEncadenado = new NodoVarEncadenado(token.getFila(), token.getColumna(), token.getLexema());
        match("prSelf");
        NodoVarEncadenado varEncadenado = null; //inicializo el nodo en null
        listaEncadenadoSimple(varEncadenado); //este metodo va agregando los nodos del encadenados
        //Si hay encadenado varEncadenado != nul -> selfEncadenado = self y varEncadenado = id1.id1.id3
        return new NodoAccesoSelfSimple(selfEncadenado,varEncadenado);
    }

    // EncadenadoSimple -> . id
    private NodoVarEncadenado encadenadoSimple() throws ErrorSintactico, ErrorLexico {
        match("pto");
        NodoVarEncadenado varEncadenado = new NodoVarEncadenado(token.getFila(), token.getColumna(), token.getLexema());
        match("idMetVar"); //sueldo
        return varEncadenado;
    }

    // ExpresionOr -> ExpresionAnd ExpresionOrRec
    private NodoExpresion expresionOr() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionAnd = expresionAnd();
        return expresionOrRec(nodoExpresionAnd);
    }

    // ExpresionOrRec -> || ExpresionAnd ExpresionOrRec | lambda
    private NodoExpresion expresionOrRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        //while (token.getTipo().equals("opOr")){
        if (token.getTipo().equals("opOr")){
            Token operador = token;
            match("opOr");
            NodoExpresion nodoDer = expresionAnd();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq,nodoDer);
            return expresionOrRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    // ExpresionAnd -> ExpIgual ExpAndRec
    private NodoExpresion expresionAnd() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionIgual = expresionIgual();
        return expresionAndRec(nodoExpresionIgual);
    }

    //ExpresionAndRec -> && ExpIgual ExpresionAndRec | lambda
    private NodoExpresion expresionAndRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (token.getTipo().equals("opAndLog")){
            Token operador = token;
            match("opAndLog");
            NodoExpresion nodoDer = expresionIgual();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            return expresionAndRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    // ExpresionIgual -> ExpresionComp ExpresionIgualRec
    private NodoExpresion expresionIgual() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionComp = expresionComp();
        //expresionigualRec();
        return expresionigualRec(nodoExpresionComp);

    }

    // ExpresionIgualRec -> OpIgual ExpresionComp ExpresionIgualRec | lambda
    private NodoExpresion expresionigualRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // voy a repetir siempre que vengan los primros de opIgual
        // Prim(OpIgual) = { == , != }
        if (token.getTipo().equals("opIgualIgual") | token.getTipo().equals("opDiferente")){
            Token operador = opIgual();
            NodoExpresion nodoDer = expresionComp();
            //expresionigualRec();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            return expresionigualRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    // ExpresionComp -> ExpresionAd ExpresionCompRec
    private NodoExpresion expresionComp() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionAd = expresionAd();
        //expresionCompRec();

        return expresionCompRec(nodoExpresionAd);
    }

    // ExpresionCompRec -> OpComp ExpresionAd | lambda
    // Esta funcion no es recursiva
    private NodoExpresion expresionCompRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // deben venir los primeros de opComp
        // Prim(OpComp) = {<, >, <=, >=}
        if (token.getTipo().equals("opMenor") | token.getTipo().equals("opMenorIgual")  | token.getTipo().equals("opMayor") | token.getTipo().equals("opMayorIgual")){
            Token operador = opComp();
            NodoExpresion nodoDer = expresionAd();

            return new NodoExpresionBin(operador, nodoIzq, nodoDer);
        }
        return nodoIzq;
    }

    // ExpresionAd -> ExpresionMul ExpresionAdRec
    private NodoExpresion expresionAd() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionMul = expresionMul();
        //expresionAdRec();

        return expresionAdRec(nodoExpresionMul);
    }

    // ExpresionAdRec -> OpAd ExpresionMul ExpresionAdRec | lambda
    private NodoExpresion expresionAdRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es recursiva cada vez que venga un opAd vuelvo a entrar
        // Prim(OpAd) = {+ , -}
        if (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos")){
            Token operador = opAd();
            NodoExpresion nodoDer = expresionMul();
            //expresionAdRec();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            return expresionAdRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    // ExpresionMul -> ExpresionUnario ExpresionMulRec
    private NodoExpresion expresionMul() throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        NodoExpresionUnario nodoExpresionUnario = expresionUnario();
        //expresionMulRec();
        return expresionMulRec(nodoExpresionUnario);
    }

    // ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
    private NodoExpresion expresionMulRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // simpre que venga un opMul hago recursividad
        if (token.getTipo().equals("opPor") | token.getTipo().equals("opdiv")){
            Token operador = opMul();
            NodoExpresionUnario nodoDer = expresionUnario();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            //expresionMulRec();
            return expresionMulRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    // ExpresionUnario -> OpUnario ExpresionUnario | Operando
    private NodoExpresionUnario expresionUnario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // siempre que venga un opUnario vuelvo
        if (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos") |
                token.getTipo().equals("opMasMas") | token.getTipo().equals("opMenosMenos") | token.getTipo().equals("opNot")){
            Token operador = opUnario();
            NodoExpresionUnario nodoExpresionUnario = expresionUnario();
            return new NodoExpresionUnario(operador, nodoExpresionUnario);
        }
        else { // si no es opMas ni opMenos es un operando
            // si lo que viene no esta en los prim de operando no voy
            if (esPrimeroOperando(token.getTipo())) {
                NodoOperando nodoOperando = operando();
                return new NodoExpresionUnario(nodoOperando);
            } else {
                throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un operando y se encontro "+token.getTipo());
            }
            //operando();
        }

    }

    // OpIgual -> == | !=
    private Token opIgual() throws ErrorSintactico, ErrorLexico {
        Token operador = token;
        if (token.getTipo().equals("opIgualIgual")) {
            match("opIgualIgual");
            return operador;
        }
        else {
            if (token.getTipo().equals("opDiferente")){
                match("opDiferente");
                return operador;
            }
        }
        return null;
    }

    // opComp -> < | > | <= | >=
    private Token opComp() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        Token operador = token;
        switch (tipo){
            case "opMayor":
                match("opMayor");
                return operador;
            case "opMayorIgual":
                match("opMayorIgual");
                return operador;
            case "opMenor":
                match("opMenor");
                System.out.println("Matheo opMenor y salgo con: "+token.getTipo());
                return operador;
            case "opMenorIgual":{
                match("opMenorIgual");
                return operador;
            }
        }
        return null;
    }

    // opAd -> + | -
    private Token opAd() throws ErrorSintactico, ErrorLexico {
        Token operador = token;
        if (token.getTipo().equals("opMas")) {
            match("opMas");
            return operador;
        }
        else {
            if (token.getTipo().equals("opMenos")){
                match("opMenos");
                return operador;
            }
        }
        return null;
    }

    // opUnario -> + | - | ++ | -- | !
    private Token opUnario() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        Token tokenOperador = token;
        switch (tipo){
            case "opMas":
                match("opMas");
                return tokenOperador;
            case "opMenos":
                match("opMenos");
                return tokenOperador;
            case "opMasMas":
                match("opMasMas");
                return tokenOperador;
            case "opMenosMenos":
                match("opMenosMenos");
                return tokenOperador;
            case "opNot":
                match("opNot");
                return tokenOperador;
        }
        return null;
    }

    // OpMul -> * | /
    private Token opMul() throws ErrorSintactico, ErrorLexico {
        Token tokenOperador = token;
        if (token.getTipo().equals("opPor")) {
            match("opPor");
            return tokenOperador;
        }
        else {
            if (token.getTipo().equals("opdiv")){
                match("opdiv");
                return tokenOperador;
            }
        }
        return null;
    }

    // Operando -> Literal | Primario EncadenadoOpt
    private NodoOperando operando() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = token.getTipo();
        //System.out.println("Estoy en operando con: "+token.getTipo());
        // si viene un literal
        switch (tipo){
            // Prim(Literal) = {nil, true, false, intLiteral, strLiteral}
            case "prNil" , "prTrue", "prFalse", "literal_entero", "literal_cadena":
                NodoLiteral nodoLiteral = literal();
                return new NodoOperando(nodoLiteral);
            // Prim(Primario) = { (, self, id, idclass, new}
            case "parAbre", "prSelf", "idMetVar", "idClass", "prNew":
                System.out.println("Voy a primario con: "+token.getTipo());
                NodoPrimario nodoPrimario = primario();
                NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
                return new NodoOperando(nodoPrimario, nodoEncadenadoOpt);

        }
        //System.out.println("salgo de operando con: "+token.getTipo());
        return null;
    }

    // EncadenadoOpt -> Encadenado | lambda
    private NodoEncadenadoOpt encadenadoOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si es pto va a encadendo, Prim(Encadenado) = { . }
        if (token.getTipo().equals("pto")){
            NodoEncadenado nodoEncadenado = encadenado();
            return new NodoEncadenadoOpt(nodoEncadenado);
        }
        return null;
    }

    // Literal -> nil | true | false | intLiteral | strLiteral
    private NodoLiteral literal() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        switch (tipo){
            case "prNil":
                NodoNil nodoNil = new NodoNil(token.getFila(), token.getColumna(), token.getLexema());
                match("prNil");
                return nodoNil;
            case "prTrue":
                NodoBool nodoBoolTrue = new NodoBool(token.getFila(), token.getColumna(), token.getLexema());
                match("prTrue");
                return nodoBoolTrue;
            case "prFalse":
                NodoBool nodoBoolFalse = new NodoBool(token.getFila(), token.getColumna(), token.getLexema());
                match("prFalse");
                return nodoBoolFalse;
            case "literal_entero":
                NodoNum nodoNum = new NodoNum(token.getFila(), token.getColumna(), token.getLexema());
                match("literal_entero");
                return nodoNum;
            case "literal_cadena":
                NodoStr nodoStr = new NodoStr(token.getFila(), token.getColumna(), token.getLexema());
                match("literal_cadena");
                return nodoStr;
        }
        return null;
    }

    // Primario -> ExpresionParentizada | AccesoSelf | AccesoVar | LlamadaMetodo | LlamadaMetodoEstatico | LlamadaConClassor
    private NodoPrimario primario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = token.getTipo();
        switch (tipo){
            // Prim(ExpresionParentizada) = { ( }
            case "parAbre":
                NodoExpresionParentizada nodoExpresionParentizada = expresionParentizada();
                return new NodoPrimario(nodoExpresionParentizada);
            // Prim(AccesoSelf) = { self }
            case "prSelf":
                NodoAccesoSelf nodoAccesoSelf = accesoSelf();
                return new NodoPrimario(nodoAccesoSelf);
            // Prim(AccesoVar) = { id } y Prim(LlamadaMetodo) = { id }
            // como ambas van a id veo los siguientes
            case "idMetVar":
                // si me viene un parAbre es porque fue a LlamadaMetodo
                System.out.println("estoy en primario con: "+token.getTipo());
                Token next = lookAhead();
                if (next.getTipo().equals("parAbre")){
                    //HACER
                    //NodoLlamadaMetodo nodoLLamadaMetodo = llamadaMetodo();
                    //return new NodoPrimario(nodoLlamadaMetodo);
                    llamadaMetodo();

                }
                else {
                    NodoAccesoVar nodoAccesoVar = accesoVar();
                    return new NodoPrimario(nodoAccesoVar);
                }
                //break;
            // Prim(LlamadaMetodoEstatico) = {idClass}
            case "idClass":
                //HACER
                llamadaMetodoEstatico();
                break;
            // Prim(LlamadaConClassor) = {new}
            case "prNew":
                //HACER
               llamadaConClassor();

        }
        return null;
    }

    // ExpresionParentizada -> ( Expresion ) EncadenadoOpt
    private NodoExpresionParentizada expresionParentizada() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("parAbre");
        NodoExpresion nodoExpresion = expresion();
        match("parCierra");
        NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();

        return new NodoExpresionParentizada(nodoExpresion, nodoEncadenadoOpt);

    }

    // AccesoSelf -> self EncadenadoOpt
    private NodoAccesoSelf accesoSelf() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("prSelf");
        NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
        return new NodoAccesoSelf(nodoEncadenadoOpt);
    }

    // AccesoVar -> id AccesoVarRec
    /*private NodoAccesoVar accesoVar(Tipo tipoContexto) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoId nodoId;
        //1. Solo entra aqui cuando tipoContexto == null
        // SI tipoCOntexto == null es la primera vez que entro a accesoVar
        if (tipoContexto == null){
            // En la primera interacion tengo que chequear que el id1 sea alguno de estos:
            //1.1 Una variable local del metodo
            //1.2 Un parametro del metodo
            //1.3 Un atributo pub de la clase
            if (ts.noEstaTs(token.getLexema())){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "La variable " + token.getLexema() + " no ha sido declarada como variable local o parametro del metodo actual y tampoco es un atributo publico de la clase "+ ts.claseActual.getNombre() );
            }

            RegistroVariable variable = ts.getVariable(token.getLexema());
            nodoId = new NodoId(token.getFila(), token.getColumna(), variable);
            tipoContexto = nodoId.getTipoSintetizado();

        }
        //2. Si tipoContexto != null -> se accedio a este metodo de manera recursiva por medio de encadenadoOpt
        // por lo que estariamos en el cuerpo del encadenado: id1.id2.id3.id4 -> estariamos evaluando los ids del 2 al 4
        else {

            //3. Verificamos que el id pertenezca a un atr del tipo contexto:
            //3. Si tipoContexto es tipoReferencia entonces tenemos que buscar en la clase de tipoContexto el atributo del token actual
            //3.1 Si la variable no es un atributo publico de tipoContexto:
            if (tipoContexto.esTipoReferencia() && ts.noEstaTs(tipoContexto.getNombreTipo(), token.getLexema())) {
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "La variable " + token.getLexema() + " no es un atributo de la clase " + tipoContexto.getNombreTipo() + " o su visibilidad es privada");
            }

            //4. Actualizamos el tipo contexto para tener el de id2
            //4.1 Si la variable es un atributo visible de tipoContexto, la busco:
            RegistroVariable variable = ts.getAtrDeClase(ts.getClase(tipoContexto.getNombreTipo()), token.getLexema());

            System.out.println("VARIABLW EN CLASE: " + ts.getClase(tipoContexto.getNombreTipo()).getNombre());
            System.out.println("VARIABLE EN ACCESOVAR: " + variable.getNombre());

            //2.1.2 Creamos el nodoId con los datos de la variable y el nro de fila y columna en el que se encuentra
            nodoId = new NodoId(token.getFila(), token.getColumna(), variable);

            //Actualizamos el tipoContexto
            //tipoContexto = nodoId.getTipoSintetizado();



        }

        match("idMetVar");


        //4. Llamo a nodoAccesoVarRec
        //Le paso el tipoContexto para que haga los chequeos correspondientes en caso de que haya encadenado
        NodoAccesoVarRec nodoAccesoVarRec = accesoVarRec(tipoContexto);
        NodoAccesoVar nodoAccesoVar = new NodoAccesoVar(nodoId, nodoAccesoVarRec);

        System.out.println("EN ACCESOVAR TIPO HEREDADO1 : " + nodoId.getTipoSintetizado().getNombreTipo());
        System.out.println("EN ACCESOVAR LINEA : " + nodoId.getNroLinea());

        /*if (tipoContexto.esTipoReferencia()){
            nodoAccesoVar.setTipoHeredado(ts.tablaClases.get(nodoId.getTipoSintetizado().getNombreTipo()));
            System.out.println("EN ACCESOVAR TIPO HEREDADO: " + nodoId.getTipoSintetizado().getNombreTipo());
        }*/




        //4. una vez que tengo los dos nodos que conforman al nodoAccesoVar hago el cheque para el caso:
        //AccesoVar -> id AccesoVarRec; AccesoVarRec -> [Expresion] EncadenadoOpt
        //nodoAccesoVar.chequear();
        //nodoAccesoVar.setTipoSintetizado(nodoId.getTipoSintetizado());
        //return nodoAccesoVar;
    //}

    private NodoAccesoVar accesoVar() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoId nodoId;
        Token tokenActual = token; // Guardamos el token para el nodo (línea y lexema)
        String lexema = tokenActual.getLexema();


        // 1. CASO BASE: Es el primer ID de la cadena (ej. 'v1' en v1.a.b)
        // En el EDT NO buscamos si existe, solo creamos el nodo con el lexema.
        // La resolución de nombres se hará en la segunda pasada (metodo chequear).
        nodoId = new NodoId(tokenActual.getFila(), tokenActual.getColumna(), lexema);

        match("idMetVar");

        // Obtenemos el resto de la cadena.
        // Pasamos null porque el tipo de 'v1' aún no se conoce (se infiere en la pasada 2).
        NodoAccesoVarRec nodoAccesoVarRec = accesoVarRec();

        return new NodoAccesoVar(nodoId, nodoAccesoVarRec);

    }

    //AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    /*private NodoAccesoVarRec accesoVarRec(Tipo tipoContexto) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoAccesoVarRec nodoAccesoVarRec;
        if (token.getTipo().equals("corcheteAbre")){
            match("corcheteAbre");
            NodoExpresion nodoExpresion = expresion();
            match("corcheteCierra");
            //encadenadoOpt();

            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt(tipoContexto);
            //2. Verifico que el tipoContexto actual de id sea array
            if (nodoEncadenadoOpt == null){
                // AccesoVarRec -> [expresion]
                nodoAccesoVarRec = new NodoAccesoVarRec(nodoExpresion);
                nodoAccesoVarRec.chequear(tipoContexto);
                return nodoAccesoVarRec;
            }

            //AccesoVarRec -> [Expresion] EncadenadoOpt
            //Este caso solo tiene sentido si EncadenadoOpt -> Encadenado -> EncadenadoRec -> LlamadaMetodo
            nodoAccesoVarRec = new NodoAccesoVarRec(nodoExpresion, nodoEncadenadoOpt);
            nodoAccesoVarRec.chequear(tipoContexto);
            return nodoAccesoVarRec;

        }
        else {
            //AccesoVarRec -> EncadenadoOpt
            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt(tipoContexto);
            nodoAccesoVarRec = new NodoAccesoVarRec(nodoEncadenadoOpt);
            return nodoAccesoVarRec;

        }

    }*/

    private NodoAccesoVarRec accesoVarRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (token.getTipo().equals("corcheteAbre")) {
            match("corcheteAbre");
            // Construimos el nodo de la expresión del índice
            NodoExpresion nodoExpresion = expresion();
            match("corcheteCierra");

            // Construimos la parte opcional del encadenado
            // Pasamos null o simplemente llamamos al constructor vacío
            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();

            // Retornamos el nodo del AST con sus hijos conectados
            // El chequeo de si el contexto es Array se hará en NodoAccesoVarRec.chequear()
            if (nodoEncadenadoOpt == null){
                return new NodoAccesoVarRec(nodoExpresion);
            }
            return new NodoAccesoVarRec(nodoExpresion, nodoEncadenadoOpt);

        } else {
            // AccesoVarRec -> EncadenadoOpt
            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
            return new NodoAccesoVarRec(nodoEncadenadoOpt);
        }
    }

    // LlamadaMetdo -> id ArgumentosActuales EncadenadoOpt
    private void llamadaMetodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("idMetVar");
        argumentosActuales();
        encadenadoOpt();
    }

    // LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
    private void llamadaMetodoEstatico() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("idClass");
        match("pto");
        llamadaMetodo();
        encadenadoOpt();
    }

    // LlamadaConClassor -> new LLamadaConClassOrRec
    private void llamadaConClassor() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("prNew");
        llamadaConClassorRec();
    }

    // LlamadaConClassorRec -> idClass ArgumentosActuales EncadenadoOpt | TipoPrimitivo [ Expresion ]
    private void llamadaConClassorRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (token.getTipo().equals("idClass")){
            match("idClass");
            argumentosActuales();
            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
        }
        else {

            NodoTipoPrimitivo nodoTipoPrimitivo = new NodoTipoPrimitivo(token.getFila(), token.getColumna(), tipoPrimitivo().getNombreTipo());
            //tipoPrimitivo();
            match("corcheteAbre");
            NodoExpresion nodoExpresion = expresion();
            match("corcheteCierra");

        }

    }

    // ArgumentosActuales -> ( ListaExpresionesOpt )
    private void argumentosActuales() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("parAbre");
        listaExpresionesOpt();
        match("parCierra");

    }

    // ListaExpresionesOpt -> ListaExpresiones | lambda
    private void listaExpresionesOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // Prim(ListaExpresiones) = Prim(Expresion)
        if (esPrimeroExpresion(token.getTipo())){
            listaExpresiones();

        }

    }

    // ListaExpresiones -> Expresion ListaExpresionesRec
    private void listaExpresiones() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresion = expresion();
        listaExpresionesRec();
    }

    // ListaExpresionesRec -> , ListaExpresiones | lambda
    private void listaExpresionesRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("coma")){
            match("coma");
            listaExpresiones();
        }
    }

    // Encadenado -> . EncadenadoRec
    private NodoEncadenado encadenado() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("pto");
        NodoEncadenadoRec nodoEncadenadoRec = encadenadoRec();
        return new NodoEncadenado(nodoEncadenadoRec);
    }

    // EncadenadoRec -> LlamadaMetodo | AccesVar
    private NodoEncadenadoRec encadenadoRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // como con ambos me llega id veo el nextToken
        Token next = lookAhead();
        if (next.getTipo().equals("parAbre")){ // es porq esta en llamada metodo
            llamadaMetodo();
        }
        else {
            NodoAccesoVar nodoAccesoVar = accesoVar();
            return new NodoEncadenadoRec(nodoAccesoVar);
        }
        return null;
    }


    // Conjuntos de primeros --------------------------------------------------------------------------------

    // COnjunto de primeros Operando
    // Prim(Operando) = {nil, true, false, intLiteral, strLiteral, (, self, id, idClass, new, ., lambda}
    private boolean esPrimeroOperando(String tipo){
        if (tipo == "prNil" | tipo == "prTrue" | tipo == "prFalse" | tipo == "literal_entero" | tipo == "literal_cadena" |
                tipo == "parAbre" | tipo == "prSelf" | tipo == "idMetVar" | tipo == "idClass" | tipo == "prNew" | tipo == "pto"){
            return true;
        } else {
            return false;
        }
    }


    // conjunto de primeros expresion
    private boolean esPrimeroExpresion(String tipo){
        // Prim(Expresion) = {+, -, !, ++, --, (, self, id, idclass, new, nil, true, false, intLiteral, strliteral, . ,lambda}
        if (tipo == "opMas" | tipo == "opMenos"| tipo == "opNot" | tipo == "opMasMas" | tipo == "opMenosMenos" |
                tipo == "prNil" | tipo == "prTrue" | tipo == "prFalse" | tipo == "literal_entero" | tipo == "literal_cadena" |
                tipo == "parAbre" | tipo == "prSelf" | tipo == "idMetVar" | tipo == "idClass" | tipo == "prNew" |
                tipo == "pto"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros sentencia
    private boolean esPrimeroSentencia(String tipo){
        // Prim(Sentencia) = {;, id, self, (, if, while, for, {, ret}
        // que tipo de id es? verificar eso asi lo devuelvo aca
        if (tipo == "ptoComa" | tipo == "idMetVar" | tipo == "prSelf" | tipo == "parAbre" | tipo == "prIf" |
                tipo == "prWhile" | tipo == "prFor" | tipo == "llaveAbre" | tipo == "prRet"){
            return true;
        } else {
            return false;
        }
    }

    // conjunto de primeros tipo primitivo
    private boolean esPrimeroTipoPrimitivo(String tipo){
        // Prim(TipoPrimitivo) = {str, bool, int}
        if (tipo == "tStr" || tipo == "tBool" || tipo == "tInt"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros de tipo metodo
    private boolean esPrimeroTipoMetodo(String tipo){
        // Prim(TipoMetodo) = {Str, BOol, Int, idClass, Array, lambda}
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros declaracion variable local
    private boolean esPrimeroDeclaracionVarLocal(String tipo){
        // Prim(DeclaracionVarLocal) = {Str, Bool, Int, idClass, Array, lambda}
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "prArray"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros miembro
    private boolean esPrimeroMiembro(String tipo){
        if (tipo == "prSt" | tipo == "pto"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros herenciaOpcional
    private boolean esPrimeroHerencia(String tipo) {
        // Prim(Herencia) = {Str, Bool, Int, idClass, Array}
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idMetVar" | tipo == "tArray"){
            return true;
        }
        else {
            return false;
        }
    }

    // funcion matcheo que vamos a utilizar para pedir el next token
    // por lo tanto voy a verificar que el tipo que recibo es el tipo esperado
    // si eso pasa pido next token
    void match(String tipoEsperado) throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals(tipoEsperado)){
            //System.out.println("Esperado: " + tipoEsperado +
                    //" | Actual: " + token.getTipo());
            // solo avanzo si matcheo, en ninguna otra parte del codigo deberia avanzar
            //System.out.println("Hice match de: "+token.getTipo());
            //si ya mire hacia adelante no necesito volver a pedir nextoken porque sino voy a perder el simbolo
            if (lookahead){
                this.token = this.next;
                lookahead = false;
            } else {

                nextToken();
            }

        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba "+tipoEsperado+" y se enontro "+token.getTipo());
        }

    }

    // funcion para pedir el next token cuando matcheo
    private void nextToken() throws ErrorLexico {
        puntero += 1;
        token = lexico.analizador();



    }

    // funcion solo para ver el siguiente, sin avanzar (lookahead)
    private Token lookAhead() throws ErrorLexico {

        this.next = lexico.analizador();
        this.lookahead = true;
        return next;

       /* if (next.getTipo() != "EOF"){
            this.lookahead = true;
            return next;
        }
        return null;*/


    }


}