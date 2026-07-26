package sintactico;

import lexico.ErrorLexico;
import lexico.Lexico;
import lexico.Token;
import semantico.Ast;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.NodoBloqueMetodo;
import semantico.nodos.NodoDefinicion;
import semantico.nodos.NodoProgram;
import semantico.nodos.NodoStart;
import semantico.registros.RegistroStart;


import java.util.ArrayList;

public class Parser {

    private final Lexico lexico;
    private Token token;
    private Token next;
    private boolean lookahead = false;
    private final TablaSimbolos ts = new TablaSimbolos();
    private Ast ast;

    private final ParserExpresiones parserExpresiones;
    private final ParserSentencias parserSentencias;
    private final ParserDeclaraciones parserDeclaraciones;

    public Parser(Lexico lexico){
        this.lexico = lexico;
        this.parserExpresiones = new ParserExpresiones(this);
        this.parserDeclaraciones = new ParserDeclaraciones(this);
        this.parserSentencias = new ParserSentencias(this);
    }

    public Token token(){return this.token;}
    public TablaSimbolos ts(){return this.ts;}
    public Ast ast(){return this.ast;}

    public ParserExpresiones getParserExpresiones() {
        return parserExpresiones;
    }

    public ParserDeclaraciones getParserDeclaraciones() {
        return parserDeclaraciones;
    }

    public ParserSentencias getParserSentencias() {
        return parserSentencias;
    }

    //------------------------------------------------------------------------------------------------------------------
    // ANALIZADOR: inicio
    //------------------------------------------------------------------------------------------------------------------
    public void analizador() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        this.token = lexico.analizador();
        // Program -> ListaDefiniciones Start
        NodoProgram program = program(); //program es la raiz de mi ast
        program.chequear(ts);
        // si sale de program es porque hizo match con $ entonces devolver Exito!

