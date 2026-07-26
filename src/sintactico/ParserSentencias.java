package sintactico;

import lexico.ErrorLexico;
import lexico.Token;
import semantico.ErrorSemantico;
import semantico.nodos.expresion.NodoExpresion;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAcceso;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoAccesoSelf;
import semantico.nodos.sentencia.*;
import semantico.tipos.Tipo;
import sintactico.auxiliares.AuxRamasIf;

import java.util.ArrayList;

public class ParserSentencias {
    private final Parser parser;

    public ParserSentencias(Parser parser) {
        this.parser = parser;
    }

    //------------------------------------------------------------------------------------------------------------
    // SENTENCIA SIMPLE:
    //      - SentenciaSimple -> ( Expresion )
    //------------------------------------------------------------------------------------------------------------
    private NodoSentenciaSimple sentenciaSimple() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tokenSS = parser.token();
        parser.match("parAbre");
        NodoExpresion nodoExpresion = parser.getParserExpresiones().expresion();
        parser.match("parCierra");
        return new NodoSentenciaSimple(tokenSS, nodoExpresion);
    }

    //------------------------------------------------------------------------------------------------------------
    // SENTENCIA:
    //      - Sentencia -> ; | Asignacion | SentenciaSimple ; | if ( Expresion ) SentenciaRec | while ( Expresion ) Sentencia |
    //          for ( TipoPrimitivo idMetAt in idMetAt) Sentencia | Bloque | ret ExpresionOpt
    //------------------------------------------------------------------------------------------------------------
    private NodoSentencia sentencia(/*Tipo tipo*/) throws ErrorSintactico, ErrorLexico, ErrorSemantico {

        if (parser.token().getTipo().equals("ptoComa")){
            parser.match("ptoComa");
        }
        else {// SENTENCIA SIMPLE
            if (parser.token().getTipo().equals("parAbre")){
                NodoSentenciaSimple sentenciaSimple = sentenciaSimple();
                parser.match("ptoComa");
                return sentenciaSimple;
            }
            else {// IF
                if (parser.token().getTipo().equals("prIf")){
                    Token tIf = parser.token();
                    parser.match(("prIf"));
                    parser.match("parAbre");
                    //System.out.println("Voy a expresion con: "+token.getTipo());
                    NodoExpresion condicion = parser.getParserExpresiones().expresion(); //devuelvo la condicion
                    parser.match("parCierra");
                    AuxRamasIf sentenciaRec = sentenciaRec(); // me devuelve 2 nodos sentencia (then y else del if actual)
                    return new NodoIf(tIf, condicion, sentenciaRec.getSentenciaThen(), sentenciaRec.getSentenciaElse());
                }
                else { // WHILE
                    if (parser.token().getTipo().equals("prWhile")){
                        Token tWhile = parser.token();
                        parser.match("prWhile");
                        parser.match("parAbre");
                        NodoExpresion expresion = parser.getParserExpresiones().expresion();
                        parser.match("parCierra");
                        NodoSentencia sentencia = sentencia();
                        return new NodoWhile(tWhile, expresion, sentencia);
                    }
                    else { // FOR
                        if (parser.token().getTipo().equals("prFor")){
                            Token tFor = parser.token();
                            parser.match("prFor");
                            parser.match("parAbre");
                            Tipo tipoVar = parser.getParserDeclaraciones().tipoPrimitivo();
                            Token tVariable = parser.token();
                            parser.match("idMetVar"); //en chequeo de sentencias se chequea que la variable no haya sido declarada como una variable local / param del metodo actual (no se si tambien atr de la clase actual)
                            parser.match("prIn");
                            // en chequeo verifico que sea de tipo Array
                            Token tIterador = parser.token();
                            parser.match("idMetVar");
                            parser.match("parCierra");
                            NodoSentencia cuerpoFor = sentencia();
                            return new NodoFor(tFor, tipoVar, tVariable, tIterador, cuerpoFor);
                        }
                        else { // BLOQUE
                            if (parser.token().getTipo().equals("llaveAbre")){
                                return bloque();
                            }
                            else { // RET

                                if (parser.token().getTipo().equals("prRet")){
                                    // aca rompo si el tipo del metodo es void
                                    //if (tipo.getNombreTipo().equals("Void")){
                                    //    throw new ErrorSemantico(token.getFila(), token.getColumna(), "El tipo de retorno del metodo es void, no puede haber un ret");
                                    //}
                                    Token tRet = parser.token();
                                    parser.match("prRet");
                                    //expresionOpt();
                                    return new NodoRetorno(tRet, parser.getParserExpresiones().expresionOpt());

                                }
                                else { //ASIGNACIÓN
                                    // con idMetVar o con self voy a asignacion
                                    if (parser.token().getTipo().equals("idMetVar") | parser.token().getTipo().equals("prSelf")){
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

    //------------------------------------------------------------------------------------------------------------
    // LISTA SENTENCIA:
    //      - ListaSentencia -> Sentencia ListaSentencia | lambda
    //------------------------------------------------------------------------------------------------------------
    public ArrayList<NodoSentencia> listaSentencia(ArrayList<NodoSentencia> listaSent) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // mientras este en los primeros de sentencia vuelvo a entrar
        if (parser.esPrimeroSentencia(parser.token())){
            NodoSentencia sentencia = sentencia();
            if (sentencia != null){
                listaSent.add(sentencia);
                return listaSentencia(listaSent);
            }
        }
        return listaSent;
    }

    //------------------------------------------------------------------------------------------------------------
    // SENTENCIA REC:
    //      - SentenciaRec -> Sentencia RecursivoElse
    //------------------------------------------------------------------------------------------------------------
    private AuxRamasIf sentenciaRec() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoSentencia nodoThen = sentencia();
        NodoSentencia nodoElse = recursivoElse();
        return new AuxRamasIf(nodoThen, nodoElse);
    }

    //------------------------------------------------------------------------------------------------------------
    // RECURSIVO ELSE:
    //      - RecursivoElse -> else Sentencia | lambda
    //------------------------------------------------------------------------------------------------------------
    private NodoSentencia recursivoElse() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (parser.token().getTipo().equals("prElse")){
            parser.match("prElse");
            return sentencia();
        }
        return null;
        // sino es lambda
    }

    //------------------------------------------------------------------------------------------------------------
    // BLOQUE:
    //      - BLoque -> { ListaSentencia }
    //------------------------------------------------------------------------------------------------------------
    private NodoBloque bloque() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tBloque = parser.token();
        parser.match("llaveAbre");
        ArrayList<NodoSentencia> listaSent = listaSentencia(new ArrayList<NodoSentencia>());
        parser.match("llaveCierra");
        return new NodoBloque(tBloque, listaSent);
    }

    //------------------------------------------------------------------------------------------------------------
    // ASIGNACION:
    //      - Asignacion -> AccesoVarSimple = Expresion | AccesoSelfSimple = Expresion
    //------------------------------------------------------------------------------------------------------------
    private NodoAsignacion asignacion() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        // si esta en los primeros de acceso var simple entro
        // Prim(AccesoVarSimple) = {id}
        if (parser.token().getTipo().equals("idMetVar")){
            NodoAcceso nodoAccesoVarSimple = parser.getParserExpresiones().accesoVarSimple(); //NODO IZQ
            Token tAsig = parser.token();
            parser.match("opIgual");
            NodoExpresion nodoExpresion = parser.getParserExpresiones().expresion(); //NODO DER
            return new NodoAsignacion(tAsig, nodoAccesoVarSimple, nodoExpresion);
        }
        else {
            // si esta en los primeros de acceso self simple entro
            // Prim(AccesoVarSimple) = {self}
            if (parser.token().getTipo().equals("prSelf")){
                NodoAccesoSelf nodoAccesoSelfSimple = parser.getParserExpresiones().accesoSelfSimple();
                Token tAsig = parser.token();
                parser.match("opIgual");
                NodoExpresion nodoExpresion = parser.getParserExpresiones().expresion();
                return new NodoAsignacion(tAsig, nodoAccesoSelfSimple, nodoExpresion);
            }
        }
        return null;
    }

}
