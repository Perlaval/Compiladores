package sintactico;
import java.util.List;
import java.util.ArrayList;
import lexico.Token;
import lexico.Lexico;


// analizador sintactico
public class Sintactico {
    private List<Token> listaTokens; //Lista de tokens que obtuve del lexico
    private int puntero;
    private Token token;

    //constructor
    public Sintactico(List<Token> listaTokens){
        this.listaTokens = listaTokens;
        this.puntero = 0;
        this.token = listaTokens.get(0);
    }

    //clase
    public void analizador() throws ErrorSintactico {
        // Program -> ListaDefiniciones Start
        program();
        // si sale de program es porque hizo match con $ entonces devolver Exito!


    }

    // Gramatica ----------------------------------------------------------------------------------------------

    //Program -> ListaDefiniciones Start
    private void program() throws ErrorSintactico{
        listaDefiniciones();
        // si es lambda va directo a start
        start();
        match("EOF"); // ver si tiene que ser $ o EOF
    }

    // Start -> start BloqueMetodo
    private void start() throws ErrorSintactico{
        // matcheo start asi avanza
        //match("prStart"); // esto tmb verificar porque nose si start era una palabra reservada (pregintar a profe)
        if (token.getLexema().equals("start")){
            // deberia matchear idMetVar, porque start al no ser reservada la toma como idMetVar
            match("idMetVar"); //consumo start y voy a bloque
            bloqueMetodo();
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba start y se enontro "+token.getTipo());
        }

    }

    // ListaDefiniciones -> Clase ListaDefiniciones | Implementacion ListaDefiniciones | lambda
    private void listaDefiniciones() throws ErrorSintactico{
        // es recursiva, por lo que voy a agregar un while, mientras lea la palabra reservada class o impl, tiene que volver a entrar
        while (token.getTipo().equals("prClass") || token.getTipo().equals("prImpl")){
            if (token.getTipo().equals("prClass")){
                clase();
            }
            else {
                impl();
            }
        }
    }

    // Class -> class idClass HerenciaOpt { listaAtributos }
    private void clase() throws ErrorSintactico{
        match("prClass");
        match("idClass");
        herenciaOpt(); //metodo creado a partir del ?, ya que puede o no estar
        match("llaveAbre");
        listaAtributos(); // si lo que viene es } es porque era lambda
        match("llaveCierra");

    }

    // HerenciaOpt -> Herencia | lambda
    // aca como puede ser opcional si va a herencia o no, necesito los primeros y siguientes
    private void herenciaOpt() throws ErrorSintactico{
        // Sig(HerenciaOpt) = { { }
        // si el token que viene no esta en los primeros de herencia es porque o vino {, entonces aca no hace nada, o vino algo mal
        // entonces verifico con los primeros
        if (esPrimeroHerencia(token.getTipo())){
            herencia();
        }
    }

    // ListaAtributos -> Atributo ListaAtributos | lambda
    private void listaAtributos() throws ErrorSintactico{
        String tipo = token.getTipo();
        // si lo que viene no esta en los primeros de Atributo es porque listaAtributos es lambda entonces aca no hace nada
        // es recursiva, por lo tanto siempre que venga alguno de los primeros de A vuelvo a entrar
        // como puede no tener prPub, tambien puedo ir directamente a Tipo
        while (tipo.equals("prPub") | tipo.equals("tStr") | tipo.equals("tBool") | tipo.equals("tInt") | tipo.equals("idClass") | tipo.equals("tArray")){
            atributo();
            // actualizo el tipo
            tipo = token.getTipo();
        }
    }

    // Impl -> impl idClass { ListaMiembros }
    private void impl() throws ErrorSintactico{
        match("prImpl");
        match("idClass");
        match("llaveAbre");
        listaMiembros();
        match("llaveCierra");
    }

    // ListaMiembros -> Miembro ListaMiembros | lambda
    private void listaMiembros() throws ErrorSintactico{
        // si lo que viene esta en los primeros de miembro es porque lista miembro no es lambda
        // Prim(E) = { st, . , lambda}
        while (esPrimeroMiembro(token.getTipo()) | token.getTipo().equals("prFn")){
            miembro();
        }
    }

    // Herencia -> Tipo
    private void herencia() throws ErrorSintactico{
        tipo();
    }

    // Miembro -> Metodo | Constructor
    private void miembro() throws ErrorSintactico{
        // tengo dos opciones o voy a metodo o voy a constructor
        // cuando no tengo st puede venir fn
        if (token.getTipo().equals("prSt") | token.getTipo().equals("prFn")){
            metodo();
        }
        else {
            System.out.println("voy al constructor");
            constructor();
        }
    }