        ast = new Ast(program);

    }

    //------------------------------------------------------------------------------------------------------------------
    // PROGRAM: Program -> ListaDefiniciones Start
    //------------------------------------------------------------------------------------------------------------------
    private NodoProgram program() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        Token tProgram = token;
        ArrayList<NodoDefinicion> listaDefiniciones = parserDeclaraciones.listaDefiniciones(new ArrayList<NodoDefinicion>());
        // si es lambda va directo a start
        /*
        System.out.println("FILA:" + token.getFila());
        System.out.println("COLUMNA:" + token.getColumna());
        System.out.println("LEXEMA:" + token.getLexema());
        */

        // ya arme toda mi TS, antes de seguir voy a consolidar
        ts.consolidar();

        //RegistroStart metodoStart = new RegistroStart();
        NodoStart start = start();

        // consolido star, para verificar las var locales
        ts.consolidarStart();

        match("EOF");
        return new NodoProgram(tProgram, listaDefiniciones, start);

    }

    //------------------------------------------------------------------------------------------------------------------
    // START: Start -> start BloqueMetodo
    //------------------------------------------------------------------------------------------------------------------
    private NodoStart start() throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        if (token.getLexema().equals("start")){
            Token tStart = token;
            // deberia matchear idMetVar, porque start al no ser reservada la toma como idMetVar
            match("idMetVar");

            // Creo el registro start para el contexto del bloque de este metodo
            RegistroStart registroStart = new RegistroStart();
            ts.setBloqueStart(registroStart);

            NodoBloqueMetodo bloqueMetodo = parserDeclaraciones.bloqueMetodo();

            return new NodoStart(tStart, bloqueMetodo);
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba start y se enontro "+token.getTipo());
        }

    }

    //------------------------------------------------------------------------------------------------------------------
    // MATCH:
    //      - funcion matcheo que vamos a utilizar para pedir el next token
    //      - voy a verificar que el tipo que recibo es el tipo esperado. si esto pasa entonces pido next token
    //      - solo avanzo si matcheo, en ninguna otra parte del codigo deberia avanzar
    //------------------------------------------------------------------------------------------------------------------
    void match(String tipoEsperado) throws ErrorSintactico, ErrorLexico {
        if (token.getTipo().equals(tipoEsperado)){
            //si ya mire hacia adelante no necesito volver a pedir nextoken porque
            //sino voy a perder el símbolo que estoy viendo
            if (lookahead){
                this.token = this.next;
                lookahead = false;
            } else {
                nextToken();
            }
        }
        else {
            throw new ErrorSintactico(token.getFila(), token.getColumna(), "Se esperaba '"+tipoEsperado+"' y se enontro '"+token.getTipo()+"'");
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // NEXT TOKEN:
    //      - funcion para pedir el next token cuando matcheo
    //------------------------------------------------------------------------------------------------------------------
    private void nextToken() throws ErrorLexico {
        token = lexico.analizador();
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONSUMIR OPERADOR
    //------------------------------------------------------------------------------------------------------------------
    public Token consumirOperador() throws ErrorSintactico, ErrorLexico {
        Token operador = token;
        match(token.getTipo());
        return operador;
    }

    //------------------------------------------------------------------------------------------------------------------
    // LOOKAHEAD:
    //      - funcion solo para ver el siguiente, sin avanzar (lookahead)
    //      - la usamos solo en el caso: COMPLETAR
    //------------------------------------------------------------------------------------------------------------------
    public Token lookAhead() throws ErrorLexico {
        this.next = lexico.analizador();
        this.lookahead = true;
        return next;

       /* if (next.getTipo() != "EOF"){
            this.lookahead = true;
            return next;
        }
        return null;*/
    }

    //..................................................................................................................
    //                                      METODOS AUXILIARES DEL PARSER
    //..................................................................................................................
    //-----------------------
    // CONJUNTOS DE PRIMEROS
    //-----------------------

    //------------------------------------------------------------------------------------------------------------------
    // CONJUNTOS DE PRIMEROS OPERANDO:
    //      - Prim(Operando) = {nil, true, false, intLiteral, strLiteral, (, self, id, idClass, new, ., lambda}
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimeroOperando(Token token){
        return token.getTipo().equals("prNil") || token.getTipo().equals("prTrue") || token.getTipo().equals("prFalse")
                || token.getTipo().equals("literal_entero") || token.getTipo().equals("literal_cadena") || token.getTipo().equals("parAbre")
                || token.getTipo().equals("prSelf") || token.getTipo().equals("idMetVar") || token.getTipo().equals("idClass")
                || token.getTipo().equals("prNew") || token.getTipo().equals("pto");
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONJUNTOS DE PRIMEROS EXPRESION:
    //      - Prim(Expresion) = {+, -, !, ++, --, (, self, id, idclass, new, nil, true, false, intLiteral, strliteral, . ,lambda}
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimeroExpresion(Token token){
        return token.getTipo().equals("opMas") || token.getTipo().equals("opMenos") || token.getTipo().equals("opNot")
                || token.getTipo().equals("opMasMas") || token.getTipo().equals("opMenosMenos") || token.getTipo().equals("prNil")
                || token.getTipo().equals("prTrue") || token.getTipo().equals("prFalse") || token.getTipo().equals("literal_entero")
                || token.getTipo().equals("literal_cadena") || token.getTipo().equals("parAbre") || token.getTipo().equals("prSelf")
                || token.getTipo().equals("idMetVar") || token.getTipo().equals("idClass") || token.getTipo().equals("prNew")
                || token.getTipo().equals("pto");
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONJUNTOS DE PRIMEROS SENTENCIA:
    //      - Prim(Sentencia) = {;, id, self, (, if, while, for, {, ret}
    //      - Que tipo de id es? verificar eso asi lo devuelvo aca
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimeroSentencia(Token token){
        return token.getTipo().equals("ptoComa") || token.getTipo().equals("idMetVar") || token.getTipo().equals("prSelf")
                || token.getTipo().equals("parAbre") || token.getTipo().equals("prIf") || token.getTipo().equals("prWhile")
                || token.getTipo().equals("prFor") || token.getTipo().equals("llaveAbre") || token.getTipo().equals("prRet");

    }

    //------------------------------------------------------------------------------------------------------------------
    // CONJUNTOS DE PRIMEROS TIPO PRIMITIVO:
    //      - Prim(TipoPrimitivo) = {str, bool, int}
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimeroTipoPrimitivo(Token token){
        return token.getTipo().equals("tStr") || token.getTipo().equals("tBool") || token.getTipo().equals("tInt");
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONJUNTOS DE PRIMEROS DE TIPO METODO:
    //      - Prim(TipoMetodo) = {Str, Bool, Int, idClass, Array, lambda}
    //      - Prim(DeclaracionVarLocal) = {Str, Bool, Int, idClass, Array, lambda}}
    //      - Prim(Herencia) = {Str, Bool, Int, idClass, Array}
    //      - tArray no lo tenemos mas es un idClass y lo verificamos dentro de tipoPrimitivo
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimeroTipoMetodo(Token token){
        return token.getTipo().equals("tStr") || token.getTipo().equals("tBool") || token.getTipo().equals("tInt")
                || token.getTipo().equals("idClass");
    }

    //------------------------------------------------------------------------------------------------------------------
    // CONJUNTOS DE PRIMEROS MIEMBRO:
    //      - Prim(Miembro) = {st, .}
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimeroMiembro(Token token){
        return token.getTipo().equals("prSt") || token.getTipo().equals("pto");

    }

    //------------------------------------------------------------------------------------------------------------------
    // ES LITERAL:
    //      - "prNil" , "prTrue", "prFalse", "literal_entero", "literal_cadena"
    //------------------------------------------------------------------------------------------------------------------
    public boolean esLiteral(Token token){
        return token.getTipo().equals("prNil") || token.getTipo().equals("prTrue")
                || token.getTipo().equals("prFalse") || token.getTipo().equals("literal_entero")
                || token.getTipo().equals("literal_cadena");
    }

    //------------------------------------------------------------------------------------------------------------------
    // ES PRIMARIO:
    //      - "parAbre", "prSelf", "idMetVar", "idClass", "prNew"
    //------------------------------------------------------------------------------------------------------------------
    public boolean esPrimario(Token token){
        return token.getTipo().equals("parAbre") || token.getTipo().equals("prSelf")
                || token.getTipo().equals("idMetVar") || token.getTipo().equals("idClass")
                || token.getTipo().equals("prNew");
    }
}
