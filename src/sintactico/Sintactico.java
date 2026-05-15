package sintactico;
import java.util.List;
import java.util.ArrayList;

import lexico.ErrorLexico;
import lexico.Token;
import lexico.Lexico;
import semantico.ValidarDeclaracion;
import semantico.nodos.NodoAccesoVar;
import semantico.nodos.NodoAccesoVarRec;
import semantico.nodos.NodoExpresion;
import semantico.nodos.NodoId;
import semantico.tipos.*;
import sintactico.ErrorSintactico;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.registros.*;


// analizador sintactico
public class Sintactico {
    //private List<Token> listaTokens; //Lista de tokens que obtuve del lexico
    private Lexico lexico;
    private int puntero;
    private Token token;
    private Token next;
    private boolean lookahead = false;
    TablaSimbolos ts = new TablaSimbolos();

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
        program();
        // si sale de program es porque hizo match con $ entonces devolver Exito!


    }

    // Gramatica ----------------------------------------------------------------------------------------------

    //Program -> ListaDefiniciones Start
    private void program() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        listaDefiniciones();
        //salgo de LS, voy a imprimir las clases:
        ts.imprimirClases();
        // si es lambda va directo a start
        RegistroStart metodoStart = new RegistroStart();
        start();
        System.out.println("token final: "+ token.getTipo());
        match("EOF"); // ver si tiene que ser $ o EOF
    }

    // Start -> start BloqueMetodo
    private void start() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // matcheo start asi avanza
        //match("prStart"); // esto tmb verificar porque nose si start era una palabra reservada (pregintar a profe)
        if (token.getLexema().equals("start")){
            // deberia matchear idMetVar, porque start al no ser reservada la toma como idMetVar
            match("idMetVar"); //consumo start y voy a bloque
            // ahora mi clase actual es el metodo start
            bloqueMetodo();
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba start y se enontro "+token.getTipo());
        }

    }

    // ListaDefiniciones -> Clase ListaDefiniciones | Implementacion ListaDefiniciones | lambda
    private void listaDefiniciones() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es recursiva, por lo que voy a agregar un while, mientras lea la palabra reservada class o impl, tiene que volver a entrar
        if (token.getTipo().equals("prClass") || token.getTipo().equals("prImpl")){
            if (token.getTipo().equals("prClass")){
                clase();
                listaDefiniciones();
            }
            else {
                impl();
                listaDefiniciones();
            }
        }
    }

    // Class -> class idClass HerenciaOpt { listaAtributos }
    private void clase() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
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
            listaAtributos(); // si lo que viene es } es porque era lambda
            // imprimo los atributos de esa clase
            System.out.println("Atributos de la clase: "+ ts.claseActual.listaAtributos.toString());
            match("llaveCierra");

            // salgo de la clase actual
            ts.claseActual = null;
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
    private void listaAtributos() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = token.getTipo();
        // si lo que viene no esta en los primeros de Atributo es porque listaAtributos es lambda entonces aca no hace nada
        // es recursiva, por lo tanto siempre que venga alguno de los primeros de A vuelvo a entrar
        // como puede no tener prPub, tambien puedo ir directamente a Tipo
        if (tipo.equals("prPub") | tipo.equals("tStr") | tipo.equals("tBool") | tipo.equals("tInt") | tipo.equals("idClass")) { //| tipo.equals("Array")
            atributo();
            // actualizo el tipo
            //tipo = token.getTipo();
            listaAtributos();
        }
    }

    // Impl -> impl idClass { ListaMiembros }
    private void impl() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
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
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idClass");
        }
        // entonces ahora voy a ir a lista miembros con la clase actual
        match("llaveAbre");
        listaMiembros();
        match("llaveCierra");

        // salgo de este impl, vuelvo la clase actual a null
        ts.claseActual = null;
    }

    // ListaMiembros -> Miembro ListaMiembros | lambda
    private void listaMiembros() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si lo que viene esta en los primeros de miembro es porque lista miembro no es lambda
        // Prim(E) = { st, . , lambda}
        if (esPrimeroMiembro(token.getTipo()) | token.getTipo().equals("prFn")){
            miembro();
            listaMiembros();
        }
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
    private void miembro() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // tengo dos opciones o voy a metodo o voy a constructor
        // cuando no tengo st puede venir fn
        if (token.getTipo().equals("prSt") | token.getTipo().equals("prFn")){
            metodo();
        }
        else {
            // si va a constructor verifico si esa clase ya tiene constructor, si es asi largo error
            if (ts.claseActual.constructor != null){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "El constructor de la clase: " + ts.claseActual.nombre + " ya ha sido declarado");
            }
            constructor();

        }
    }

    // Metodo -> FormaMetodoOpt fn TipoMetodoOpt idMetAt ArgumentosFormales BloqueMetodo
    private void metodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        boolean estatico = formaMetodoOpt(); // true = estatico, false = no estatico
        match("prFn");
        // si el tipo de retorno es vacio es porque es void
        Tipo tipo = tipoMetodoOpt(); // va a guardar en la ts el tipo de retorno de la funcion
        // aca es donde guardo el metodo con forma, tipo nombre

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
            //System.out.println("Metodo: "+metodo.getNombre());
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idMetVar y se recibio: "+token.getTipo());
        }
        // ya guarde el metodo en la ts.claseactual.listametodos, ahora voy a sus parametros y varlocales
        // voy a argumentos formales con el metodoactual
        argumentosFormales(); //voy a guardar en la hash de listaParametros todos los argumentos
        // salgo de argumentos imprimo a ver que guardo

        // voy a ir a bloque metodo con el metodoactual
        bloqueMetodo();
        ts.metodoActual.imprimirMetodo(ts.metodoActual, ts.claseActual);
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
    private void argumentosFormales() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("parAbre");
        listaArgumentosFormalesOpt();
        match("parCierra");
    }

    // Constructor -> . ArgumentosFormales BloqueMetodo
    private void constructor() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("pto");
        //ts.claseActual.inConstructor = true;
        ts.claseActual.constructor = new Constructor();
        ts.metodoActual = ts.claseActual.constructor;
        //System.out.println("Voy a arg formales con el constructor: "+ts.metodoActual);
        argumentosFormales();
        //System.out.println("Voy al bloque metodo del constructor de la clase: "+ts.claseActual.constructor.getNombre()+" con el token: "+token.getTipo());
        bloqueMetodo();
        ts.metodoActual.imprimirMetodo(ts.metodoActual, ts.claseActual);
        //ts.claseActual.constructor.active = true;
        //ts.claseActual.inConstructor = false;
    }

    // Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar ;
    private void atributo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // guardo en la TS el atributo con la posicion, visibilidad, tipo, nombre
        boolean vis = visibilidadOpt(); // si no entra a visibilidad es de tipo priv
        Tipo tipo = tipo();
        listaDeclaracionVar(vis, tipo);
        // salgo de aca y voy a haber guardado por ej para: Int a,b
        // pos visibilidad tipo nombre
        //| 0 | pub | Int a | b |
        match("ptoComa");
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
    private void listaDeclaracionVar(boolean vis, Tipo tipo) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
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
                    //System.out.println("Guardo en la lista de variables del metodo: "+ts.metodoActual.getNombre()+ " la variable: "+varLocal.getNombre());
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

                }
            }
            match("idMetVar");

        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un idMetVar y se recibio: "+token.getTipo());
        }
        listaDeclaracionVarRec(vis, tipo);
    }

    // ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
    private void listaDeclaracionVarRec(boolean vis, Tipo tipo) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("coma")){
            match("coma");
            listaDeclaracionVar(vis, tipo);
        }
    }

    // BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
    private void bloqueMetodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("llaveAbre");
        // voy a lista declaracion var local con el metodo actual
        listaDeclaracionVarLocal();
        // voy a hacer un print de las variables locales de un metodo para ver que me devuelve
        //System.out.println(ts.metodoActual.listaVarLocales.toString());
        // tengo que ir a lista sentencia con el retorno del metodo
        //System.out.println("El retorno del metodo: "+ts.metodoActual.getNombre()+" es: "+ts.metodoActual.tipoRetorno.getNombreTipo());
        //Tipo tipoRetorno = ts.metodoActual.tipoRetorno; // si es null es de retorno void
        listaSentencia(); // le tengo que pasar el tipo
        match("llaveCierra");
    }

    // ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal | lambda
    private void listaDeclaracionVarLocal() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // recursiva
        // si lo que viene esta en los primeros de declaracionVarLocal es porque no es lambda
        if (esPrimeroDeclaracionVarLocal(token.getTipo())){
            declaracionVarLocal(); // en declaracion variables las voy a guardar en la TS
            listaDeclaracionVarLocal();

        }
    }

    // ListaSentencia -> Sentencia ListaSentencia | lambda
    private void listaSentencia() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // mientras este en los primeros de sentencia vuelvo a entrar
        if (esPrimeroSentencia(token.getTipo())){
            sentencia();
            listaSentencia();
        }
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
    private void declaracionVarLocal() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // en la hash de ListaVariablesLocales de un metodo voy a guardar:
        // la pos
        Tipo tipo = tipo(); //guardo el tipo
        // agrego vis pero porque lo uso para atributo, ver bien como seria en los metodos
        boolean vis = false;
        listaDeclaracionVar(vis, tipo); //guardo el o los nombres de la variable
        match("ptoComa");
    }

    // ListaArgumentosFormalesOpt -> ListaArgumentosFormales | lambda
    private void listaArgumentosFormalesOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // Prim(ListaArgumentosFormales) = {str, Bool, Int, idClass, Array}
        String tipo = token.getTipo();
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
            listaArgumentosFormales();
        }
    }

    // ListaArgumentosFormales -> ArgumentoFormal ListaArgumentosFormalesRec
    private void listaArgumentosFormales() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        argumentoFormal(); // en argumentoformal va a a agregar a la ts de ese metodo el argumento
        listaArgumentosFormalesRec();
    }

    // ListaArgumentosFormalesRec -> , ListaArgumentosFormales | lambda
    private void listaArgumentosFormalesRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("coma")){
            match("coma");
            listaArgumentosFormales();
        }
    }

    // ArgumentoFormal -> Tipo idMetAt
    private void argumentoFormal() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // en la hash de listaParametros voy a guardar
        Tipo tipo = tipo();
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
    private void sentencia(/*Tipo tipo*/) throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (token.getTipo().equals("ptoComa")){
            match("ptoComa");
        }
        else {
            if (token.getTipo().equals("parAbre")){
                sentenciaSimple();
                match("ptoComa");
            }
            else {
                if (token.getTipo().equals("prIf")){
                    match(("prIf"));
                    match("parAbre");
                    System.out.println("Voy a expresion con: "+token.getTipo());
                    expresion(); //devuelvo la condicion
                    match("parCierra");
                    sentenciaRec(); // como parametro
                }
                else {
                    if (token.getTipo().equals("prWhile")){
                        match("prWhile");
                        match("parAbre");
                        expresion();
                        match("parCierra");
                        sentencia();
                    }
                    else {
                        if (token.getTipo().equals("prFor")){
                            match("prFor");
                            match("parAbre");
                            tipoPrimitivo();
                            match("idMetVar");
                            match("prIn");
                            match("idMetVar");
                            match("parCierra");
                            sentencia();
                        }
                        else {
                            if (token.getTipo().equals("llaveAbre")){
                                bloque();
                            }
                            else {

                                if (token.getTipo().equals("prRet")){
                                    // aca rompo si el tipo del metodo es void
                                    //if (tipo.getNombreTipo().equals("Void")){
                                    //    throw new ErrorSemantico(token.getFila(), token.getColumna(), "El tipo de retorno del metodo es void, no puede haber un ret");
                                    //}
                                    match("prRet");
                                    expresionOpt();
                                }
                                else {
                                    // con idMetVar o con self voy a asignacion
                                    if (token.getTipo().equals("idMetVar") | token.getTipo().equals("prSelf")){
                                        asignacion();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // SentenciaRec -> Sentencia RecursivoElse
    private void sentenciaRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        sentencia();
        recursivoElse();
    }

    // RecursivoElse -> else Sentencia | lambda
    private void recursivoElse() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("prElse")){
            match("prElse");
            sentencia();
        }
        // sino es lambda
    }

    // ExpresionOpt -> Expresion | lambda
    private void expresionOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (esPrimeroExpresion(token.getTipo())){
            expresion();
        }
    }

    // SentenciaSimple -> ( Expresion )
    private void sentenciaSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("parAbre");
        expresion();
        match("parCierra");
    }

    //Expresion -> ExpresionOr
    private NodoExpresion expresion() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionOr();
        return null;
    }

    //BLoque -> { ListaSentencia }
    private void bloque() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("llaveAbre");
        listaSentencia();
        match("llaveCierra");
    }

    //Asignacion -> AccesoVarSimple = Expresion | AccesoSelfSimple = Expresion
    private void asignacion() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de acceso var simple entro
        // Prim(AccesoVarSimple) = {id}
        if (token.getTipo().equals("idMetVar")){
            accesoVarSimple();
            match("opIgual");
            expresion();
        }
        else {
            // si esta en los primeros de acceso self simple entro
            // Prim(AccesoVarSimple) = {self}
            if (token.getTipo().equals("prSelf")){
                accesoSelfSimple();
                match("opIgual");
                expresion();
            }
        }
    }

    // AccesoVarSimple -> id AccesoVarSImpleRec
    private void accesoVarSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("idMetVar");
        accesoVarSimpleRec();
    }

    // AccesoVarSimpleRec -> ListaEncadenadoSImple | [ Expresion ]
    private void accesoVarSimpleRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de lista enadenado simple entro ahi
        // Prim(ListaEncadenadoSimple) = {. , lambda}
        if (token.getTipo().equals("pto")){
            listaEncadenadoSimple();
        }
        else {
            if (token.getTipo().equals("corcheteAbre")){
                match("corcheteAbre");
                expresion();
                match("corcheteCierra");
            }
        }
    }

    // ListaEncadenadoSimple -> EncadenadoSimpple ListaEncadenadoSimple | lambda
    private void listaEncadenadoSimple() throws ErrorSintactico, ErrorLexico {
        // es recursiva por lo tanto cada vez que viene un primero de encadenado simple vuelvo a entrar
        // Prim(EncadenadoSimple) = {.}
        if (token.getTipo().equals("pto")){
            encadenadoSimple();
            listaEncadenadoSimple();
        }
       // else {
            //sueldo
       // }
    }

    // AccesoSelfSimple -> self ListaEncadenadoSimple
    private void accesoSelfSimple() throws ErrorSintactico, ErrorLexico {
        match("prSelf");
        listaEncadenadoSimple();
    }

    // EncadenadoSimple -> . id
    private void encadenadoSimple() throws ErrorSintactico, ErrorLexico {
        match("pto");
        match("idMetVar"); //sueldo
    }

    // ExpresionOr -> ExpresionAnd ExpresionOrRec
    private void expresionOr() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionAnd();
        expresionOrRec();
    }

    // ExpresionOrRec -> || ExpresionAnd ExpresionOrRec | lambda
    private void expresionOrRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        while (token.getTipo().equals("opOr")){
            match("opOr");
            expresionAnd();
            expresionOrRec();
        }
    }

    // ExpresionAnd -> ExpIgual ExpAndRec
    private void expresionAnd() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionIgual();
        expresionAndRec();
    }

    //ExpresionAndRec -> && ExpIgual ExpresionAndRec | lambda
    private void expresionAndRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("opAndLog")){
            match("opAndLog");
            expresionIgual();
            expresionAndRec();
        }
    }

    // ExpresionIgual -> ExpresionComp ExpresionIgualRec
    private void expresionIgual() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionComp();
        expresionigualRec();
    }

    // ExpresionIgualRec -> OpIgual ExpresionComp ExpresionIgualRec | lambda
    private void expresionigualRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // voy a repetir siempre que vengan los primros de opIgual
        // Prim(OpIgual) = { == , != }
        if (token.getTipo().equals("opIgualIgual") | token.getTipo().equals("opDiferente")){
            opIgual();
            expresionComp();
            expresionigualRec();
        }
    }

    // ExpresionComp -> ExpresionAd ExpresionCompRec
    private void expresionComp() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionAd();
        expresionCompRec();
    }

    // ExpresionCompRec -> OpComp ExpresionAd | lambda
    private void expresionCompRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // deben venir los primeros de opComp
        // Prim(OpComp) = {<, >, <=, >=}
        if (token.getTipo().equals("opMenor") | token.getTipo().equals("opMenorIgual")  | token.getTipo().equals("opMayor")
                | token.getTipo().equals("opMayorIgual")){
            opComp();
            expresionAd();
        }
    }

    // ExpresionMul -> ExpresionUnario ExpresionMulRec
    private void expresionMul() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionUnario();
        expresionMulRec();
    }

    // ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
    private void expresionMulRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // simpre que venga un opMul hago recursividad
        if (token.getTipo().equals("opPor") | token.getTipo().equals("opdiv")){
            opMul();
            expresionUnario();
            expresionMulRec();
        }
    }

    // ExpresionUnario -> OpUnario ExpresionUnario | Operando
    private void expresionUnario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // siempre que venga un opUnario vuelvo
        if (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos") |
                token.getTipo().equals("opMasMas") | token.getTipo().equals("opMenosMenos") | token.getTipo().equals("opNot")){
            opUnario();
            expresionUnario();
        }
        else { // si no es opMas ni opMenos es un operando
            // si lo que viene no esta en los prim de operando no voy
            if (esPrimeroOperando(token.getTipo())) {
                operando();
            } else {
                throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba un operando y se enontro "+token.getTipo());
            }
            //operando();
        }
    }

    // ExpresionAd -> ExpresionMul ExpresionAdRec
    private void expresionAd() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        expresionMul();
        expresionAdRec();
    }

    // ExpresionAdRec -> OpAd ExpresionMul ExpresionAdRec | lambda
    private void expresionAdRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es recursiva cada vez que venga un opAd vuelvo a entrar
        // Prim(OpAd) = {+ , -}
        if (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos")){
            opAd();
            expresionMul();
            expresionAdRec();
        }
    }

    // OpIgual -> == | !=
    private void opIgual() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("opIgualIgual")) {
            match("opIgualIgual");
        }
        else {
            if (token.getTipo().equals("opDiferente")){
                match("opDiferente");
            }
        }
    }

    // opComp -> < | > | <= | >=
    private void opComp() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        switch (tipo){
            case "opMayor":
                match("opMayor");
                break;
            case "opMayorIgual":
                match("opMayorIgual");
                break;
            case "opMenor":
                match("opMenor");
                System.out.println("Matheo opMenor y salgo con: "+token.getTipo());
                break;
            case "opMenorIgual":{
                match("opMenorIgual");
                break;
            }
        }
    }

    // opAd -> + | -
    private void opAd() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("opMas")) {
            match("opMas");
        }
        else {
            if (token.getTipo().equals("opMenos")){
                match("opMenos");
            }
        }
    }

    // opUnario -> + | - | ++ | -- | !
    private void opUnario() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        switch (tipo){
            case "opMas":
                match("opMas");
                break;
            case "opMenos":
                match("opMenos");
                break;
            case "opMasMas":
                match("opMasMas");
                break;
            case "opMenosMenos":
                match("opMenosMenos");
                break;
            case "opNot":
                match("opNot");
                break;
        }
    }

    // OpMul -> * | /
    private void opMul() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("opPor")) {
            match("opPor");
        }
        else {
            if (token.getTipo().equals("opdiv")){
                match("opdiv");
            }
        }
    }

    // Operando -> Literal | Primario EncadenadoOpt
    private void operando() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = token.getTipo();
        //System.out.println("Estoy en operando con: "+token.getTipo());
        // si viene un literal
        switch (tipo){
            // Prim(Literal) = {nil, true, false, intLiteral, strLiteral}
            case "prNil" , "prTrue", "prFalse", "literal_entero", "literal_cadena":
                literal();
                break;
            // Prim(Primario) = { (, self, id, idclass, new}
            case "parAbre", "prSelf", "idMetVar", "idClass", "prNew":
                System.out.println("Voy a primario con: "+token.getTipo());
                primario();
                encadenadoOpt();

        }
        //System.out.println("salgo de operando con: "+token.getTipo());
    }

    // EncadenadoOpt -> Encadenado | lambda
    private void encadenadoOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si es pto va a encadendo, Prim(Encadenado) = { . }
        if (token.getTipo().equals("pto")){
            encadenado();
        }
    }

    // Literal -> nil | true | false | intLiteral | strLiteral
    private void literal() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        switch (tipo){
            case "prNil":
                match("prNil");
                break;
            case "prTrue":
                match("prTrue");
                break;
            case "prFalse":
                match("prFalse");
                break;
            case "literal_entero":
                match("literal_entero");
                break;
            case "literal_cadena":
                match("literal_cadena");
                break;
        }
    }

    // Primario -> ExpresionParentizada | AccesoSelf | AccesoVar | LlamadaMetodo | LlamadaMetodoEstatico | LlamadaConClassor
    private void primario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = token.getTipo();
        switch (tipo){
            // Prim(ExpresionParentizada) = { ( }
            case "parAbre":
                expresionParentizada();
                break;
            // Prim(AccesoSelf) = { self }
            case "prSelf":
                accesoSelf();
                break;
            // Prim(AccesoVar) = { id } y Prim(LlamadaMetodo) = { id }
            // como ambas van a id veo los siguientes
            case "idMetVar":
                // si me viene un parAbre es porque fue a LlamadaMetodo
                System.out.println("estoy en primario con: "+token.getTipo());
                Token next = lookAhead();
                if (next.getTipo().equals("parAbre")){
                    llamadaMetodo();
                }
                else {
                    accesoVar();
                }
                break;
            // Prim(LlamadaMetodoEstatico) = {idClass}
            case "idClass":
                llamadaMetodoEstatico();
                break;
            // Prim(LlamadaConClassor) = {new}
            case "prNew":
                llamadaConClassor();
                break;
        }
    }

    // ExpresionParentizada -> ( Expresion ) EncadenadoOpt
    private void expresionParentizada() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("parAbre");
        expresion();
        match("parCierra");
        encadenadoOpt();
    }

    // AccesoSelf -> self EncadenadoOpt
    private void accesoSelf() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("prSelf");
        encadenadoOpt();
    }

    // AccesoVar -> id AccesoVarRec
    private void accesoVar() throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (ts.noEstaTs(token.getLexema())){
            throw new ErrorSemantico(token.getFila(), token.getColumna(), "La variable " + token.getLexema() + "no ha sido declarada o es un atributo de clase con visibilidad privada");
        }
        else {
            RegistroVariable variable = ts.getVariable(token.getLexema());
        }
        // creo el nodo id antes de hacer match
        NodoId nodoId = new NodoId(token.getFila(), token.getColumna(), token);
        match("idMetVar");
        //accesoVarRec();
        NodoAccesoVarRec nodoAccesoVarRec = accesoVarRec();
        if (!nodoAccesoVarRec.nodoDer.equals(null)){

        }



    }

    //AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    private NodoAccesoVarRec accesoVarRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getTipo().equals("corcheteAbre")){
            match("corcheteAbre");
            NodoExpresion nodoExpresion = expresion();
            Tipo tipoArray = nodoExpresion.chequear(true);
            match("corcheteCierra");
            encadenadoOpt();
            return null;
            //return new NodoAccesoVarRec(nodoExpresion, );
        }
        else {
            encadenadoOpt();
        }
        return null;
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
            encadenadoOpt();
        }
        else {
            tipoPrimitivo();
            match("corcheteAbre");
            expresion();
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
        expresion();
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
    private void encadenado() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        match("pto");
        encadenadoRec();
    }

    // EncadenadoRec -> LlamadaMetodo | AccesVar
    private void encadenadoRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // como con ambos me llega id veo el nextToken
        Token next = lookAhead();
        if (next.getTipo().equals("parAbre")){ // es porq esta en llamada metodo
            llamadaMetodo();
        }
        else {
            accesoVar();
        }
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