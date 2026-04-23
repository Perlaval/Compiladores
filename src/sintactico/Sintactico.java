package sintactico;
import java.util.List;
import java.util.ArrayList;

import lexico.ErrorLexico;
import lexico.Token;
import lexico.Lexico;


// analizador sintactico
public class Sintactico {
    //private List<Token> listaTokens; //Lista de tokens que obtuve del lexico
    private Lexico lexico;
    private int puntero;
    private Token token;
    private Token next;
    private boolean lookahead = false;

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
    public void analizador() throws ErrorSintactico, ErrorLexico {
        this.token = lexico.analizador();
        // Program -> ListaDefiniciones Start
        program();
        // si sale de program es porque hizo match con $ entonces devolver Exito!


    }

    // Gramatica ----------------------------------------------------------------------------------------------

    //Program -> ListaDefiniciones Start
    private void program() throws ErrorSintactico, ErrorLexico {
        listaDefiniciones();
        // si es lambda va directo a start
        start();
        System.out.println("token final: "+ token.getTipo());
        match("EOF"); // ver si tiene que ser $ o EOF
    }

    // Start -> start BloqueMetodo
    private void start() throws ErrorSintactico, ErrorLexico {
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
    private void listaDefiniciones() throws ErrorSintactico, ErrorLexico {
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
    private void clase() throws ErrorSintactico, ErrorLexico {
        match("prClass");
        match("idClass"); //guardarTS(lexema);
        herenciaOpt(); //metodo creado a partir del ?, ya que puede o no estar
        match("llaveAbre");
        listaAtributos(); // si lo que viene es } es porque era lambda
        match("llaveCierra");

    }

    // HerenciaOpt -> Herencia | lambda
    // aca como puede ser opcional si va a herencia o no, necesito los primeros y siguientes
    private void herenciaOpt() throws ErrorSintactico, ErrorLexico {
        // Sig(HerenciaOpt) = { { }
        // si el token que viene no esta en los primeros de herencia es porque o vino {, entonces aca no hace nada, o vino algo mal
        // entonces verifico con los primeros
        if (esPrimeroHerencia(token.getTipo())){
            herencia();
        }
    }

    // ListaAtributos -> Atributo ListaAtributos | lambda
    private void listaAtributos() throws ErrorSintactico, ErrorLexico {
        String tipo = token.getTipo();
        // si lo que viene no esta en los primeros de Atributo es porque listaAtributos es lambda entonces aca no hace nada
        // es recursiva, por lo tanto siempre que venga alguno de los primeros de A vuelvo a entrar
        // como puede no tener prPub, tambien puedo ir directamente a Tipo
        if (tipo.equals("prPub") | tipo.equals("tStr") | tipo.equals("tBool") | tipo.equals("tInt") | tipo.equals("idClass") | tipo.equals("tArray")){
            atributo();
            // actualizo el tipo
            tipo = token.getTipo();
            listaAtributos();
        }
    }

    // Impl -> impl idClass { ListaMiembros }
    private void impl() throws ErrorSintactico, ErrorLexico {
        match("prImpl");
        match("idClass");
        match("llaveAbre");
        listaMiembros();
        match("llaveCierra");
    }

    // ListaMiembros -> Miembro ListaMiembros | lambda
    private void listaMiembros() throws ErrorSintactico, ErrorLexico {
        // si lo que viene esta en los primeros de miembro es porque lista miembro no es lambda
        // Prim(E) = { st, . , lambda}
        if (esPrimeroMiembro(token.getTipo()) | token.getTipo().equals("prFn")){
            miembro();
            listaMiembros();
        }
    }

    // Herencia -> Tipo
    private void herencia() throws ErrorSintactico, ErrorLexico {
        tipo();
    }

    // Miembro -> Metodo | Constructor
    private void miembro() throws ErrorSintactico, ErrorLexico {
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
    private void metodo() throws ErrorSintactico, ErrorLexico {
        formaMetodoOpt();
        match("prFn");
        tipoMetodoOpt();
        match("idMetVar");
        argumentosFormales();
        bloqueMetodo();
    }

    // formaMetodoOpt -> formaMetodo | lambda
    private void formaMetodoOpt() throws ErrorSintactico, ErrorLexico {
        // si el token que viene esta en los primeros de formaMetodo tengo que entrar
        // si viene otra cosa no hace nada y si no viene nada no entra y es valido
        if (token.getTipo().equals("prSt")){
            formaMetodo();
        }
    }

    // TipoMetodoOpt -> TipoMetodo | lambda
    private void tipoMetodoOpt() throws ErrorSintactico, ErrorLexico {
        // si el tokoen esta en los primeros de tipoMetodo entro
        if (esPrimeroTipoMetodo(token.getTipo())){
            tipoMetodo();
        }
    }

    // ArgumentosFormales -> ( ListaArgumentosFormalesOpt )
    private void argumentosFormales() throws ErrorSintactico, ErrorLexico {
        match("parAbre");
        listaArgumentosFormalesOpt();
        match("parCierra");
    }

    // Constructor -> . ArgumentosFormales BloqueMetodo
    private void constructor() throws ErrorSintactico, ErrorLexico {
        match("pto");
        argumentosFormales();
        bloqueMetodo();
    }

    // Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar ;
    private void atributo() throws ErrorSintactico, ErrorLexico {
        visibilidadOpt();
        tipo();
        listaDeclaracionVar();
        match("ptoComa");
    }

    // VisibilidadOpt -> Visibilidad | lambda
    private void visibilidadOpt() throws ErrorSintactico, ErrorLexico {
        // si lo que viene esta en los primeros de visibilidad entro
        if (token.getTipo().equals("prPub")){
            visibilidad();
        }
    }

    // Tipo -> TipoPrimitivo | TipoReferencia | TipoArreglo
    private void tipo() throws ErrorSintactico, ErrorLexico {
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
    private void listaDeclaracionVar() throws ErrorSintactico, ErrorLexico {
        match("idMetVar");
        listaDeclaracionVarRec();
    }

    // ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
    private void listaDeclaracionVarRec() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("coma")){
            match("coma");
            listaDeclaracionVar();
        }
    }

    // BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
    private void bloqueMetodo() throws ErrorSintactico, ErrorLexico {
        match("llaveAbre");
        listaDeclaracionVarLocal();
        listaSentencia();
        match("llaveCierra");
        System.out.println("deberia no ver llave cierra, y veo: "+token.getTipo());
    }

    // ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal | lambda
    private void listaDeclaracionVarLocal() throws ErrorSintactico, ErrorLexico {
        // recursiva
        // si lo que viene esta en los primeros de declaracionVarLocal es porque no es lambda
        if (esPrimeroDeclaracionVarLocal(token.getTipo())){
            declaracionVarLocal();
            listaDeclaracionVarLocal();
        }
    }

    // ListaSentencia -> Sentencia ListaSentencia | lambda
    private void listaSentencia() throws ErrorSintactico, ErrorLexico {
        // mientras este en los primeros de sentencia vuelvo a entrar
        if (esPrimeroSentencia(token.getTipo())){
            sentencia();
            listaSentencia();
        }
    }

    // Visibilidad -> pub
    private void visibilidad() throws ErrorSintactico, ErrorLexico {
        match("prPub");
    }

    // FormaMetodo -> st
    private void formaMetodo() throws ErrorSintactico, ErrorLexico {
        match("prSt");
    }

    // TipoPrimitivo -> Str | Bool | Int
    private void tipoPrimitivo() throws ErrorSintactico, ErrorLexico {
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
    private void tipoReferencia() throws ErrorSintactico, ErrorLexico {
        match("idClass");
    }
    // TipoArray -> Array TipoPrimitivo
    private void tipoArreglo() throws ErrorSintactico, ErrorLexico {
        match("prArray");
        tipoPrimitivo();
    }

    // DeclaracionVarLocal -> Tipo ListaDeclaracionVar ;
    private void declaracionVarLocal() throws ErrorSintactico, ErrorLexico {
        tipo();
        listaDeclaracionVar();
        match("ptoComa");
    }

    // ListaArgumentosFormalesOpt -> ListaArgumentosFormales | lambda
    private void listaArgumentosFormalesOpt() throws ErrorSintactico, ErrorLexico {
        // Prim(ListaArgumentosFormales) = {str, Bool, Int, idClass, Array}
        String tipo = token.getTipo();
        if (tipo == "tStr" | tipo == "tBool" | tipo == "tInt" | tipo == "idClass" | tipo == "tArray"){
            listaArgumentosFormales();
        }
    }

    // ListaArgumentosFormales -> ArgumentoFormal ListaArgumentosFormalesRec
    private void listaArgumentosFormales() throws ErrorSintactico, ErrorLexico {
        argumentoFormal();
        listaArgumentosFormalesRec();
    }

    // ListaArgumentosFormalesRec -> , ListaArgumentosFormales | lambda
    private void listaArgumentosFormalesRec() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("coma")){
            match("coma");
            listaArgumentosFormales();
        }
    }

    // ArgumentoFormal -> Tipo idMetAt
    private void argumentoFormal() throws ErrorSintactico, ErrorLexico {
        tipo();
        match("idMetVar");
    }
     // TipoMetodo -> Tipo | void
    // Prim(Tipo)= {str, bool int, idClass, Array}
    private void tipoMetodo() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("tStr") || token.getTipo().equals("tBool") || token.getTipo().equals("tInt") ||
                token.getTipo().equals("idClass") || token.getTipo().equals("tArray")) {
            tipo();
        }
        else {
            match("prVoid");
        }

    }

    // Sentencia -> ; | Asignacion | SentenciaSimple ; | if ( Expresion ) SentenciaRec | while ( Expresion ) Sentencia |
    // for ( TipoPrimitivo idMetAt in idMetAt) Sentencia | Bloque | ret ExpresionOpt
    private void sentencia() throws ErrorSintactico, ErrorLexico {
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
                    expresion(); //devuelvo la condicion
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
    private void sentenciaRec() throws ErrorSintactico, ErrorLexico {
        sentencia();
        recursivoElse();
    }

    // RecursivoElse -> else Sentencia | lambda
    private void recursivoElse() throws ErrorSintactico, ErrorLexico {
        match("prElse");
        sentencia();
    }

    // ExpresionOpt -> Expresion | lambda
    private void expresionOpt() throws ErrorSintactico, ErrorLexico {
        expresion();
    }

    // SentenciaSimple -> ( Expresion )
    private void sentenciaSimple() throws ErrorSintactico, ErrorLexico {
        match("parAbre");
        expresion();
        match("parCierra");
    }

    //Expresion -> ExpresionOr
    private void expresion() throws ErrorSintactico, ErrorLexico {
        expresionOr();
    }

    //BLoque -> { ListaSentencia }
    private void bloque() throws ErrorSintactico, ErrorLexico {
        match("llaveAbre");
        listaSentencia();
        match("llaveCierra");
    }

    //Asignacion -> AccesoVarSimple = Expresion | AccesoSelfSimple = Expresion
    private void asignacion() throws ErrorSintactico, ErrorLexico {
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
    private void accesoVarSimple() throws ErrorSintactico, ErrorLexico {
        match("idMetVar");
        accesoVarSimpleRec();
    }

    // AccesoVarSimpleRec -> ListaEncadenadoSImple | [ Expresion ]
    private void accesoVarSimpleRec() throws ErrorSintactico, ErrorLexico {
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
    }

    // AccesoSelfSimple -> self ListaEncadenadoSimple
    private void accesoSelfSimple() throws ErrorSintactico, ErrorLexico {
        match("prSelf");
        listaEncadenadoSimple();
    }

    // EncadenadoSimple -> . id
    private void encadenadoSimple() throws ErrorSintactico, ErrorLexico {
        match("pto");
        match("idMetVar");
    }

    // ExpresionOr -> ExpresionAnd ExpresionOrRec
    private void expresionOr() throws ErrorSintactico, ErrorLexico {
        expresionAnd();
        expresionOrRec();
    }

    // ExpresionOrRec -> || ExpresionAnd ExpresionOrRec | lambda
    private void expresionOrRec() throws ErrorSintactico, ErrorLexico {
        while (token.getTipo().equals("opOr")){
            match("opOr");
            expresionAnd();
            expresionOrRec();
        }
    }

    // ExpresionAnd -> ExpIgual ExpAndRec
    private void expresionAnd() throws ErrorSintactico, ErrorLexico {
        expresionIgual();
        expresionAndRec();
    }

    //ExpresionAndRec -> && ExpIgual ExpresionAndRec | lamnda
    private void expresionAndRec() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("opAndLog")){
            match("opAndLog");
            expresionIgual();
            expresionAndRec();
        }
    }

    // ExpresionIgual -> ExpresionComp ExpresionIgualRec
    private void expresionIgual() throws ErrorSintactico, ErrorLexico {
        expresionComp();
        expresionigualRec();
    }

    // ExpresionIgualRec -> OpIgual ExpresionComp ExpresionIgualRec | lambda
    private void expresionigualRec() throws ErrorSintactico, ErrorLexico {
        // voy a repetir siempre que vengan los primros de opIgual
        // Prim(OpIgual) = { == , != }
        if (token.getTipo().equals("opIgualIgual") | token.getTipo().equals("opDiferente")){
            opIgual();
            expresionComp();
            expresionigualRec();
        }
    }

    // ExpresionComp -> ExpresionAd ExpresionCompRec
    private void expresionComp() throws ErrorSintactico, ErrorLexico {
        expresionAd();
        expresionCompRec();
    }

    // ExpresionCompRec -> OpComp ExpresionAd | lambda
    private void expresionCompRec() throws ErrorSintactico, ErrorLexico {
        // deben venir los primeros de opComp
        // Prim(OpComp) = {<, >, <=, >=}
        if (token.getTipo().equals("opMenor") | token.getTipo().equals("opMenorIgual")  | token.getTipo().equals("opMayor")
                | token.getTipo().equals("opMayorIgual")){

            opComp();
            expresionAd();
        }
    }

    // ExpresionMul -> ExpresionUnario ExpresionMulRec
    private void expresionMul() throws ErrorSintactico, ErrorLexico {
        expresionUnario();
        expresionMulRec();
    }

    // ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
    private void expresionMulRec() throws ErrorSintactico, ErrorLexico {
        // simpre que venga un opMul hago recursividad
        if (token.getTipo().equals("opPor") | token.getTipo().equals("opdiv")){
            opMul();
            expresionUnario();
            expresionMulRec();
        }
    }

    // ExpresionUnario -> OpUnario ExpresionUnario | Operando
    private void expresionUnario() throws ErrorSintactico, ErrorLexico {
        // siempre que venga un opUnario vuelvo
        if (token.getTipo().equals("opMas") | token.getTipo().equals("opMenos") |
                token.getTipo().equals("opMasMas") | token.getTipo().equals("opMenosMenos")){
            opUnario();
            expresionUnario();
        }
        else { // si no es opMas ni opMenos es un operando
            operando();
        }
    }

    // ExpresionAd -> ExpresionMul ExpresionAdRec
    private void expresionAd() throws ErrorSintactico, ErrorLexico {
        expresionMul();
        expresionAdRec();
    }

    // ExpresionAdRec -> OpAd ExpresionMul ExpresionAdRec | lambda
    private void expresionAdRec() throws ErrorSintactico, ErrorLexico {
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

    // opUnario -> + | - | ++ | --
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

    // Operando -> Literal | Primario | EncadenadoOpt
    private void operando() throws ErrorSintactico, ErrorLexico {
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
    private void encadenadoOpt() throws ErrorSintactico, ErrorLexico {
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
    private void primario() throws ErrorSintactico, ErrorLexico {
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
    private void expresionParentizada() throws ErrorSintactico, ErrorLexico {
        match("parAbre");
        expresion();
        match("parCierra");
        encadenadoOpt();
    }

    // AccesoSelf -> self EncadenadoOpt
    private void accesoSelf() throws ErrorSintactico, ErrorLexico {
        match("prSelf");
        encadenadoOpt();
    }

    // AccesoVar -> id AccesoVarRec
    private void accesoVar() throws ErrorSintactico, ErrorLexico {
        match("idMetVar");
        accesoVarRec();
    }

    //AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    private void accesoVarRec() throws ErrorSintactico, ErrorLexico {
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
    private void llamadaMetodo() throws ErrorSintactico, ErrorLexico {
        match("idMetVar");
        argumentosActuales();
        encadenadoOpt();
    }

    // LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
    private void llamadaMetodoEstatico() throws ErrorSintactico, ErrorLexico {
        match("idClass");
        match("pto");
        llamadaMetodo();
        encadenadoOpt();
    }

    // LlamadaConClassor -> new LLamadaConClassOrRec
    private void llamadaConClassor() throws ErrorSintactico, ErrorLexico {
        match("prNew");
        llamadaConClassorRec();
    }

    // LlamadaConClassorRec -> idClass ArgumentosActuales EncadenadoOpt | TipoPrimitivo [ Expresion ]
    private void llamadaConClassorRec() throws ErrorSintactico, ErrorLexico {

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
    private void argumentosActuales() throws ErrorSintactico, ErrorLexico {
        match("parAbre");
        listaExpresionesOpt();
        match("parCierra");
    }

    // ListaExpresionesOpt -> ListaExpresiones | lambda
    private void listaExpresionesOpt() throws ErrorSintactico, ErrorLexico {
        // Prim(ListaExpresiones) = Prim(Expresion)
        if (esPrimeroExpresion(token.getTipo())){
            listaExpresiones();
        }
    }

    // ListaExpresiones -> Expresion ListaExpresionesRec
    private void listaExpresiones() throws ErrorSintactico, ErrorLexico {
        expresion();
        listaExpresionesRec();
    }

    // ListaExpresionesRec -> , ListaExpresiones | lambda
    private void listaExpresionesRec() throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals("coma")){
            match("coma");
            listaExpresiones();
        }
    }

    // Encadenado -> . fec
    private void encadenado() throws ErrorSintactico, ErrorLexico {
        match("pto");
        encadenadoRec();
    }

    // EncadenadoRec -> LlamadaMetodo | AccesVar
    private void encadenadoRec() throws ErrorSintactico, ErrorLexico {
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
    void match(String tipoEsperado) throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals(tipoEsperado)){
            System.out.println("Esperado: " + tipoEsperado +
                    " | Actual: " + token.getTipo());
            // solo avanzo si matcheo, en ninguna otra parte del codigo deberia avanzar
            System.out.println("Hice match de: "+token.getTipo());
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
        // verifico que no consumi todos los tokens
        /*if (puntero < listaTokens.size()){
            token = listaTokens.get(puntero);
        }*/
        if (!lexico.esFinArchivo()){
            token = lexico.analizador();
        }


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