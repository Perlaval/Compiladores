package sintactico;

import lexico.ErrorLexico;
import lexico.Token;
import semantico.ErrorSemantico;
import semantico.ValidarDeclaracion;
import semantico.nodos.declaraciones.*;
import semantico.nodos.definiciones.NodoClase;
import semantico.nodos.definiciones.NodoDefinicion;
import semantico.nodos.definiciones.NodoImpl;
import semantico.nodos.miembro.NodoMetodo;
import semantico.nodos.sentencia.NodoSentencia;
import semantico.registros.*;
import semantico.tipos.*;

import java.util.ArrayList;

public class ParserDeclaraciones {
    private final Parser parser;

    public ParserDeclaraciones(Parser parser) {
        this.parser = parser;
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA DEFINICIONES:
    //      - ListaDefiniciones -> Clase ListaDefiniciones | Implementacion ListaDefiniciones | lambda
    //------------------------------------------------------------------------------------------------------------------
    public ArrayList<NodoDefinicion> listaDefiniciones(ArrayList<NodoDefinicion> listaDef) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es recursiva mientras lea la palabra reservada class o impl, tiene que volver a entrar
        if (parser.token().getTipo().equals("prClass") || parser.token().getTipo().equals("prImpl")){
            if (parser.token().getTipo().equals("prClass")){
                listaDef.add(clase());
                return listaDefiniciones(listaDef);
            }
            else {
                listaDef.add(impl());
                return listaDefiniciones(listaDef);
            }
        }
        if (!parser.token().getLexema().equals("start")){
            throw new ErrorSemantico(parser.token(), "Se esperaba impl, class o start y se encontró '"+parser.token().getLexema()+"'");
        }
        return listaDef;
    }

