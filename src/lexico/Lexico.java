package lexico;
import java.util.ArrayList;
import java.util.List;


//Esta clase corresponde a la etapa del analizador lexico del compilador, en la misma se identifican tanto los lexemas y su token correspondiente como el numero de linea y columna en la que se encuentra cada uno en el codigo fuente,
//en caso de que exista un error lexico el analizador lanza una excepion notificando el error sino la información se almacena en un array para que luego pueda ser presentada por el ejecutador
public class Lexico {
    private int contadorLineas;
    private int contadorColumnas;
    private int puntero;
    private String codFuente;
    private char charActual;
    private char charSig;
    //agrego una lista de tokens para pasarle al sintactico
    private List<Token> listaTokens = new ArrayList<>();

    public Lexico (String codFuente) {
        this.codFuente = codFuente;
        this.contadorLineas = 1;
        this.contadorColumnas = 1;
        this.puntero = 0;

    }

    //Recorre el codigo fuente caracter a caracter formando lexemas y verificando que que cada uno cumpla con las reglas lexicas
    //Se ignoran los comentarios y espacios en blanco
    //Almacena en un arreglo los lexemas cuya verficación lexica sea correcta
    //Actualiza el contador de lineas y el contador de columnas a medida que va haciendo el analisis
    //Detiene la ejecución en caso de que consiga algún error lexico y notifica la informacion del error
    public Token analizador() throws ErrorLexico{

        //Inicializo la tabla de palabras reservadas
        Keywords.inicializar();

        String lexema;
        String lexemaError;
        
        //1. Analizamos el codigo fuente y evaluamos cada uno de los casos
        //int longitud = codFuente.length();
        int tab = 8; // defino esta variable estableciendo que un tab son 8 posiciones

        inicializarCharActual();
        //avanzar();

        while (true){

            // ESPACIOS ------------------------------------------------------------------------------------------------
            if (Character.isWhitespace(charActual)){
                //System.out.println("WHITESPACE AT: LINEA" + contadorLineas + ", COLUMNA" + contadorColumnas + " ");
                if ((int) charActual == 32 | (int) charActual == 11){ // ' ', \v
                    avanzar();
                }
                else {
                        if ((int) charActual == 10) { // \n : salto de linea en Linux
                            incrementarLineas();
                            reiniciarColumnas();
                            avanzar();
                        }
                        else {
                                // REVISAR ------------------------------------------
                                if ((int) charActual == 9) { // \t : tab (8 posiciones)
                                    //aumentamos un tab
                                    int incrementoCol = (tab - (this.contadorColumnas % tab));
                                    incrementarColumnas(incrementoCol);

                                }
                                else {
                                        // \r: va combinado con \n para hacer el salto de linea en WIndows, no incrementa columnas
                                        if ((int) charActual == 13) {
                                            avanzar();
                                        }
                                }
                        }
                }
            }
            //  COMENTARIOS --------------------------------------------------------------------------------------------
            else {
                    if (charActual == '/') {
                        actualizarCharSig();

                        // 1. COMENTARIO SIMPLE
                        if (charSig == '/'){
                            avanzar(); // incremento puntero, incremento columnas, cambio charActual
                            //actualizarCharSig();
                            while (!esFinArchivo(puntero) && (int) charActual != 10 && (int) charActual != 13){
                                avanzar();
                                //actualizarCharSig();
                            }


                        }
                        // 2. COMENTARIO MULTIPLE
                        else {
                                if (charSig == '*') { // /*
                                    avanzar(); // charActual = '*'
                                    avanzar();
                                    actualizarCharSig();
                                    while (!esFinArchivo(puntero) && charActual != '*' && charSig != '/') {
                                        if ((int) charActual == 13 && (int) charSig == 10) {
                                            //hay un salto de lineas: incremento lineas y reinicio columnas
                                            avanzar(); // (int) charActual = 10
                                            incrementarLineas();
                                            reiniciarColumnas();
                                        }
                                        else {
                                                if (charActual == 10) {
                                                    incrementarLineas();
                                                    reiniciarColumnas();
                                                }
                                        }

                                        avanzar();
                                        actualizarCharSig();
                                    }

                                    if (esFinArchivo(puntero)) {
                                        throw new ErrorLexico(contadorLineas, contadorColumnas, "NO SE CERRO EL COMENTARIO MULTIPLE");
                                    }
                                    else {
                                        // charActual = '*' y charSig = '/'
                                        avanzar(); // charActual = '/'

                                    }


                                }
                                // OPERADOR /= -----------------------------------------------------------------------------------------
                                else {
                                        if (charSig == '=') {
                                            avanzar();
                                            //almacenarToken("opdivIgual", "/=", contadorLineas, contadorColumnas);
                                            return new Token("opdivIgual", "/=", contadorLineas, contadorColumnas);

                                        }
                                        // OPERADOR / ------------------------------------------------------------------------------------------
                                        else {
                                            avanzar();
                                            //almacenarToken("opdiv", "/", contadorLineas, contadorColumnas);
                                            return new Token("opdiv", "/", contadorLineas, contadorColumnas);

                                        }
                                }
                        }
                    }
                    // CADENAS DE TEXTO ----------------------------------------------------------------------------------------
                    else {
                            if ((int) charActual == 34) {

                                // Comillas dobles, almacena strings
                                lexema = "" + charActual;

                                avanzar();
                                actualizarCharSig();

                                while (!esFinArchivo(puntero) && (int) charActual != 34) {

                                    if (((int) charActual == 13 && (int) charSig == 10) | (int) charActual == 10) {
                                        //hay un salto de lineas: incremento lineas y reinicio columnas
                                        avanzar();
                                        lexema += " ";
                                        incrementarLineas();
                                        reiniciarColumnas();

                                    }
                                    else {
                                        if ((int) charActual == 10) {
                                            lexema += " ";
                                            incrementarLineas();
                                            reiniciarColumnas();

                                        }
                                        else {
                                            lexema += charActual; // se puede o no agregar el salto a la cadena (no lo agrego)
                                        }

                                    }

                                    avanzar();
                                    actualizarCharSig();
                                }

                                if (esFinArchivo(puntero)) {
                                    throw new ErrorLexico(contadorLineas, contadorColumnas, "NO SE CERRO LA CADENA DE CARACTERES");
                                    // ERROR LEXICO: NO SE CERRO LA CADENA DE CARACTERES
                                }
                                else {
                                    lexema += charActual;
                                    avanzar();
                                    //almacenarToken("literal_cadena", lexema, contadorLineas, contadorColumnas);
                                    return new Token("literal_cadena", lexema, contadorLineas, contadorColumnas);

                                }
                            }
                            // OPERADORES ARITMÉTICOS Y DE DECREMENTO ------------------------------------------------------------------
                            else {
                                    if (esOperador(charActual)) {

                                        actualizarCharSig();

                                        switch (charActual) {
                                            case '+':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMasIgual", "+=", contadorLineas, contadorColumnas);
                                                    return new Token("opMasIgual", "+=", contadorLineas, contadorColumnas);

                                                } else {
                                                    // para los casos de opUniario que tiene ++ y --
                                                    if (charSig == '+'){
                                                        avanzar();
                                                        avanzar();
                                                        //almacenarToken("opMasMas", "++", contadorLineas, contadorColumnas);
                                                        return new Token("opMasMas", "++", contadorLineas, contadorColumnas);

                                                    } else {
                                                        avanzar();
                                                        //almacenarToken("opMas", "+", contadorLineas, contadorColumnas);
                                                        return new Token("opMas", "+", contadorLineas, contadorColumnas);
                                                    }
                                                }

                                            case '-':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMenosIgual", "-=", contadorLineas, contadorColumnas);
                                                    return new Token("opMenosIgual", "-=", contadorLineas, contadorColumnas);

                                                } else {
                                                    if (charSig == '-'){
                                                        avanzar();
                                                        avanzar();
                                                        //almacenarToken("opMenosMenos", "--", contadorLineas, contadorColumnas);
                                                        return new Token("opMenosMenos", "--", contadorLineas, contadorColumnas);

                                                    } else {
                                                        avanzar();
                                                        //almacenarToken("opMenos", "-", contadorLineas, contadorColumnas);
                                                        return new Token("opMenos", "-", contadorLineas, contadorColumnas);
                                                    }
                                                }

                                            case '*':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opPorIgual", "*=", contadorLineas, contadorColumnas);
                                                    return new Token("opPorIgual", "*=", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opPor", "*", contadorLineas, contadorColumnas);
                                                    return new Token("opPor", "*", contadorLineas, contadorColumnas);

                                                }

                                            case '%':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opModIgual", "%=", contadorLineas, contadorColumnas);
                                                    return new Token("opModIgual", "%=", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opMod", "%", contadorLineas, contadorColumnas);
                                                    return new Token("opMod", "%", contadorLineas, contadorColumnas);

                                                }

                                            case '<':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMenorIgual", "<=", contadorLineas, contadorColumnas);
                                                    return new Token("opMenorIgual", "<=", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opMenor", "<", contadorLineas, contadorColumnas);
                                                    return new Token("opMenor", "<", contadorLineas, contadorColumnas);

                                                }


                                            case '>':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMayorIgual", ">=", contadorLineas, contadorColumnas);
                                                    return new Token("opMayorIgual", ">=", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opMayor", ">", contadorLineas, contadorColumnas);
                                                    return new Token("opMayor", ">", contadorLineas, contadorColumnas-1);

                                                }


                                            case '=':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opIgualIgual", "==", contadorLineas, contadorColumnas);
                                                    return new Token("opIgualIgual", "==", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opIgual", "=", contadorLineas, contadorColumnas);
                                                    return new Token("opIgual", "=", contadorLineas, contadorColumnas);

                                                }


                                            case '!':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opDiferente", "!=", contadorLineas, contadorColumnas);
                                                    return new Token("opDiferente", "!=", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opNot", "!", contadorLineas, contadorColumnas);
                                                    return new Token("opNot", "!", contadorLineas, contadorColumnas);

                                                }

                                            case '&':
                                                if (charSig == '&') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opAndLog", "&&", contadorLineas, contadorColumnas);
                                                    return new Token("opAndLog", "&&", contadorLineas, contadorColumnas);

                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opAndBit", "&", contadorLineas, contadorColumnas);
                                                    return new Token("opAndBit", "&", contadorLineas, contadorColumnas);

                                                }

                                            case '|':
                                                if (charSig == '|') {
                                                    avanzar();
                                                    //almacenarToken("opOr", "||", contadorLineas, contadorColumnas);
                                                    new Token("opOr", "||", contadorLineas, contadorColumnas);

                                                } else {
                                                    throw new ErrorLexico(contadorLineas, contadorColumnas,
                                                            "OPERADOR INVALIDO: |");
                                                }

                                        }

                                        //avanzar();
                                    }
                                    // IDENTIFICADORES: PRESERVADAS, METVAR --------------------------------------------------------------------
                                    else {
                                            if (Character.isLetter(charActual) | charActual == '_') {
                                                // ID METODO O VARIABLE, PALABRA RESERVADA: nombre, _nombre, Nombre

                                                lexema = "";

                                                while (Character.isLetter(charActual) | Character.isDigit(charActual) | charActual == '_') {
                                                    lexema += charActual;
                                                    avanzar();
                                                }

                                                if (!esFinArchivo(puntero) && !Character.isWhitespace(charActual) && !esDelimitador(charActual) && !esOperador(charActual)) {

                                                    lexemaError = lexema;

                                                    while (!esFinArchivo(puntero) && !Character.isWhitespace(charActual)) {
                                                        lexemaError += charActual;
                                                        avanzar();
                                                    }

                                                    // REPORTAR ERROR LEXICO: IDENTIFICADOR INCORRECTO: nombre_Edad*.4
                                                    throw new ErrorLexico(contadorLineas, contadorColumnas, "IDENTIFICADOR INCORRECTO: " + lexemaError);

                                                }
                                                else {
                                                    // 1. Verifico si es palabra reservada
                                                    if (Keywords.esPr(lexema)) {
                                                        // devuelvo la pr con + info
                                                        String tokenPr = Keywords.getToken(lexema);
                                                        if (tokenPr != null) {
                                                            //almacenarToken(tokenPr, lexema, contadorLineas, contadorColumnas-1);
                                                            return new Token(tokenPr, lexema, contadorLineas, contadorColumnas-1);
                                                        }


                                                    } else {

                                                        // 2. Si empieza por mayúscula es identificador de clase
                                                        if (Character.isUpperCase(lexema.charAt(0))) {
                                                            //avanzar();
                                                            //almacenarToken("idClass", lexema, contadorLineas, contadorColumnas-1);
                                                            return new Token("idClass", lexema, contadorLineas, contadorColumnas-1);

                                                            //3. Sino es identificador de metodo o variable
                                                        } else {
                                                            //avanzar();
                                                            //almacenarToken("idMetVar", lexema, contadorLineas, contadorColumnas-1);
                                                            return new Token("idMetVar", lexema, contadorLineas, contadorColumnas-1);

                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                    if (Character.isDigit(charActual)) {

                                                        lexema = "";

                                                        while (Character.isDigit(charActual)) {
                                                            // es un decimal
                                                            lexema += charActual;
                                                            avanzar();

                                                        }

                                                        if (!esFinArchivo(puntero) && charActual == '.' | (!Character.isWhitespace(charActual) && !esDelimitador(charActual) && !esOperador(charActual))) {
                                                            lexemaError = lexema;
                                                            while (!esFinArchivo(puntero) && !Character.isWhitespace(charActual)) {
                                                                lexemaError += charActual;
                                                                avanzar();
                                                            }

                                                            throw new ErrorLexico(contadorLineas, contadorColumnas - 1, "IDENTIFICADOR INCORRECTO: " + lexemaError);

                                                        } else {
                                                            //avanzar();
                                                            //almacenarToken("literal_entero", lexema, contadorLineas, contadorColumnas-1);
                                                            return new Token("literal_entero", lexema, contadorLineas, contadorColumnas-1);

                                                        }
                                                    }
                                                    // DELIMITADORES ---------------------------------------------------------------------------------------
                                                    else {
                                                            if (esDelimitador(charActual)) {

                                                                switch (charActual) {
                                                                    case '(':
                                                                        avanzar();
                                                                        //almacenarToken("parAbre", "(", contadorLineas, contadorColumnas);
                                                                        return new Token("parAbre", "(", contadorLineas, contadorColumnas);

                                                                    case ')':
                                                                        avanzar();
                                                                        //almacenarToken("parCierra", ")", contadorLineas, contadorColumnas);
                                                                        return new Token("parCierra", ")", contadorLineas, contadorColumnas);

                                                                    case '[':
                                                                        avanzar();
                                                                        //almacenarToken("corcheteAbre", "[", contadorLineas, contadorColumnas);
                                                                        return new Token("corcheteAbre", "[", contadorLineas, contadorColumnas);

                                                                    case ']':
                                                                        avanzar();
                                                                        //almacenarToken("corcheteCierra", "]", contadorLineas, contadorColumnas);
                                                                        return new Token("corcheteCierra", "]", contadorLineas, contadorColumnas);

                                                                    case '{':
                                                                        avanzar();
                                                                        //almacenarToken("llaveAbre", "{", contadorLineas, contadorColumnas);
                                                                        return new Token("llaveAbre", "{", contadorLineas, contadorColumnas);

                                                                    case '}':
                                                                        avanzar();
                                                                        //almacenarToken("llaveCierra", "}", contadorLineas, contadorColumnas);
                                                                        return new Token("llaveCierra", "}", contadorLineas, contadorColumnas);

                                                                    case ';':
                                                                        avanzar();
                                                                        //almacenarToken("ptoComa", ";", contadorLineas, contadorColumnas);
                                                                        return new Token("ptoComa", ";", contadorLineas, contadorColumnas);

                                                                    case ',':
                                                                        avanzar();
                                                                        //almacenarToken("coma", ",", contadorLineas, contadorColumnas);
                                                                        return new Token("coma", ",", contadorLineas, contadorColumnas);

                                                                    case ':':
                                                                        avanzar();
                                                                        //almacenarToken("dosPuntos", ":", contadorLineas, contadorColumnas);
                                                                        return new Token("dosPuntos", ":", contadorLineas, contadorColumnas);

                                                                    case '.':
                                                                        avanzar();
                                                                        //almacenarToken("pto", ".", contadorLineas, contadorColumnas);
                                                                        return new Token("pto", ".", contadorLineas, contadorColumnas);

                                                                }

                                                            }
                                                            else {
                                                                if (charActual == '\0') {
                                                                    return new Token("EOF", "", contadorLineas, contadorColumnas);
                                                                } else {
                                                                    throw new ErrorLexico(contadorLineas, contadorColumnas, "CARACTER DESCONOCIDO: " + charActual);
                                                                }

                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
        }
    }

    //almacena en un arraylist cada lexema-token-nrolinea-nrocolumna encontrado durante el analisis lexico
    // este es el metodo viejo que usabamos cuando teniamos el lexico solo
    /*
    private void almacenarToken(String token, String lexema, String linea, String columna){
        this.token.add("| " + token + " | " + lexema + " | " + "LINEA " + linea + " (COLUMNA " + columna + ") |");
    }*/
    // como necesito pasarle objetos token al sintactico esta es el metodo a usar
    private void almacenarToken(Token token){
        listaTokens.add(token);
    }

    //incrementa el numero de lineas
    private void incrementarLineas(){
        contadorLineas += 1;
    }

    //incrementa el numero de columnas
    private void incrementarColumnas(int cant){
        contadorColumnas += cant;
    }

    //reinicia el numero de columnas
    //se utiliza cuando hay un salto de linea
    private void reiniciarColumnas(){
        contadorColumnas = 0;
    }

    //incrementa el puntero y actualiza el caracter actual en casa de que no se haya consumido completamente el codigo fuente
    private void avanzar(){

        puntero += 1;
        if (esFinArchivo(puntero)){
            charActual = '\0';
            charSig = '\0';
        } else {
            incrementarColumnas(1);
            charActual = codFuente.charAt(puntero);

        }
    }

    // Actualiza el caracter siguiente en caso de que no haya llegado al final del codigo fuente
    private void actualizarCharSig(){

        if (esFinArchivo(puntero + 1)){
            charSig = '\0';
        }
        else {
            charSig = codFuente.charAt(puntero+1);
        }
    }

    //asigna el valor del primer caracter del codigo fuente a la cariable charActual
    private void inicializarCharActual(){
        if (!esFinArchivo()){
            charActual = codFuente.charAt(puntero);
        } else {
            charActual = '\0';
            charSig = '\0';
        }


    }

    //identifica si el parámetro recibido es un operador reconocido por la gramática
    private boolean esOperador(char c){
        if (c == '+' | c == '-' | c == '*' | c == '%' | c == '<' | c == '>' | c == '!' | c == '=' | c == '&' | c == '|'){
            return true;
        }
        return false;
    }

    //identifica si el parámetro recibido es un operador delimitador reconocido por la gramatica
    private boolean esDelimitador(char delim){
        if (delim == ')' | delim == '(' | delim == '[' | delim == ']' | delim == ';' | delim == ',' | delim == '{' | delim == '}' | delim == ':' | delim == '.'){
            return true;
        }
        return false;
    }

    //identifica si se ha consumido por completo el codigo fuente
    private boolean esFinArchivo(int punt){
        if (punt >= codFuente.length()){
            return true;
        }
        return false;
    }

    public boolean esFinArchivo() {
        if (this.puntero >= codFuente.length()) {
            return true;
        }
        return false;
    }

    //Solicita el next token al analizador y genera e imprime por pantalla una lista con cada uno de los lexemas identificados en el codigo fuente asi como también el token, número de linea y numero de columna correspondiente a cada lexema
    public void ejecutador() throws ErrorLexico {

        while (!esFinArchivo(puntero)){
            Token token = this.analizador();
            almacenarToken(token);

        }

        System.out.print("CORRECTO: ANALISIS LEXICO\n" +
                "| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |\n");

        for (Token t : listaTokens){
            System.out.println("| " + t.getTipo() + " | " + t.getLexema() +
                    " | LINEA " + t.getFila() + " (COLUMNA " + t.getColumna() + ") |");
        }
    }



    /*public List<Token> getTokens() {
        return listaTokens;
    }*/


}
