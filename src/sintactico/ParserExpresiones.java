package sintactico;

import lexico.ErrorLexico;
import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.expresion.*;
import semantico.nodos.expresion.encadenables.primario.Nnew.NodoNew;
import semantico.nodos.expresion.encadenables.primario.Nnew.NodoNewArreglo;
import semantico.nodos.expresion.encadenables.primario.Nnew.NodoNewObjeto;
import semantico.nodos.expresion.encadenables.primario.NodoPrimario;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoLlamadaMetodo;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoLlamadaMetodoEstatico;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAcceso;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAccesoArreglo;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAccesoSelf;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAccesoVar;
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
        //System.out.println("Voy a expresion or con: "+parser.token().getLexema());
        return expresionOr();

    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION OR:
    //      - ExpresionOr -> ExpresionAnd ExpresionOrRec
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionOr() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoExpresion nodoExpresionAnd = expresionAnd();
        //System.out.println("Voy a expresion or rec con: "+parser.token().getLexema());
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
            //System.out.println("Entre al if de expresionOrRec con: "+parser.token().getLexema());
            //System.out.println("retorno: "+nodoIzqRec.getToken().getLexema());
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
        NodoExpresion expresionUnario = expresionUnario();
        return expresionMulRec(expresionUnario);
    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION MUL REC:
    //      - ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresion expresionMulRec(NodoExpresion nodoIzq) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // simpre que venga un opMul hago recursividad
        if (Operador.esOpMul(parser.token())){
            Token operador = opMul();
            NodoExpresion nodoDer = expresionUnario();
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
    private NodoExpresion expresionUnario() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // siempre que venga un opUnario vuelvo
        if (Operador.esOpUnario(parser.token())){
            Token operador = opUnario();
            NodoExpresion expresion = expresionUnario();
            //System.out.println("Entre a expresionUnario con el token: "+operador.getLexema());
            //System.out.println("Armo el nodo expresion unario y le mando: "+operador.getLexema()+" y la expresion: "+expresion.getToken().getLexema());
            return new NodoExpresionUnario(operador, expresion);
        } else { // si no es opMas ni opMenos es un operando
            // si lo que viene no esta en los prim de operando no voy
            if (parser.esPrimeroOperando(parser.token())) {
                //NodoOperando operando = operando();
                //return new NodoExpresionUnario(operando);
                return operando();
            } else {
                throw new ErrorSintactico(parser.token(), "Se esperaba un operando y se encontro " + parser.token().getTipo());
            }
            //operando();
        }

    }

    //------------------------------------------------------------------------------------------------------------
    // EXPRESION PARENTIZADA:
    //      - ExpresionParentizada -> ( Expresion ) EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoExpresionParentizada expresionParentizada() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tExpresionParentizada = parser.token();
        parser.match("parAbre");
        NodoExpresion nodoExpresion = expresion();
        parser.match("parCierra");
        NodoExpresionParentizada nodoExpresionParentizada = new NodoExpresionParentizada(tExpresionParentizada, nodoExpresion);
        nodoExpresionParentizada.setProxEncadenado(encadenadoOpt());

        return nodoExpresionParentizada;
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
    //      - devuelve: AccesoVar: id.id.id | AccesoArreglo: id[indice]
    //------------------------------------------------------------------------------------------------------------
    public NodoAcceso accesoVarSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoAccesoVar varEncadenado = new NodoAccesoVar(parser.token());
        parser.match("idMetVar");
        //accesoVarSimpleRec puede ser: null | NodoVarEncadenado| nodoExpresion
        return accesoVarSimpleRec(varEncadenado);
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR SIMPLE RECURSIVO:
    //      - AccesoVarSimpleRec -> ListaEncadenadoSimple | [ Expresion ]
    //------------------------------------------------------------------------------------------------------------
    private NodoAcceso accesoVarSimpleRec(NodoAccesoVar varEncadenado) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de lista enadenado simple entro ahi
        // Prim(ListaEncadenadoSimple) = {. , lambda}
        if (parser.token().getTipo().equals("pto")) {
            NodoAccesoVar resto = listaEncadenadoSimple(); // no le paso nada
            varEncadenado.setProxEncadenado(resto);
            return varEncadenado;
        } else {
            if (parser.token().getTipo().equals("corcheteAbre")) {
                parser.match("corcheteAbre");
                NodoExpresion nodoExpresion = expresion();
                parser.match("corcheteCierra");
                return new NodoAccesoArreglo(varEncadenado.getToken(),nodoExpresion);
            }
        }
        return varEncadenado;
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO SELF SIMPLE:
    //      - AccesoSelfSimple -> self ListaEncadenadoSimple
    //------------------------------------------------------------------------------------------------------------
    public NodoAccesoSelf accesoSelfSimple() throws ErrorSintactico, ErrorLexico {
        NodoAccesoSelf selfEncadenado = new NodoAccesoSelf(parser.token());
        parser.match("prSelf");
        NodoAccesoVar varEncadenado = listaEncadenadoSimple();
        //Si hay encadenado varEncadenado != nul -> selfEncadenado = self y varEncadenado = id1.id2.id3
        selfEncadenado.setProxEncadenado(varEncadenado);
        return selfEncadenado;
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO SELF:
    //      - AccesoSelf -> self EncadenadoOpt
    //      - Si el metodo es estatico no puedo acceder a una variable de instancia (self)
    //------------------------------------------------------------------------------------------------------------
    private NodoAccesoSelf accesoSelf() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tokenSelf = parser.token();
        parser.match("prSelf");
        NodoAccesoSelf nodoAccesoSelf = new NodoAccesoSelf(tokenSelf);
        nodoAccesoSelf.setProxEncadenado(encadenadoOpt());
        return nodoAccesoSelf;
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR:
    //      - AccesoVar -> id AccesoVarRec
    //------------------------------------------------------------------------------------------------------------
    private NodoAcceso accesoVar() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tokenId = parser.token();
        parser.match("idMetVar");
        return accesoVarRec(tokenId);
    }

    //------------------------------------------------------------------------------------------------------------
    // ACCESO VAR REC:
    //      - AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoAcceso accesoVarRec(Token tokenId) throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (parser.token().getTipo().equals("corcheteAbre")) {
            parser.match("corcheteAbre");
            NodoExpresion nodoExpresion = expresion();
            NodoAccesoArreglo nodoAccesoArreglo = new NodoAccesoArreglo(tokenId, nodoExpresion);
            parser.match("corcheteCierra");

            nodoAccesoArreglo.setProxEncadenado(encadenadoOpt());
            return nodoAccesoArreglo;
        } else {
            // AccesoVarRec -> EncadenadoOpt
            NodoAccesoVar nodoAccesoVar = new NodoAccesoVar(tokenId);
            nodoAccesoVar.setProxEncadenado(encadenadoOpt());
            return nodoAccesoVar;
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
    private NodoAccesoVar listaEncadenadoSimple() throws ErrorSintactico, ErrorLexico {
        // es recursiva por lo tanto cada vez que viene un primero de encadenado simple vuelvo a entrar
        // Prim(EncadenadoSimple) = {.}
        //System.out.println("Entre a lista Encadenado simple con: "+parser.token().getLexema());
        if (parser.token().getTipo().equals("pto")) {
            NodoAccesoVar nuevaVarEnc = encadenadoSimple();
            //System.out.println("Encadenado nueva: "+nuevaVarEnc.getToken().getLexema());
            NodoAccesoVar resto = listaEncadenadoSimple();
            //System.out.println("Resto: "+resto.toString());
            //System.out.println("Sali de encadenado simple con: "+parser.token().getLexema());
            nuevaVarEnc.setProxEncadenado(resto);
            //System.out.println("Retorno en nodoaccesovar: "+nuevaVarEnc.getToken().getLexema());
            return nuevaVarEnc;
        }
        return null; // ya no hay encadenado
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO SIMPLE:
    //      - EncadenadoSimple -> . id
    //------------------------------------------------------------------------------------------------------------
    private NodoAccesoVar encadenadoSimple() throws ErrorSintactico, ErrorLexico {
        parser.match("pto");
        //System.out.println("Entre a encadenado simple y leo: "+parser.token().getLexema());
        NodoAccesoVar varEncadenado = new NodoAccesoVar(parser.token());
        parser.match("idMetVar");
        return varEncadenado;
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO OPT:
    //      - EncadenadoOpt -> Encadenado | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoAcceso encadenadoOpt() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si es pto va a encadendo, Prim(Encadenado) = { . }
        //System.out.println("Vine a encadenado opt con: "+parser.token().getLexema());
        if (parser.token().getTipo().equals("pto")){
            //System.out.println("voy a encadenado: "+parser.token().getLexema());
            return encadenado();
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO:
    //      - Encadenado -> . EncadenadoRec
    //------------------------------------------------------------------------------------------------------------
    private NodoAcceso encadenado() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        parser.match("pto");
        //System.out.println("Estoy en encadenado, matcheo el punto y leo: "+parser.token().getLexema());
        return encadenadoRec();
    }

    //------------------------------------------------------------------------------------------------------------
    // ENCADENADO RECURSIVO:
    //      - EncadenadoRec -> LlamadaMetodo | AccesVar
    //------------------------------------------------------------------------------------------------------------
    private NodoAcceso encadenadoRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // como con ambos me llega id veo el nextToken
        Token next = parser.lookAhead();
        if (next.getTipo().equals("parAbre")){ // es porq esta en llamada metodo
            return llamadaMetodo();
        }
        else {
            //System.out.println("Voy a accesoVar con: "+parser.token().getLexema());
            return accesoVar();

        }
        //return null;
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
    private NodoExpresion operando() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        //String tipo = token.getTipo();
        if (parser.esLiteral(parser.token())){
            //NodoLiteral literal = literal();
            return literal();
        }
        if (parser.esPrimario(parser.token())){
            //NodoPrimario primario = primario();
            //primario.setProxEncadenado(encadenadoOpt());

            return primario(); //primario() ya devielve el nodo con su cadena completa
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
                return new NodoNil(t);
            case "prTrue" , "prFalse":
                parser.match(parser.token().getTipo());
                return new NodoBool(t);
            case "literal_entero":
                parser.match("literal_entero");
                return new NodoNum(t);
            case "literal_cadena":
                parser.match("literal_cadena");
                return new NodoStr(t);
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
                return nodoExpresionParentizada;
            // Prim(AccesoSelf) = { self }
            case "prSelf":
                // verifico que no este en un contexto estatico
                //System.out.println("Metodo actual: "+ts.metodoActual.getNombre());
                //if (parser.ts().metodoActual.esEstatico){
                //    throw new ErrorSemantico(parser.token(), "No se puede acceder a una variable de instancia en un contexto estatico");
                //}
                NodoAccesoSelf nodoAccesoSelf = accesoSelf();
                return nodoAccesoSelf;
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
                    return llamadaMetodo();

                }
                else {
                    NodoAcceso accesoVar = accesoVar();
                    return accesoVar;
                }
                //break;
                // Prim(LlamadaMetodoEstatico) = {idClass}
            case "idClass":
                return llamadaMetodoEstatico();

            // Prim(LlamadaConClassor) = {new}
            case "prNew":
                return llamadaConClassor();

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
        // resolver cuando estoy en start
        /*
        if (parser.ts().bloqueStart != null){
            // estoy en start, cambia el contexto
        } */
        Token tId = parser.token();
        parser.match("idMetVar");
        ArrayList<NodoExpresion> listaArgumentosActuales = argumentosActuales();
        NodoLlamadaMetodo nodoLlamadaMetodo = new NodoLlamadaMetodo(tId, listaArgumentosActuales);
        nodoLlamadaMetodo.setProxEncadenado(encadenadoOpt());
        return nodoLlamadaMetodo;
    }

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA METODO ESTATICO:
    //      - LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoLlamadaMetodoEstatico llamadaMetodoEstatico() throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        Token tLlamadaMetodoEstatico = parser.token();
        parser.match("idClass");
        parser.match("pto");
        NodoLlamadaMetodo nodoLlamadaMetodo  = llamadaMetodo();

        NodoLlamadaMetodoEstatico nodoLlamadaMetodoEstatico = new NodoLlamadaMetodoEstatico(tLlamadaMetodoEstatico, nodoLlamadaMetodo);
        nodoLlamadaMetodoEstatico.setProxEncadenado(encadenadoOpt());
        return nodoLlamadaMetodoEstatico;

    }

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA CON CLASSOR:
    //      - LlamadaConClassor -> new LLamadaConClassOrRec
    //------------------------------------------------------------------------------------------------------------
    private NodoNew llamadaConClassor() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tNew = parser.token();
        parser.match("prNew");
        return llamadaConClassorRec(tNew);
    }

    //------------------------------------------------------------------------------------------------------------
    // LLAMADA CON CLASSOR REC:
    //      - LlamadaConClassorRec -> idClass ArgumentosActuales EncadenadoOpt | TipoPrimitivo [ Expresion ]
    //------------------------------------------------------------------------------------------------------------
    private NodoNew llamadaConClassorRec(Token tNew) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // es igual a llamada metodo, solo que recibe una clase
        // por lo tanto hago lo mismo que en llamada metodo
        //Este metodo devuelve un nodoExpresion que puede ser llamadaMetodo o un NodoExpresion con un tipo
        if (parser.token().getTipo().equals("idClass")){

            Token tIdClass = parser.token();
            parser.match("idClass");
            ArrayList<NodoExpresion> listaArgumentosActuales = argumentosActuales();
            NodoNewObjeto nodoNewObjeto = new NodoNewObjeto(tIdClass, listaArgumentosActuales);
            nodoNewObjeto.setProxEncadenado(encadenadoOpt());
            return nodoNewObjeto;
        }
        else {
            Tipo tipo = parser.getParserDeclaraciones().tipoPrimitivo();
            parser.match("corcheteAbre");
            NodoExpresion nodoExpresion = expresion();
            parser.match("corcheteCierra");

            NodoNewArreglo nodoNewArreglo = new NodoNewArreglo(tNew, tipo, nodoExpresion);
            return nodoNewArreglo;
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