    //------------------------------------------------------------------------------------------------------------------
    // CLASE:
    //      - Class -> class idClass HerenciaOpt { listaAtributos }
    //------------------------------------------------------------------------------------------------------------------
    private NodoClase clase() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("prClass");
        // si el id esta en las clases predefinidas -> error
        if (parser.ts().isNombreClasePredefinida(parser.token().getLexema())){
            throw new ErrorSemantico(parser.token(), "La clase: "+parser.token().getLexema()+" No se puede redefinir, es una clase predefinida");
        }
        if (parser.token().getTipo().equals("idClass")){
            Token id = parser.token(); // guardo el token para guardarlo en la ts, porque cuando matcheo avanzo entonces lo pierdo
            parser.match("idClass");
            RegistroClase clase;
            if (parser.ts().noEstaTs(id.getLexema())){
                // no esta guardada la clase en la TS
                if (parser.token().getTipo().equals("dosPuntos")){ // tiene herencia
                    String superClase = herenciaOpt();
                    clase = parser.ts().crearRegClase(id.getLexema(), superClase);
                }
                else {
                    clase = parser.ts().crearRegClase(id.getLexema(), null); // le colocamos que hereda de null, en la consolidacion se le coloca que hereda de Object
                }
                parser.ts().tablaClases.put(clase.getNombre(), clase);
            }
            else { // si ya esta en la TS verifico que no haya redefinicion de herencia
                clase = parser.ts().getClase(id.getLexema());
                if (parser.token().getTipo().equals("dosPuntos")) {
                    String padre = herenciaOpt();
                    // si esta declarada la firme debe coincidir porque la guarde desde class
                    if (clase.declarada){
                        if (!clase.heredaDe.equals(padre)){
                            throw new ErrorSemantico(parser.token(),
                                    "Redefinicion de herencia para la clase: "+clase.getNombre());
                        }
                    }
                    else {
                        // si no esta declarada, la guarde desde impl entonces le seteo la herencia
                        clase.setHeredaDe(padre);
                    }
                }
            }
            clase.setTokenClase(id); // token utilizado para largar errores durante la consolidacion
            clase.setDeclarada(true); // la declaro
            // contexto para atributos
            parser.ts().claseActual = clase;
            parser.match("llaveAbre");
            // si lo que viene es } es entonces listaAtr es lambda
            ArrayList<NodoDeclaracion> listaAtr = listaAtributos(new ArrayList<NodoDeclaracion>());
            parser.match("llaveCierra");
            // limpio la clase actual porque salgo
            parser.ts().claseActual = null;
            return new NodoClase(id, listaAtr);
        }
        else {
            throw new ErrorSintactico(parser.token(), "Se esperaba un idClass y se recibio: "+parser.token().getTipo());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // HERENCIA OPT:
    //      - HerenciaOpt -> Herencia | lambda
    //      - Acá como puede ser opcional si va a herencia o no, necesito los primeros y siguientes
    //------------------------------------------------------------------------------------------------------------------
    private String herenciaOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico{
        // Sig(HerenciaOpt) = { "{" }
        // si el token que viene no esta en los primeros de herencia es porque o vino {, entonces aca no hace nada, o vino algo mal
        // entonces verifico con los primeros
        String heredaDe = null;
        if (parser.token().getTipo().equals("dosPuntos")) {
            heredaDe = herencia();
        }
        return heredaDe;
    }

    //------------------------------------------------------------------------------------------------------------------
    // HERENCIA:
    //      - Herencia -> : Tipo
    //------------------------------------------------------------------------------------------------------------------
    private String herencia() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("dosPuntos");
        String superClase;
        Tipo tipoSuperClase;
        // verifica si o si que lo que se recibe es un idClass
        // es un error heredar o redefinir las clases predefinidas
        // hago doble if para poder comunicar bien el error
        if (parser.token().getTipo().equals("idClass") || parser.ts().isNombreClasePredefinida(parser.token().getLexema())) {
            if (!parser.ts().isNombreClasePredefinida(parser.token().getLexema())) { // si es idClass y No es predefinida entonces sigo
                tipoSuperClase = tipo(); // como es idClass va a ir a TipoReferencia
                superClase = tipoSuperClase.getNombreTipo();
            }
            else {
                throw new ErrorSemantico(parser.token(), "Es un error heredar de la clase: " +parser.token().getLexema());
            }
        }
        else {
            throw new ErrorSemantico(parser.token(), "Se esperaba un id de clase y se encontro: "+parser.token().getLexema());
        }
        return superClase;
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA ATRIBUTOS:
    //      - ListaAtributos -> Atributo ListaAtributos | lambda
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaAtributos(ArrayList<NodoDeclaracion> listaAtr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = parser.token().getTipo();
        // si lo que viene no esta en los primeros de Atributo es porque listaAtributos es lambda entonces aca no hace nada
        // es recursiva, por lo tanto siempre que venga alguno de los primeros de A vuelvo a entrar
        // como puede no tener prPub, tambien puedo ir directamente a Tipo
        if (tipo.equals("prPub") | tipo.equals("tStr") | tipo.equals("tBool") | tipo.equals("tInt") | tipo.equals("idClass")) { //| tipo.equals("Array")
            ArrayList<NodoDeclaracion> listaAtrNueva = atributo(listaAtr);
            return listaAtributos(listaAtrNueva);
        }
        return listaAtr;
    }

    //------------------------------------------------------------------------------------------------------------------
    // IMPL:
    //      - Impl -> impl idClass { ListaMiembros }
    //------------------------------------------------------------------------------------------------------------------
    private NodoImpl impl() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tImpl = parser.token();
        parser.match("prImpl");
        if (parser.ts().isNombreClasePredefinida(parser.token().getLexema())){
            throw new ErrorSemantico(parser.token(), "La clase: "+parser.token().getLexema()+" No se puede redefinir, es una clase predefinida");
        }
        if (parser.token().getTipo().equals("idClass")){
            if (parser.ts().noEstaTs(parser.token().getLexema())){
                // no esta esa clase, la agrego
                RegistroClase clase = new RegistroClase(parser.token().getLexema());
                //System.out.println("La clase: "+clase.getNombre()+ "  hereda de: "+clase.getHeredaDe());
                // le seteo declarada a false porque la guarde desde un impl
                clase.setDeclarada(false);
                // le seteo el token por si luego no se declara para lanzar el error
                clase.setTokenClase(tImpl);

                // le seteo que tiene un impl esa clase
                clase.setImplementada(true);
                parser.ts().tablaClases.put(clase.getNombre(), clase);
                parser.ts().claseActual = clase;
            }
            // obtengo la clase actual para guardarle los metodos
            RegistroClase clase = parser.ts().getClase(parser.token().getLexema());
            parser.ts().claseActual = clase;
            clase.setImplementada(true);
            parser.match("idClass");
            // entonces ahora voy a ir a lista miembros con la clase actual
            parser.match("llaveAbre");
            ArrayList<NodoMetodo> listaMiembros = listaMiembros(new ArrayList<NodoMetodo>());
            parser.match("llaveCierra");
            // salgo de este impl, vuelvo la clase actual a null
            parser.ts().claseActual = null;
            parser.ts().metodoActual = null;
            return new NodoImpl(tImpl, clase.nombre, listaMiembros);
        }
        else {
            throw new ErrorSintactico(parser.token(), "Se esperaba un idClass");
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA MIEMBROS:
    //      - ListaMiembros -> Miembro ListaMiembros | lambda
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoMetodo> listaMiembros(ArrayList<NodoMetodo> listaMet) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si lo que viene esta en los primeros de miembro es porque lista miembro no es lambda
        // Prim(E) = { st, . , lambda}
        if (parser.esPrimeroMiembro(parser.token()) | parser.token().getTipo().equals("prFn")){
            listaMet.add(miembro());
            return listaMiembros(listaMet);
        }
        return listaMet;
    }

    //------------------------------------------------------------------------------------------------------------------
    // MIEMBRO:
    //      - Miembro -> Metodo | Constructor
    //------------------------------------------------------------------------------------------------------------------
    private NodoMetodo miembro() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // tengo dos opciones o voy a metodo o voy a constructor
        // cuando no tengo st puede venir fn
        if (parser.token().getTipo().equals("prSt") | parser.token().getTipo().equals("prFn")){
            return metodo();
        }
        else {
            // si va a constructor verifico si esa clase ya tiene constructor, si es asi largo error
            if (parser.ts().claseActual.getConstructor() != null){
                throw new ErrorSemantico(parser.token(), "El constructor de la clase: " + parser.ts().claseActual.nombre + " ya ha sido declarado");
            }
            return constructor();

        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR:
    //      - Constructor -> . ArgumentosFormales BloqueMetodo
    //------------------------------------------------------------------------------------------------------------------
    private NodoMetodo constructor() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si entra en constructor es porque no habia sido declarado todavia
        // el cheuqueo de + de un constructor se hace en el metodo miembro
        Token tConst = parser.token();
        parser.match("pto");
        parser.ts().claseActual.inConstructor = true; // seteo la clase actual como el constructor
        //parser.ts().claseActual.constructor = new Constructor(); // lo creo
        parser.ts().claseActual.setConstructor(new Constructor()); // lo creo
        parser.ts().metodoActual = parser.ts().claseActual.getConstructor(); // actualizo el metodo actual

        ArrayList<NodoDeclaracion> listaArg = argumentosFormales(new ArrayList<NodoDeclaracion>());

        //ts.metodoActual.imprimirMetodo(ts.metodoActual, ts.claseActual);
        return new NodoMetodo(parser.token(), listaArg, bloqueMetodo(), parser.ts().metodoActual);
        //ts.claseActual.constructor.active = true;
        //ts.claseActual.inConstructor = false;
    }

    //------------------------------------------------------------------------------------------------------------------
    // METODO:
    //      - Metodo -> FormaMetodoOpt fn TipoMetodoOpt idMetAt ArgumentosFormales BloqueMetodo
    //------------------------------------------------------------------------------------------------------------------
    private NodoMetodo metodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        boolean estatico = formaMetodoOpt(); // true = estatico, false = no estatico
        parser.match("prFn");
        // si el tipo de retorno es vacio es porque es void
        Tipo tipo = tipoMetodoOpt(); // va a guardar en la ts el tipo de retorno de la funcion
        // aca es donde guardo el metodo con forma, tipo nombre
        Token tMetodo = parser.token();
        if (parser.token().getTipo().equals("idMetVar")){

            if (!parser.ts().validarNombre(ValidarDeclaracion.Definicion.METODO, parser.token().getLexema())){
                throw new ErrorSemantico(parser.token(), "El nombre: "
                        +parser.token().getLexema()+" no es válido ya que representa un tipo especial");
            }

            // analizar los casos de redefinicion, de metodos heredados
            // en la misma clase no puedo tener dos metodos con el mismo nombre
            if (parser.ts().claseActual.listaMetodos.containsKey(parser.token().getLexema())){
                throw new ErrorSemantico(parser.token(), "Ya existe un metodo con el nombre: "
                        +parser.token().getLexema()+" en el impl de la clase: "+parser.ts().claseActual.getNombre());
            }
            // caso base agregar el metodo a la lista de metodos de la clase
            RegistroMetodo metodo = parser.ts().crearRegMetodo(parser.token().getLexema(),estatico,tipo);
            metodo.setTokenMetodo(tMetodo);
            parser.match("idMetVar");
            parser.ts().claseActual.listaMetodos.put(metodo.getNombre(), metodo);
            // seteo el metodo actual para los parametros y varlocales
            parser.ts().metodoActual = metodo;
            //System.out.println("Metodo: "+metodo.getNombre());
        }
        else {
            throw new ErrorSintactico(parser.token(), "Se esperaba un idMetVar y se recibio: "+parser.token().getTipo());
        }
        // ya guarde el metodo en la ts.claseactual.listametodos, ahora voy a sus parametros y varlocales
        // voy a argumentos formales con el metodoactual
        ArrayList<NodoDeclaracion> listaArg = argumentosFormales(new ArrayList<NodoDeclaracion>()); //voy a guardar en la hash de listaParametros todos los argumentos

        // voy a ir a bloque metodo con el metodoactual
        //ts.metodoActual.imprimirMetodo(ts.metodoActual, ts.claseActual);
        return new NodoMetodo(tMetodo, listaArg, bloqueMetodo(), parser.ts().metodoActual);
    }