    // Metodo -> FormaMetodoOpt fn TipoMetodoOpt idMetAt ArgumentosFormales BloqueMetodo
    private void metodo() throws ErrorSintactico{
        formaMetodoOpt();
        match("prFn");
        tipoMetodoOpt();
        match("idMetVar");
        argumentosFormales();
        bloqueMetodo();
    }

    // formaMetodoOpt -> formaMetodo | lambda
    private void formaMetodoOpt() throws ErrorSintactico{
        // si el token que viene esta en los primeros de formaMetodo tengo que entrar
        // si viene otra cosa no hace nada y si no viene nada no entra y es valido
        if (token.getTipo().equals("prSt")){
            formaMetodo();
        }
    }

    // TipoMetodoOpt -> TipoMetodo | lambda
    private void tipoMetodoOpt() throws ErrorSintactico{
        // si el tokoen esta en los primeros de tipoMetodo entro
        if (esPrimeroTipoMetodo(token.getTipo())){
            tipoMetodo();
        }
    }

    // ArgumentosFormales -> ( ListaArgumentosFormalesOpt )
    private void argumentosFormales() throws ErrorSintactico{
        match("parAbre");
        listaArgumentosFormalesOpt();
        match("parCierra");
    }

    // Constructor -> . ArgumentosFormales BloqueMetodo
    private void constructor() throws ErrorSintactico{
        match("pto");
        argumentosFormales();
        bloqueMetodo();
    }

    // Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar ;
    private void atributo() throws ErrorSintactico{
        visibilidadOpt();
        tipo();
        listaDeclaracionVar();
        match("ptoComa");
    }

    // VisibilidadOpt -> Visibilidad | lambda
    private void visibilidadOpt() throws ErrorSintactico{
        // si lo que viene esta en los primeros de visibilidad entro
        if (token.getTipo().equals("prPub")){
            visibilidad();
        }
    }

    // Tipo -> TipoPrimitivo | TipoReferencia | TipoArreglo
    private void tipo() throws ErrorSintactico{
        // si lo que viene esta en los primeros de tipo primitivo entro ahi
        if (esPrimeroTipoPrimitivo(token.getTipo())){
            tipoPrimitivo();
        }
        else {
            if (token.getTipo().equals("idClass")){
                tipoReferencia();
            }
            else {
                if (token.getTipo().equals("tArray")){
                    tipoArreglo();
                }
            }
        }
    }

    // ListaDeclaracionVar -> idMetAt ListaDeclaracionesVarRec
    private void listaDeclaracionVar() throws ErrorSintactico{
        match("idMetVar");
        listaDeclaracionVarRec();
    }

    // ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
    private void listaDeclaracionVarRec() throws ErrorSintactico{
        if (token.getTipo().equals("coma")){
            match("coma");
            listaDeclaracionVar();
        }
    }

    // BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
    private void bloqueMetodo() throws ErrorSintactico{
        match("llaveAbre");
        listaDeclaracionVarLocal();
        listaSentencia();
        match("llaveCierra");
    }

    // ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal | lambda
    private void listaDeclaracionVarLocal() throws ErrorSintactico{
        // recursiva
        // si lo que viene esta en los primeros de declaracionVarLocal es porque no es lambda
        while (esPrimeroDeclaracionVarLocal(token.getTipo())){
            declaracionVarLocal();
        }
    }

    // ListaSentencia -> Sentencia ListaSentencia | lambda
    private void listaSentencia() throws ErrorSintactico{
        // mientras este en los primeros de sentencia vuelvo a entrar
        while (esPrimeroSentencia(token.getTipo())){
            sentencia();
        }
    }

    // Visibilidad -> pub
    private void visibilidad() throws ErrorSintactico{
        match("prPub");
    }

    // FormaMetodo -> st
    private void formaMetodo() throws ErrorSintactico{
        match("prSt");
    }

    // TipoPrimitivo -> Str | Bool | Int
    private void tipoPrimitivo() throws ErrorSintactico{
        switch (token.getTipo()){
            case "tStr":
                match("tStr");
                break;
            case "tBool":
                match("tBool");
                break;
            case "tInt":
                match("tInt");
                break;
        }
    }
    // TipoReferencia -> idClass
    private void tipoReferencia() throws ErrorSintactico{
        match("idClass");
    }
    // TipoArray -> Array TipoPrimitivo
    private void tipoArreglo() throws ErrorSintactico{
        match("tArray");
        tipoPrimitivo();
    }

