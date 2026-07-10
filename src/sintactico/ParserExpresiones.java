package sintactico;

import lexico.ErrorLexico;
import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.NodoEncadenado;
import semantico.nodos.NodoEncadenadoOpt;
import semantico.nodos.NodoEncadenadoRec;
import semantico.nodos.expresion.*;
import semantico.nodos.sentencia.NodoAccesoSelfSimple;
import semantico.nodos.sentencia.NodoAccesoVarSimple;
import semantico.nodos.sentencia.NodoAccesoVarSimpleRec;
import semantico.nodos.sentencia.NodoVarEncadenado;
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;

import java.util.ArrayList;

public class ParserExpresiones {
    private final Parser parser;

    public ParserExpresiones(Parser parser) {
        this.parser = parser;
    }

    // ExpresionOpt -> Expresion | lambda
    public NodoExpresion expresionOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.esPrimeroExpresion(parser.token())) {
            return expresion();
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION:
    //      - Expresion -> ExpresionOr
    //------------------------------------------------------------------------------------------------------------
    public NodoExpresion expresion() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        //NodoExpresion nodoExpresionOr = expresionOr();
        //return nodoExpresionOr;
        return expresionOr();

    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION OR:
    //      - ExpresionOr -> ExpresionAnd ExpresionOrRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionOr() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionAnd = expresionAnd();
        return expresionOrRec(nodoExpresionAnd);
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION OR RECURSIVO:
    //      - ExpresionOrRec -> || ExpresionAnd ExpresionOrRec | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionOrRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.token().getTipo().equals("opOr")){
            Token operador = parser.token();
            parser.match("opOr");
            NodoExpresion nodoDer = expresionAnd();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq,nodoDer);
            return expresionOrRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION AND:
    //      - ExpresionAnd -> ExpIgual ExpAndRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionAnd() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionIgual = expresionIgual();
        return expresionAndRec(nodoExpresionIgual);
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION AND RECURSIVO:
    //      - ExpresionAndRec -> && ExpIgual ExpresionAndRec | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionAndRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (parser.token().getTipo().equals("opAndLog")){
            Token operador = parser.token();
            parser.match("opAndLog");
            NodoExpresion nodoDer = expresionIgual();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            return expresionAndRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION IGUAL:
    //      - ExpresionIgual -> ExpresionComp ExpresionIgualRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionIgual() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionComp = expresionComp();
        //expresionigualRec();
        return expresionigualRec(nodoExpresionComp);

    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION IGUAL RECURSIVO:
    //      - ExpresionIgualRec -> OpIgual ExpresionComp ExpresionIgualRec | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionigualRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // voy a repetir siempre que vengan los primros de opIgual
        // Prim(OpIgual) = { == , != }
        if (parser.token().getTipo().equals("opIgualIgual") | parser.token().getTipo().equals("opDiferente")){
            Token operador = opIgual();
            NodoExpresion nodoDer = expresionComp();
            //expresionigualRec();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            return expresionigualRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION COMPARACION:
    //      - ExpresionComp -> ExpresionAd ExpresionCompRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionComp() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionAd = expresionAd();
        return expresionCompRec(nodoExpresionAd);
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION COMPARACION REC:
    //      - ExpresionCompRec -> OpComp ExpresionAd | lambda
    //      - Esta funcion no es recursiva
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionCompRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // deben venir los primeros de opComp
        // Prim(OpComp) = {<, >, <=, >=}
        if (Operador.esOpComp(parser.token())){
            Token operador = opComp();
            NodoExpresion nodoDer = expresionAd();

            return new NodoExpresionBin(operador, nodoIzq, nodoDer);
        }
        return nodoIzq;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION AD:
    //      - ExpresionAd -> ExpresionMul ExpresionAdRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionAd() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionMul = expresionMul();
        //expresionAdRec();
        return expresionAdRec(nodoExpresionMul);
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION AD REC:
    //      - ExpresionAdRec -> OpAd ExpresionMul ExpresionAdRec | lambda
    //      - es recursiva cada vez que venga un opAd vuelvo a entrar
    //      - Prim(OpAd) = {+ , -}
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionAdRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (Operador.esOpAd(parser.token())){
            Token operador = opAd();
            NodoExpresion nodoDer = expresionMul();
            //expresionAdRec();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            return expresionAdRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION MUL:
    //      - ExpresionMul -> ExpresionUnario ExpresionMulRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionMul() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresionUnario nodoExpresionUnario = expresionUnario();
        return expresionMulRec(nodoExpresionUnario);
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION MUL REC:
    //      - ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionMulRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // simpre que venga un opMul hago recursividad
        if (Operador.esOpMul(parser.token())){
            Token operador = opMul();
            NodoExpresionUnario nodoDer = expresionUnario();
            NodoExpresionBin nodoIzqRec = new NodoExpresionBin(operador, nodoIzq, nodoDer);
            //expresionMulRec();
            return expresionMulRec(nodoIzqRec);
        }
        return nodoIzq;
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION UNARIO:
    //      - ExpresionUnario -> OpUnario ExpresionUnario | Operando
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresionUnario expresionUnario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // siempre que venga un opUnario vuelvo
        if (Operador.esOpUnario(parser.token())){
            Token operador = opUnario();
            NodoExpresionUnario nodoExpresionUnario = expresionUnario();
            return new NodoExpresionUnario(operador, nodoExpresionUnario);
        } else { // si no es opMas ni opMenos es un operando
            // si lo que viene no esta en los prim de operando no voy
            if (parser.esPrimeroOperando(parser.token())) {
                NodoOperando nodoOperando = operando();
                return new NodoExpresionUnario(nodoOperando);
            } else {
                throw new ErrorSintactico(parser.token().getFila(), parser.token().getColumna(), "Se esperaba un operando y se encontro " + parser.token().getTipo());
            }
            //operando();
        }

    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION PARENTIZADA:
    //      - ExpresionParentizada -> ( Expresion ) EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresionParentizada expresionParentizada() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("parAbre");
        NodoExpresion nodoExpresion = expresion();
        parser.match("parCierra");
        NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();

        return new NodoExpresionParentizada(nodoExpresion, nodoEncadenadoOpt);

    }

    //------------------------------------------------------------------------------------------------------------
    // LISTA EXPRESIONES OPT:
    //      - ListaExpresionesOpt -> ListaExpresiones | lambda
    //------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoExpresion> listaExpresionesOpt(ArrayList<NodoExpresion> listaExpr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // Prim(ListaExpresiones) = Prim(Expresion)
        if (parser.esPrimeroExpresion(parser.token())){
            return listaExpresiones(listaExpr);
            //NodoListaExpresiones nodoListaExpresiones = listaExpresiones();
            //return new NodoListaExpresionesOpt(nodoListaExpresiones);
        }
        //return null;
        return listaExpr; // caso base la lista esta vacia
    }

    //------------------------------------------------------------------------------------------------------------
    // LISTA EXPRESIONES:
    //      - ListaExpresiones -> Expresion ListaExpresionesRec
    //------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoExpresion> listaExpresiones(ArrayList<NodoExpresion> listaExpr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // llego al caso base, entonces armo la lista con las expresiones
        //ArrayList<NodoExpresion> listaExpresiones = new ArrayList<>(); // creo la lista para guardar todas las expresiones que van a llegar hasta argumentos actuales
        //NodoExpresion nodoE = expresion(); //me traigo la primera expresion
        listaExpr.add(expresion()); // agrego a la lista la expresion
        return listaExpresionesRec(listaExpr);

        //ArrayList<NodoExpresion> listaExpRec = listaExpresionesRec();
        //NodoListaExpresionesRec nodoListaExpRec = listaExpresionesRec();
        /*if (listaExpRec.isEmpty()){
            return listaExpresiones; // caso base
        }
        else {
            return listaExpRec;
        }*/
    }

    //------------------------------------------------------------------------------------------------------------
    // LISTA EXPRESIONES REC:
    //      - ListaExpresionesRec -> , ListaExpresiones | lambda
    //------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoExpresion> listaExpresionesRec(ArrayList<NodoExpresion> listaExpr) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.token().getTipo().equals("coma")){
            parser.match("coma");
            //ArrayList<NodoExpresion> listaExpresionesrec = listaExpresiones();
            //NodoListaExpresiones nodoListaExp = listaExpresiones();
            return listaExpresiones(listaExpr);
        }
        return listaExpr;
    }

    //------------------------------------------------------------------------------------------------------------
    //                                          ACCESOS
    //------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR SIMPLE:
    //      - AccesoVarSimple -> id AccesoVarSimpleRec
    //------------------------------------------------------------------------------------------------------------
    public NodoAccesoVarSimple accesoVarSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoVarEncadenado varEncadenado = new NodoVarEncadenado(parser.token().getFila(), parser.token().getColumna(), parser.token().getLexema());
        parser.match("idMetVar");
        NodoVarEncadenado proxEncadenado = null;
        return new NodoAccesoVarSimple(varEncadenado, accesoVarSimpleRec(proxEncadenado));
        //accesoVarSimpleRec puede ser: null | NodoVarEncadenado| nodoExpresion
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR SIMPLE RECURSIVO:
    //      - AccesoVarSimpleRec -> ListaEncadenadoSimple | [ Expresion ]
    //------------------------------------------------------------------------------------------------------------
    private NodoAccesoVarSimpleRec accesoVarSimpleRec(NodoVarEncadenado varEncadenado) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de lista enadenado simple entro ahi
        // Prim(ListaEncadenadoSimple) = {. , lambda}
        if (parser.token().getTipo().equals("pto")) {
            listaEncadenadoSimple(varEncadenado); //en este metodo anido todos los encadenados a el id principal varEncadendo
            return new NodoAccesoVarSimpleRec(varEncadenado);
        } else {
            if (parser.token().getTipo().equals("corcheteAbre")) {
                parser.match("corcheteAbre");
                NodoExpresion nodoExpresion = expresion();
                parser.match("corcheteCierra");
                return new NodoAccesoVarSimpleRec(nodoExpresion);
            }
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO SELF SIMPLE:
    //      - AccesoSelfSimple -> self ListaEncadenadoSimple
    //------------------------------------------------------------------------------------------------------------
    public NodoAccesoSelfSimple accesoSelfSimple() throws ErrorSintactico, ErrorLexico {
        System.out.println("Estoy en AccesoSelfSimple con el metodo actual: " + parser.ts().metodoActual.getNombre());
        NodoVarEncadenado selfEncadenado = new NodoVarEncadenado(parser.token().getFila(), parser.token().getColumna(), parser.token().getLexema());
        parser.match("prSelf");
        NodoVarEncadenado varEncadenado = null; //inicializo el nodo en null
        listaEncadenadoSimple(varEncadenado); //este metodo va agregando los nodos del encadenados
        //Si hay encadenado varEncadenado != nul -> selfEncadenado = self y varEncadenado = id1.id1.id3
        return new NodoAccesoSelfSimple(selfEncadenado, varEncadenado);
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO SELF:
    //      - AccesoSelf -> self EncadenadoOpt
    //      - Si el metood es estatico no puedo acceder a una variable de instancia (self)
    //------------------------------------------------------------------------------------------------------------
    private NodoAccesoSelf accesoSelf() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("prSelf");
        NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
        return new NodoAccesoSelf(nodoEncadenadoOpt);
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR:
    //      - AccesoVar -> id AccesoVarRec
    //------------------------------------------------------------------------------------------------------------
    private NodoAccesoVar accesoVar() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoId nodoId;
        Token tokenActual = parser.token(); // Guardamos el token para el nodo (línea y lexema)
        String lexema = tokenActual.getLexema();
        // 1. CASO BASE: Es el primer ID de la cadena (ej. 'v1' en v1.a.b)
        // En el EDT NO buscamos si existe, solo creamos el nodo con el lexema.
        // La resolución de nombres se hará en la segunda pasada (metodo chequear).
        nodoId = new NodoId(tokenActual.getFila(), tokenActual.getColumna(), lexema);
        parser.match("idMetVar");
        // Obtenemos el resto de la cadena.
        // Pasamos null porque el tipo de 'v1' aún no se conoce (se infiere en la pasada 2).
        NodoAccesoVarRec nodoAccesoVarRec = accesoVarRec();
        return new NodoAccesoVar(nodoId, nodoAccesoVarRec);
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR REC:
    //      - AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoAccesoVarRec accesoVarRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (parser.token().getTipo().equals("corcheteAbre")) {
            parser.match("corcheteAbre");
            // Construimos el nodo de la expresión del índice
            NodoExpresion nodoExpresion = expresion();
            parser.match("corcheteCierra");

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


    //------------------------------------------------------------------------------------------------------------
    //                                          ENCADENADO
    //------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------
    // LISTA ENCADENADO SIMPLE:
    //      - ListaEncadenadoSimple -> EncadenadoSimpple ListaEncadenadoSimple | lambda
    //      - En esta clase se hacen los chequeos de tipos del encadenado en la 2da pasada
    //------------------------------------------------------------------------------------------------------------
    private void listaEncadenadoSimple(NodoVarEncadenado varEncadenado) throws ErrorSintactico, ErrorLexico {
        // es recursiva por lo tanto cada vez que viene un primero de encadenado simple vuelvo a entrar
        // Prim(EncadenadoSimple) = {.}
        if (parser.token().getTipo().equals("pto")) {
            NodoVarEncadenado nuevaVarEnc = encadenadoSimple();
            if (varEncadenado != null) { //si varEncadenado == null entonces recien voy a setear el varEncadeno de id2
                varEncadenado.setProxEncadenado(nuevaVarEnc);
            }
            //Sino ya pase el id2
            //Aqui deberia chequear la correctitud semnatica del encadenado!
            listaEncadenadoSimple(nuevaVarEnc);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO SIMPLE:
    //      - EncadenadoSimple -> . id
    //------------------------------------------------------------------------------------------------------------
    private NodoVarEncadenado encadenadoSimple() throws ErrorSintactico, ErrorLexico {
        parser.match("pto");
        NodoVarEncadenado varEncadenado = new NodoVarEncadenado(parser.token().getFila(), parser.token().getColumna(), parser.token().getLexema());
        parser.match("idMetVar"); //sueldo
        return varEncadenado;
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO:
    //      - Encadenado -> . EncadenadoRec
    //------------------------------------------------------------------------------------------------------------
    private NodoEncadenado encadenado() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("pto");
        NodoEncadenadoRec nodoEncadenadoRec = encadenadoRec();
        return new NodoEncadenado(nodoEncadenadoRec);
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO OPT:
    //      - EncadenadoOpt -> Encadenado | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoEncadenadoOpt encadenadoOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si es pto va a encadendo, Prim(Encadenado) = { . }
        if (parser.token().getTipo().equals("pto")){
            NodoEncadenado nodoEncadenado = encadenado();
            return new NodoEncadenadoOpt(nodoEncadenado);
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO RECURSIVO:
    //      - EncadenadoRec -> LlamadaMetodo | AccesVar
    //------------------------------------------------------------------------------------------------------------
    private NodoEncadenadoRec encadenadoRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // como con ambos me llega id veo el nextToken
        Token next = parser.lookAhead();
        if (next.getTipo().equals("parAbre")){ // es porq esta en llamada metodo
            llamadaMetodo();
        }
        else {
            NodoAccesoVar nodoAccesoVar = accesoVar();
            return new NodoEncadenadoRec(nodoAccesoVar);
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    //                                          OPERADORES
    //------------------------------------------------------------------------------------------------------------

    // OpIgual -> == | !=
    private Token opIgual() throws ErrorSintactico, ErrorLexico {
        return parser.consumirOperador();
    }

    // opComp -> < | > | <= | >=
    private Token opComp() throws ErrorSintactico, ErrorLexico {
        return parser.consumirOperador();
    }

    // opAd -> + | -
    private Token opAd() throws ErrorSintactico, ErrorLexico {
        return parser.consumirOperador();
    }

    // opUnario -> + | - | ++ | -- | !
    private Token opUnario() throws ErrorSintactico, ErrorLexico {
        return parser.consumirOperador();
    }

    // OpMul -> * | /
    private Token opMul() throws ErrorSintactico, ErrorLexico {
        return parser.consumirOperador();
    }

    //------------------------------------------------------------------------------------------------------------
    //                                          OPERANDOS
    //------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------
    // OPERANDO:
    //      - Operando -> Literal | Primario EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoOperando operando() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        //String tipo = token.getTipo();
        if (parser.esLiteral(parser.token())){
            NodoLiteral nodoLiteral = literal();
            return new NodoOperando(nodoLiteral);
        }
        if (parser.esPrimario(parser.token())){
            NodoPrimario nodoPrimario = primario();
            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
            return new NodoOperando(nodoPrimario, nodoEncadenadoOpt);
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    // LITERAL:
    //      - Literal -> nil | true | false | intLiteral | strLiteral
    //------------------------------------------------------------------------------------------------------------
    private NodoLiteral literal() throws ErrorSintactico, ErrorLexico {
        Token t = parser.token();
        switch (t.getTipo()){
            case "prNil":
                parser.match("prNil");
                return new NodoNil(t.getFila(),t.getColumna(),t.getLexema());
            case "prTrue" , "prFalse":
                parser.match(parser.token().getTipo());
                return new NodoBool(t.getFila(),t.getColumna(),t.getLexema());
            case "literal_entero":
                parser.match("literal_entero");
                return new NodoNum(t.getFila(),t.getColumna(),t.getLexema());
            case "literal_cadena":
                parser.match("literal_cadena");
                return new NodoStr(t.getFila(),t.getColumna(),t.getLexema());
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    // PRIMARIO:
    //      - Primario -> ExpresionParentizada | AccesoSelf | AccesoVar | LlamadaMetodo | LlamadaMetodoEstatico | LlamadaConClassor
    //------------------------------------------------------------------------------------------------------------
    private NodoPrimario primario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        String tipo = parser.token().getTipo();
        switch (tipo){
            // Prim(ExpresionParentizada) = { ( }
            case "parAbre":
                NodoExpresionParentizada nodoExpresionParentizada = expresionParentizada();
                return new NodoPrimario(nodoExpresionParentizada);
            // Prim(AccesoSelf) = { self }
            case "prSelf":
                // verifico que no este en un contexto estatico
                //System.out.println("Metodo actual: "+ts.metodoActual.getNombre());
                if (parser.ts().metodoActual.esEstatico){
                    throw new ErrorSemantico(parser.token().getFila(), parser.token().getColumna(), "No se puede acceder a una variable de instancia en un contexto estatico");
                }
                NodoAccesoSelf nodoAccesoSelf = accesoSelf();
                return new NodoPrimario(nodoAccesoSelf);
            // Prim(AccesoVar) = { id } y Prim(LlamadaMetodo) = { id }
            // como ambas van a id veo los siguientes
            case "idMetVar":
                // si me viene un parAbre es porque fue a LlamadaMetodo
                //System.out.println("estoy en primario con: "+token.getTipo());
                Token next = parser.lookAhead();
                if (next.getTipo().equals("parAbre")){
                    //HACER
                    //NodoLlamadaMetodo nodoLLamadaMetodo = llamadaMetodo();
                    //return new NodoPrimario(nodoLlamadaMetodo);
                    return new NodoPrimario(llamadaMetodo());

                }
                else {
                    NodoAccesoVar nodoAccesoVar = accesoVar();
                    return new NodoPrimario(nodoAccesoVar);
                }
                //break;
                // Prim(LlamadaMetodoEstatico) = {idClass}
            case "idClass":
                //HACER
                return new NodoPrimario(llamadaMetodoEstatico());

            // Prim(LlamadaConClassor) = {new}
            case "prNew":
                //HACER
                return new NodoPrimario(llamadaConClassor());

        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    //                                          LLAMADAS
    //------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA METODO:
    //      - LlamadaMetdo -> id ArgumentosActuales EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoLlamadaMetodo llamadaMetodo() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.ts().noEstaMetodoTs(parser.token().getLexema())){
            throw new ErrorSemantico(parser.token().getFila(), parser.token().getColumna(), "El id no fue declarado");
        }
        else {
            RegistroVariable id = parser.ts().getVariable(parser.token().getLexema());
            // creo el nodo id
            NodoId nodoId = new NodoId(parser.token().getFila(), parser.token().getColumna(), parser.token().getLexema());
            // aca pierdo el id, se matchea
            parser.match("idMetVar");

            ArrayList<NodoExpresion> listaArgumentosActuales = argumentosActuales();
            // en chaqueo de sentencias debo verificar que el tam de argumentos actuales y el tam de id coinciden

            // si encadenado es null creo el nodo llamada metodo solo con arg actuales y el id
            NodoEncadenadoOpt nodoEncOpt = encadenadoOpt();
            // si no tiene encadenado se pone null
            return new NodoLlamadaMetodo(nodoId, listaArgumentosActuales, nodoEncOpt);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA METODO ESTATICO:
    //      - LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoLlamadaMetodoEstatico llamadaMetodoEstatico() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // voy a buscar el idClass a mi TS
        if (parser.ts().noEstaTs(parser.token().getLexema())){
            throw new ErrorSemantico(parser.token().getFila(), parser.token().getColumna(), "El id de clase: "+parser.token().getLexema()+" no ha sido declarado");
        }
        else {
            // obtengo el id
            //RegistroClase idClase = ts.getClase(token.getLexema());
            NodoId nodoId = new NodoId(parser.token().getFila(), parser.token().getColumna(), parser.token().getLexema());
            parser.match("idClass");
            parser.match("pto");
            NodoLlamadaMetodo nodoLlamadaMetodo  = llamadaMetodo();
            NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();

            // si no tiene encadenado se pone null
            return new NodoLlamadaMetodoEstatico(nodoId, nodoLlamadaMetodo, nodoEncadenadoOpt);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA CON CLASSOR:
    //      - LlamadaConClassor -> new LLamadaConClassOrRec
    //------------------------------------------------------------------------------------------------------------
    private NodoLlamadaConClassOr llamadaConClassor() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tNew = parser.token();
        parser.match("prNew");
        return new NodoLlamadaConClassOr(tNew, llamadaConClassorRec());
    }

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA CON CLASSOR REC:
    //      - LlamadaConClassorRec -> idClass ArgumentosActuales EncadenadoOpt | TipoPrimitivo [ Expresion ]
    //------------------------------------------------------------------------------------------------------------
    private NodoLlamadaConClassOrRec llamadaConClassorRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es igual a llamada metodo, solo que recibe una clase
        // por lo tanot hago lo mismo que en llamada metodo
        //Este metodo devuelve un nodoExpresion que puede ser llamadaMetodo o un NodoExpresion con un tipo
        if (parser.token().getTipo().equals("idClass")){
            if (parser.ts().noEstaTs(parser.token().getLexema())){
                throw new ErrorSemantico(parser.token().getFila(), parser.token().getColumna(), "El id de clase: "+parser.token().getLexema()+" no ha sido declarado");
            }
            else {
                // obtengo el id
                //RegistroClase idclase = ts.getClase(token.getLexema());
                NodoId nodoId = new NodoId(parser.token().getFila(), parser.token().getColumna(), parser.token().getLexema());
                parser.match("idClass");
                ArrayList<NodoExpresion> listaArgumentosActuales = argumentosActuales();
                NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt();
                // si no tiene encadenado se pone null
                return new NodoLlamadaConClassOrRec(nodoId, listaArgumentosActuales, nodoEncadenadoOpt);
            }
        }
        else {
            Tipo tipo = parser.getParserDeclaraciones().tipoPrimitivo();
            // tipo es mas que nada para chequeo de sentencias, para verificar que lp que venga en expresion coincida con el tipoprimitivo
            //tipoPrimitivo();
            parser.match("corcheteAbre");
            NodoExpresion nodoExpresion = expresion();
            NodoExpresion nodoE = expresion();
            parser.match("corcheteCierra");

            // VER BIEN QUE DEVOLVER ACA
            //return null; // pongo esto para que no me largue error
            return new NodoLlamadaConClassOrRec(tipo, nodoExpresion);
        }
    }

    //------------------------------------------------------------------------------------------------------------
    //                                          ARGUMENTOS ACTUALES
    //------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------
    // ARGUMENTOS ACTUALES:
    //      - ArgumentosActuales -> ( ListaExpresionesOpt )
    //------------------------------------------------------------------------------------------------------------
    private ArrayList<NodoExpresion> argumentosActuales() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("parAbre");
        ArrayList<NodoExpresion> listaArgumentosActuales = listaExpresionesOpt(new ArrayList<NodoExpresion>());
        parser.match("parCierra");
        return listaArgumentosActuales;

    }
}