    //------------------------------------------------------------------------------------------------------------------
    // FORMA METODO OPT:
    //      - formaMetodoOpt -> formaMetodo | lambda
    //------------------------------------------------------------------------------------------------------------------
    private boolean formaMetodoOpt() throws ErrorSintactico, ErrorLexico {
        // si el token que viene esta en los primeros de formaMetodo tengo que entrar
        // si viene otra cosa no hace nada y si no viene nada no entra y es valido
        if (parser.token().getTipo().equals("prSt")){
            return formaMetodo();
        }
        return false; // si no entra es porque no es estatico, devuelo false
    }

    //------------------------------------------------------------------------------------------------------------------
    // FORMA METODO:
    //      - FormaMetodo -> st
    //------------------------------------------------------------------------------------------------------------------
    private boolean formaMetodo() throws ErrorSintactico, ErrorLexico {
        parser.match("prSt"); // lo manejo con flags para guardar en la TS
        return true; // es estatico
    }

    //------------------------------------------------------------------------------------------------------------------
    // TIPO METODO OPT:
    //      - TipoMetodoOpt -> TipoMetodo | lambda
    //------------------------------------------------------------------------------------------------------------------
    private Tipo tipoMetodoOpt() throws ErrorSintactico, ErrorLexico {
        // si el tokoen esta en los primeros de tipoMetodo entro
        if (parser.esPrimeroTipoMetodo(parser.token())){
            return tipoMetodo();
        }
        return new TipoVoid();
    }