    // DeclaracionVarLocal -> Tipo ListaDeclaracionVar ;
    private void declaracionVarLocal() throws ErrorSintactico{
        tipo();
        listaDeclaracionVar();
        match("ptoComa");
    }

    // ListaArgumentosFormalesOpt -> ListaArgumentosFormales | lambda
    private void listaArgumentosFormalesOpt() throws ErrorSintactico{
        // Prim(ListaArgumentosFormales) = {str, Bool, Int, idClass, Array}
        String tipo = token.getTipo();
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
            listaArgumentosFormales();
        }
    }

    // ListaArgumentosFormales -> ArgumentoFormal ListaArgumentosFormalesRec
    private void listaArgumentosFormales() throws ErrorSintactico{
        argumentoFormal();
        listaArgumentosFormalesRec();
    }

    // ListaArgumentosFormalesRec -> , ListaArgumentosFormales | lambda
    private void listaArgumentosFormalesRec() throws ErrorSintactico{
        if (token.getTipo().equals("coma")){
            match("coma");
            listaArgumentosFormales();
        }
    }

    // ArgumentoFormal -> Tipo idMetAt
    private void argumentoFormal() throws ErrorSintactico{
        tipo();
        match("idMetVar");
    }
     // TipoMetodo -> Tipo void
    private void tipoMetodo() throws ErrorSintactico{
        tipo();
        match("prVoid");
    }

    // Sentencia -> ; | Asignacion | SentenciaSimple ; | if ( Expresion ) SentenciaRec | while ( Expresion ) Sentencia |
    // for ( TipoPrimitivo idMetAt in idMetAt) Sentencia | Bloque | ret ExpresionOpt
    private void sentencia() throws ErrorSintactico{
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
                    expresion();
                    match("parCierra");
                    sentenciaRec();
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
    private void sentenciaRec() throws ErrorSintactico{
        sentencia();
        recursivoElse();
    }

    // RecursivoElse -> else Sentencia | lambda
    private void recursivoElse() throws ErrorSintactico{
        match("prElse");
        sentencia();
    }

    // ExpresionOpt -> Expresion | lambda
    private void expresionOpt() throws ErrorSintactico{
        expresion();
    }

    // SentenciaSimple -> ( Expresion )
    private void sentenciaSimple() throws ErrorSintactico{
        match("parAbre");
        expresion();
        match("parCierra");
    }

    //Expresion -> ExpresionOr
    private void expresion() throws ErrorSintactico{
        expresionOr();
    }

    //BLoque -> { ListaSentencia }
    private void bloque() throws ErrorSintactico{
        match("llaveAbre");
        listaSentencia();
        match("llaveCierra");
    }

    //Asignacion -> AccesoVarSimple = Expresion | AccesoSelfSimple = Expresion
    private void asignacion() throws ErrorSintactico{
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
    private void accesoVarSimple() throws ErrorSintactico{
        match("idMetVar");
        accesoVarSimpleRec();
    }

    // AccesoVarSimpleRec -> ListaEncadenadoSImple | [ Expresion ]
    private void accesoVarSimpleRec() throws ErrorSintactico{
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
    private void listaEncadenadoSimple() throws ErrorSintactico{
        // es recursiva por lo tanto cada vez que viene un primero de encadenado simple vuelvo a entrar
        // Prim(EncadenadoSimple) = {.}
        while (token.getTipo().equals("pto")){
            encadenadoSimple();
            listaEncadenadoSimple();
        }
    }

    // AccesoSelfSimple -> self ListaEncadenadoSimple
    private void accesoSelfSimple() throws ErrorSintactico{
        match("prSelf");
        listaEncadenadoSimple();
    }

    // EncadenadoSimple -> . id
    private void encadenadoSimple() throws ErrorSintactico{
        match("pto");
        match("idMetVar");
    }

    // ExpresionOr -> ExpresionAnd ExpresionOrRec
    private void expresionOr() throws ErrorSintactico{
        expresionAnd();
        expresionOrRec();
    }

    // ExpresionOrRec -> || ExpresionAnd ExpresionOrRec | lambda
    private void expresionOrRec() throws ErrorSintactico{
        while (token.getTipo().equals("opOr")){
            match("opOr");
            expresionAnd();
            expresionOrRec();
        }
    }

    // ExpresionAnd -> ExpIgual ExpAndRec
    private void expresionAnd() throws ErrorSintactico{
        expresionIgual();
        expresionAndRec();
    }

    //ExpresionAndRec -> && ExpIgual ExpresionAndRec | lamnda
    private void expresionAndRec() throws ErrorSintactico{
        while (token.getTipo().equals("opAndLog")){
            match("opAndLog");
            expresionIgual();
            expresionAndRec();
        }
    }

    // ExpresionIgual -> ExpresionComp ExpresionIgualRec
    private void expresionIgual() throws ErrorSintactico{
        expresionComp();
        expresionigualRec();
    }

    // ExpresionIgualRec -> OpIgual ExpresionComp ExpresionIgualRec | lambda
    private void expresionigualRec() throws ErrorSintactico{
        // voy a repetir siempre que vengan los primros de opIgual
        // Prim(OpIgual) = { == , != }
        while (token.getTipo().equals("opIgualIgual") | token.getTipo().equals("opDiferente")){
            opIgual();
            expresionComp();
            expresionigualRec();
        }
    }

    // ExpresionComp -> ExpresionAd ExpresionCompRec
    private void expresionComp() throws ErrorSintactico{
        expresionAd();
        expresionCompRec();
    }

    // ExpresionCompRec -> OpComp ExpresionAd | lambda
    private void expresionCompRec() throws ErrorSintactico{
        // deben venir los primeros de opComp
        // Prim(OpComp) = {<, >, <=, >=}
        if (token.getTipo().equals("opMenor") | token.getTipo().equals("opMenorIgual")  | token.getTipo().equals("opMayor")
                | token.getTipo().equals("opMayorIgual")){
            opComp();
            expresionAd();
        }
    }

    // ExpresionMul -> ExpresionUnario ExpresionMulRec
    private void expresionMul() throws ErrorSintactico{
        expresionUnario();
        expresionMulRec();
    }

    // ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
    private void expresionMulRec() throws ErrorSintactico{
        // simpre que venga un opMul hago recursividad
        while (token.getTipo().equals("opPor") | token.getTipo().equals("opdiv")){
            opMul();
            expresionUnario();
            expresionMulRec();
        }
    }

    // ExpresionUnario -> OpUnario ExpresionUnario | Operando
    private void expresionUnario() throws ErrorSintactico{
        // siempre que venga un opUnario vuelvo
        if (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos")){
            while (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos") |
                    token.getTipo().equals("opNot") | token.getTipo().equals("parAbre")){
                opUnario();
                expresionUnario();
            }
        }
        else { // si no es opMas ni opMenos es un operando
            operando();
        }
    }

    // ExpresionAd -> ExpresionMul ExpresionAdRec
    private void expresionAd() throws ErrorSintactico{
        expresionMul();
        expresionAdRec();
    }

    // ExpresionAdRec -> OpAd ExpresionMul ExpresionAdRec | lambda
    private void expresionAdRec() throws ErrorSintactico{
        // es recursiva cada vez que venga un opAd vuelvo a entrar
        // Prim(OpAd) = {+ , -}
        while (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos")){
            opAd();
            expresionMul();
            expresionAdRec();
        }
    }

    // OpIgual -> == | !=
    private void opIgual() throws ErrorSintactico{
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
    private void opComp() throws ErrorSintactico{
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
                break;
            case "opMenorIgual":{
                match("opMenorIgual");
                break;
            }
        }
    }

    // opAd -> + | -
    private void opAd() throws ErrorSintactico{
        if (token.getTipo().equals("opMas")) {
            match("opMas");
        }
        else {
            if (token.getTipo().equals("opMenos")){
                match("opMenos");
            }
        }
    }

    // opUnario -> + | - | ++ | -- | (Int)
    private void opUnario() throws ErrorSintactico{
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
            case "parAbre":
                match("parAbre");
                match("tInt");
                match("parCierra");
                break;
        }
    }

    // OpMul -> * | /
    private void opMul() throws ErrorSintactico{
        if (token.getTipo().equals("opPor")) {
            match("opPor");
        }
        else {
            if (token.getTipo().equals("opdiv")){
                match("opdiv");
            }
        }
    }

    // Operando -> Literal | Primario | EncadenadoOpt
    private void operando() throws ErrorSintactico{
        String tipo = token.getTipo();
        // si viene un literal
        switch (tipo){
            // Prim(Literal) = {nil, true, false, intLiteral, strLiteral}
            case "prNil" , "prTrue", "prFalse", "literal_entero", "literal_cadena":
                literal();
                break;
            // Prim(Primario) = { (, self, id, idclass, new}
            case "parAbre", "prSelf", "idMetVar", "idClass", "prNew":
                primario();
            // Prim(EncadenadoOpt) = { . , lambda}
            case "pto":
                encadenadoOpt();
        }
    }

    // EncadenadoOpt -> Encadenado | lambda
    private void encadenadoOpt() throws ErrorSintactico{
        // si es pto va a encadendo, Prim(Encadenado) = { . }
        if (token.getTipo().equals("pto")){
            encadenado();
        }
    }

    // Literal -> nil | true | false | intLiteral | strLiteral
    private void literal() throws ErrorSintactico{
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
    private void primario() throws ErrorSintactico{
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
    private void expresionParentizada() throws ErrorSintactico{
        match("parAbre");
        expresion();
        match("parCierra");
        encadenadoOpt();
    }

    // AccesoSelf -> self EncadenadoOpt
    private void accesoSelf() throws ErrorSintactico{
        match("prSelf");
        encadenadoOpt();
    }

    // AccesoVar -> id AccesoVarRec
    private void accesoVar() throws ErrorSintactico{
        match("idMetVar");
        accesoVarRec();
    }

    //AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    private void accesoVarRec() throws ErrorSintactico{
        if (token.getTipo().equals("corcheteAbre")){
            match("corcheteAbre");
            expresion();
            match("corcheteCierra");
            encadenadoOpt();
        }
        else {
            encadenadoOpt();
        }
    }

    // LlamadaMetdo -> id ArgumentosActuales EncadenadoOpt
    private void llamadaMetodo() throws ErrorSintactico{
        match("idMetVar");
        argumentosActuales();
        encadenadoOpt();
    }

    // LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
    private void llamadaMetodoEstatico() throws ErrorSintactico {
        match("idClass");
        match("pto");
        llamadaMetodo();
        encadenadoOpt();
    }

    // LlamadaConClassor -> new LLamadaConClassOrRec
    private void llamadaConClassor() throws ErrorSintactico{
        match("prNew");
        llamadaConClassorRec();
    }

    // LlamadaConClassorRec -> idClass ArgumentosActuales EncadenadoOpt | TipoPrimitivo [ Expresion ]
    private void llamadaConClassorRec() throws ErrorSintactico{

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
    private void argumentosActuales() throws ErrorSintactico{
        match("parAbre");
        listaExpresionesOpt();
        match("parCierra");
    }

    // ListaExpresionesOpt -> ListaExpresiones | lambda
    private void listaExpresionesOpt() throws ErrorSintactico{
        // Prim(ListaExpresiones) = Prim(Expresion)
        if (esPrimeroExpresion(token.getTipo())){
            listaExpresiones();
        }
    }

    // ListaExpresiones -> Expresion ListaExpresionesRec
    private void listaExpresiones() throws ErrorSintactico{
        expresion();
        listaExpresionesRec();
    }

    // ListaExpresionesRec -> , ListaExpresiones | lambda
    private void listaExpresionesRec() throws ErrorSintactico{
        if (token.getTipo().equals("coma")){
            match("coma");
            listaExpresiones();
        }
    }

    // Encadenado -> . EncadenadoRec
    private void encadenado() throws ErrorSintactico{
        match("pto");
        encadenadoRec();
    }

    // EncadenadoRec -> LlamadaMetodo | AccesVar
    private void encadenadoRec() throws ErrorSintactico{
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

    // conjunto de primeros expresion
    private boolean esPrimeroExpresion(String tipo){
        // Prim(Expresion) = {+, -, !, ++, --, (, self, id, idclass, new, nil, true, false, intLiteral, strliteral, . ,lambda}
        if (tipo == "opMas" | tipo == "opMenos"| tipo == "opNot" | tipo == "opMasMAs" | tipo == "opMenosMenos" |
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
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
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
    void match(String tipoEsperado) throws ErrorSintactico{
        if (token.getTipo().equals(tipoEsperado)){
            System.out.println("Esperado: " + tipoEsperado +
                    " | Actual: " + token.getTipo());
            // solo avanzo si matcheo, en ninguna otra parte del codigo deberia avanzar
            System.out.println("Hice match de: "+token.getTipo());
            nextToken();
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba "+tipoEsperado+" y se enontro "+token.getTipo());
        }

    }

    // funcion para pedir el next token cuando matcheo
    private void nextToken(){
        puntero += 1;
        // verifico que no consumi todos los tokens
        if (puntero < listaTokens.size()){
            token = listaTokens.get(puntero);
        }
    }

    // funcion solo para ver el siguiente, sin avanzar (lookahead)
    private Token lookAhead(){
        if (puntero < listaTokens.size()){
            return listaTokens.get(puntero + 1);
        }
        return null;
    }


}