    //------------------------------------------------------------------------------------------------------------------
    // TIPO METODO:
    //      - TipoMetodo -> Tipo | void
    //      - Prim(Tipo)= {str, bool int, idClass, Array} // Array es un idClass
    //------------------------------------------------------------------------------------------------------------------
    private Tipo tipoMetodo() throws ErrorSintactico, ErrorLexico {
        if (parser.esPrimeroTipoMetodo(parser.token())){
            return tipo();
        }
        /*
        if (token.getTipo().equals("tStr") || token.getTipo().equals("tBool") || token.getTipo().equals("tInt") ||
                token.getTipo().equals("idClass") || token.getTipo().equals("tArray")) {
            return tipo();
        }*/
        else {
            parser.match("prVoid");
            return new TipoVoid();
        }

    }

    //------------------------------------------------------------------------------------------------------------------
    // TIPO:
    //      - Tipo -> TipoPrimitivo | TipoReferencia | TipoArreglo
    //------------------------------------------------------------------------------------------------------------------
    private Tipo tipo() throws ErrorSintactico, ErrorLexico {
        // si lo que viene esta en los primeros de tipo primitivo entro ahi
        Tipo tipo = null;
        if (parser.esPrimeroTipoPrimitivo(parser.token())){
            return tipoPrimitivo();
        }
        else {
            if (parser.token().getTipo().equals("idClass")){
                if (parser.token().getLexema().equals("Array")){
                    return tipoArreglo();
                }
                else{
                    if (parser.ts().bloqueStart != null){
                        //System.out.println("Voy a tipo referencia con: "+parser.token().getLexema());
                    }
                    return tipoReferencia();
                }
            }
        }
        return tipo;
    }

    //------------------------------------------------------------------------------------------------------------------
    // TIPO PRIMITIVO:
    //      - TipoPrimitivo -> Str | Bool | Int
    //------------------------------------------------------------------------------------------------------------------
    public Tipo tipoPrimitivo() throws ErrorSintactico, ErrorLexico {
        if (parser.esPrimeroTipoPrimitivo(parser.token())){
            String tipo = parser.token().getTipo();
            parser.match(parser.token().getTipo());
            return new TipoPrimitivo(tipo);
        }
        else {
            throw new ErrorSintactico(parser.token(),
                    "Se esperaba un tipo primitivo (Int, Str, Bool), y se recibio: "+parser.token().getLexema());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // TIPO REFERENCIA:
    //      - TipoReferencia -> idClass
    //------------------------------------------------------------------------------------------------------------------
    private Tipo tipoReferencia() throws ErrorSintactico, ErrorLexico {
        String nombre = parser.token().getLexema();
        parser.match("idClass");
        return new TipoReferencia(nombre);
    }

    //------------------------------------------------------------------------------------------------------------------
    // TIPO ARRAY:
    //      - TipoArray -> Array TipoPrimitivo
    //------------------------------------------------------------------------------------------------------------------
    private Tipo tipoArreglo() throws ErrorSintactico, ErrorLexico {
        parser.match("idClass");
        return new TipoArreglo(tipoPrimitivo());
    }

    //------------------------------------------------------------------------------------------------------------------
    // ARGUMENTOS FORMALES:
    //      - ArgumentosFormales -> ( ListaArgumentosFormalesOpt )
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> argumentosFormales(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("parAbre");
        ArrayList<NodoDeclaracion> listaArgNueva = listaArgumentosFormalesOpt(listaArg);
        parser.match("parCierra");
        return listaArgNueva;
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA ARGUMENTOS FORMALES OPT:
    //      - ListaArgumentosFormalesOpt -> ListaArgumentosFormales | lambda
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaArgumentosFormalesOpt(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // Prim(ListaArgumentosFormales) = {str, Bool, Int, idClass, Array}
        //String tipo = token.getTipo();
        if (parser.esPrimeroTipoPrimitivo(parser.token()) || parser.token().getTipo().equals("idClass")){
            //if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
            return listaArgumentosFormales(listaArg);
        }
        return listaArg;
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA ARGUMENTOS FORMALES:
    //      - ListaArgumentosFormales -> ArgumentoFormal ListaArgumentosFormalesRec
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaArgumentosFormales(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoDeclaracion nodoArgumento = argumentoFormal(); // en argumentoformal va a a agregar a la ts de ese metodo el argumento
        listaArg.add(nodoArgumento);
        return listaArgumentosFormalesRec(listaArg);
    }

    //------------------------------------------------------------------------------------------------------------------
    // ARGUMENTO FORMAL:
    //      - ArgumentoFormal -> Tipo idMetAt
    //------------------------------------------------------------------------------------------------------------------
    private NodoDeclaracion argumentoFormal() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // en la hash de listaParametros voy a guardar
        Tipo tipo = tipo();
        Token tArgu = parser.token();
        if (tipo == null){
            throw new ErrorSintactico(parser.token(), "Se esperaba un tipo para el parametro: "+parser.token().getLexema());
        }
        if (parser.token().getTipo().equals("idMetVar")){
            // no pueden haber dos parametros que se llamen igual para el mismo metodo
            if (parser.ts().metodoActual.listaParametros.containsKey(parser.token().getLexema())){
                throw new ErrorSintactico(parser.token(), "Ya existe un parametro con nombre: " + parser.token().getLexema());
            }
            else {
                // creo un argumento formal
                RegistroParametro parametro = parser.ts().crearRegParametros(parser.token().getLexema(), tipo);
                // no esta ese parametro lo agrego
                parametro.setPos(parser.ts().metodoActual.getProxPosParametro());
                parser.ts().metodoActual.listaParametros.put(parametro.getNombre(), parametro);
                parser.match("idMetVar");
                return new NodoArgumento(tArgu);
            }
        }
        else {
            throw new ErrorSintactico(parser.token(), "Se esperaba un idMetVar y se recibio: "+parser.token().getTipo());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA ARGUMENTOS FORMALES REC:
    //      - ListaArgumentosFormalesRec -> , ListaArgumentosFormales | lambda
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaArgumentosFormalesRec(ArrayList<NodoDeclaracion> listaArg) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.token().getTipo().equals("coma")){
            parser.match("coma");
            return listaArgumentosFormales(listaArg);
        }
        return listaArg;
    }

    //------------------------------------------------------------------------------------------------------------------
    // ATRIBUTO:
    //      - Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> atributo(ArrayList<NodoDeclaracion> listaAtr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // guardo en la TS el atributo con la posicion, visibilidad, tipo, nombre
        boolean vis = visibilidadOpt(); // si no entra a visibilidad es de tipo priv (false)
        Tipo tipo = tipo();
        ArrayList<NodoDeclaracion> listaAtrNueva = listaDeclaracionVar(vis, tipo, listaAtr);
        // salgo de aca y voy a haber guardado por ej para: Int a,b
        // pos visibilidad tipo nombre
        //| 0 | pub | Int a | b |
        parser.match("ptoComa");
        return listaAtrNueva;
    }

    //------------------------------------------------------------------------------------------------------------------
    // VISIBILIDAD OPT:
    //      - VisibilidadOpt -> Visibilidad | lambda
    //------------------------------------------------------------------------------------------------------------------
    private boolean visibilidadOpt() throws ErrorSintactico, ErrorLexico {
        // si lo que viene esta en los primeros de visibilidad entro
        if (parser.token().getTipo().equals("prPub")){
            return visibilidad();
        }
        return false;
    }

    //------------------------------------------------------------------------------------------------------------------
    // VISIBILIDAD:
    //      - Visibilidad -> pub
    //------------------------------------------------------------------------------------------------------------------
    private boolean visibilidad() throws ErrorSintactico, ErrorLexico {
        parser.match("prPub"); //lo manejo con flags para guardar en la TS
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    // BLOQUE METODO:
    //      - BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
    //------------------------------------------------------------------------------------------------------------------
    public NodoBloqueMetodo bloqueMetodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tBloqueM = parser.token();
        parser.match("llaveAbre");

        /*
        if (parser.ts().bloqueStart != null){
            System.out.println("Estoy entrando a start con: "+parser.token().getLexema());
        }*/
        if (parser.ts().bloqueStart != null){
            //System.out.println("Estoy en start y leo: "+parser.token().getLexema());
        }
        // voy a lista declaracion var local con el metodo actual
        ArrayList<NodoDeclaracion> listaVarLocal = listaDeclaracionVarLocal(new ArrayList<NodoDeclaracion>());
        //System.out.println(ts.metodoActual.listaVarLocales.toString());
        // tengo que ir a lista sentencia con el retorno del metodo
        //System.out.println("El retorno del metodo: "+ts.metodoActual.getNombre()+" es: "+ts.metodoActual.tipoRetorno.getNombreTipo());
        //Tipo tipoRetorno = ts.metodoActual.tipoRetorno; // si es null es de retorno void
        ArrayList<NodoSentencia> listaSent = parser.getParserSentencias().listaSentencia(new ArrayList<NodoSentencia>());
        parser.match("llaveCierra");
        return new NodoBloqueMetodo(tBloqueM, listaVarLocal, listaSent);
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA DECLARACION VAR LOCAL:
    //      - ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal | lambda
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaDeclaracionVarLocal(ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // recursiva
        // si lo que viene esta en los primeros de declaracionVarLocal es porque no es lambda
        // prim declaracion var local = primeros tipo metodo
        if (parser.esPrimeroTipoMetodo(parser.token())){
            declaracionVarLocal(listaDec); // en declaracion variables las voy a guardar en la TS
            return listaDeclaracionVarLocal(listaDec);
        }
        return listaDec;
    }

    //------------------------------------------------------------------------------------------------------------------
    // DECLARACION VAR LOCAL:
    //      - DeclaracionVarLocal -> Tipo ListaDeclaracionVar
    //------------------------------------------------------------------------------------------------------------------
    private void declaracionVarLocal(ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // en la hash de ListaVariablesLocales de un metodo voy a guardar:
        // la pos
        Tipo tipo = tipo(); //guardo el tipo
        // agrego vis pero porque lo uso para atributo, ver bien como seria en los metodos
        boolean vis = false;
        listaDeclaracionVar(vis, tipo, listaDec); //guardo el o los nombres de la variable
        parser.match("ptoComa");
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA DECLARACION VAR:
    //      - ListaDeclaracionVar -> idMetAt ListaDeclaracionesVarRec
    //      - Misma funcion para guardar los atributos y las variables locales de un metodo
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaDeclaracionVar(boolean vis, Tipo tipo, ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        //System.out.println("Metodo actual: "+ts.metodoActual.getNombre());
        // obtengo el metodo actual, si no es null es porque estoy en las variables locales de un metodo
        // por lo tanto no pueden repetirse los nombres de los parametros con los de las variables

        if (parser.token().getTipo().equals("idMetVar")){
            // variable del metodo

            if (parser.ts().metodoActual != null){
                // declarando variables locales del metodo
                // estoy en metodo
                // busco el idmetvar que voy a agregar en la lista de los parametros y si existe largo error, no pueden llamarse igual

                if (parser.ts().metodoActual.listaParametros.containsKey(parser.token().getLexema())){
                    throw new ErrorSemantico(parser.token(),
                            "El nombre de la variable: "+parser.token().getLexema()+ " ya fue asignado para un parametro");
                }
                else{
                    // no existe ese nombre en la lista de parametros por lo tanto creo una nueva variable del metodo
                    RegistroVariable varLocal;
                    varLocal = parser.ts().crearRegVar(parser.token().getLexema(), tipo);
                    varLocal.setPos(parser.ts().metodoActual.getProxPosVarLocal());
                    varLocal.setTokenVarLocal(parser.token());
                    parser.ts().metodoActual.listaVarLocales.put(varLocal.getNombre(), varLocal);
                    listaDec.add(new NodoVariableLocal(parser.token()));
                    //System.out.println("Guardo en la lista de variables del metodo: "+ts.metodoActual.getNombre()+ " la variable: "+varLocal.getNombre());
                }
            }
            else {
                if (parser.ts().bloqueStart != null){
                    // estoy en bloque start
                    if (parser.ts().bloqueStart.listaVariables.containsKey(parser.token().getLexema())){
                        throw new ErrorSemantico(parser.token(),
                                "El nombre de la variable '"+parser.token().getLexema()+ "' ya fue asignado");
                    }
                    // variables locales de start
                    RegistroVariable varLocal;
                    varLocal = parser.ts().crearRegVar(parser.token().getLexema(), tipo);
                    varLocal.setPos(parser.ts().bloqueStart.getProxPosVarLocal());
                    varLocal.setTokenVarLocal(parser.token()); // le seteo el token para luego lanzar bien errores
                    parser.ts().bloqueStart.listaVariables.put(varLocal.getNombre(), varLocal);
                    listaDec.add(new NodoAtributo(parser.token()));
                }
                else {
                    // Atributos de una clase
                    RegistroAtributo atributo;
                    // atributo de clase
                    // verifico que no este guardado ya en la lista de atributos
                    if (parser.ts().claseActual.listaAtributos.containsKey(parser.token().getLexema())){
                        // si ya esta, lanzo error semantico
                        throw new ErrorSemantico(parser.token(), "Atributo: "+parser.token().getLexema()+" ,repetido");
                    }
                    else {
                        atributo = parser.ts().crearRegAtributo(parser.token().getLexema(), tipo, vis);
                        atributo.setPos(parser.ts().claseActual.getProxPosAtributo());
                        atributo.setTokenAtributo(parser.token()); // le seteo el token para luego lanzar bien errores
                        parser.ts().claseActual.listaAtributos.put(atributo.getNombre(), atributo);
                        listaDec.add(new NodoAtributo(parser.token()));
                    }

                }
            }
            parser.match("idMetVar");
        }
        else {
            throw new ErrorSintactico(parser.token(), "Se esperaba un idMetVar y se recibio: "+parser.token().getTipo());
        }
        return listaDeclaracionVarRec(vis, tipo, listaDec);
    }

    //------------------------------------------------------------------------------------------------------------------
    // LISTA DECLARACION VAR REC:
    //      - ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
    //------------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoDeclaracion> listaDeclaracionVarRec(boolean vis, Tipo tipo, ArrayList<NodoDeclaracion> listaDec) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.token().getTipo().equals("coma")){
            parser.match("coma");
            return listaDeclaracionVar(vis, tipo, listaDec);
        }
        return listaDec;
    }
}
