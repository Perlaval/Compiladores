import java.util.ArrayList;
import java.util.List;

public class Lexico {
    private int contadorLineas;
    private int contadorColumnas;
    private int puntero;
    private String codFuente;
    private char charActual;
    private char charSig;
    private List<String> token;

    public Lexico (String codFuente) {
        this.codFuente = codFuente;
        this.contadorLineas = 1;
        this.contadorColumnas = 1;
        this.puntero = 0;
        this.token = new ArrayList<>();
    }

    public void analizador() throws ErrorLexico{

        //Inicializo la tabla de palabras reservadas
        Keywords.inicializar();

        String lexema;
        String lexemaError;
        boolean flagPto = false;

        //1. Analizamos el codigo fuente y evaluamos cada uno de los casos
        //int longitud = codFuente.length();
        int tab = 8; // defino esta variable estableciendo que un tab son 8 posiciones

        inicializarCharActual();

        while (!esFinArchivo(puntero)){

            // ESPACIOS ------------------------------------------------------------------------------------------------
            if (Character.isWhitespace(charActual)){

                if ((int) charActual == 32 | (int) charActual == 11){ // ' ', \v
                    avanzar();

                } else if ((int) charActual == 10) { // \n : salto de linea en Linux
                    incrementarLineas();
                    reiniciarColumnas();
                    avanzar();

                // REVISAR ------------------------------------------
                } else if ((int) charActual == 9) { // \t : tab (8 posiciones)
                    //aumentamos un tab
                    int incrementoCol = (tab - (this.contadorColumnas % tab));
                    incrementarColumnas(incrementoCol);

                } else if ((int) charActual == 13) { // \r: va combinado con \n para hacer el salto de linea en WIndows, no incrementa columnas
                    avanzar();
                }

            //  COMENTARIOS --------------------------------------------------------------------------------------------
            } else if (charActual == '/') {

                actualizarCharSig();

                // 1. COMENTARIO SIMPLE
                if (charSig == '/'){
                    avanzar(); // incremento puntero, incremento columnas, cambio charActual
                    //actualizarCharSig();
                    while (!esFinArchivo(puntero) && (int) charActual != 10 && (int) charActual != 13){
                        avanzar();
                        //actualizarCharSig();
                    }

                // 2. COMENTARIO MULTIPLE
                } else if (charSig == '*') { // /*
                    avanzar(); // charActual = '*'
                    avanzar();
                    actualizarCharSig();
                    while (!esFinArchivo(puntero) && charActual != '*' && charSig != '/'){
                        if ((int) charActual == 13 && (int) charSig == 10){
                            //hay un salto de lineas: incremento lineas y reinicio columnas
                            avanzar(); // (int) charActual = 10
                            incrementarLineas();
                            reiniciarColumnas();
                        } else if (charActual == 10) {
                            incrementarLineas();
                            reiniciarColumnas();
                        }

                        avanzar();
                        actualizarCharSig();
                    }

                    if (esFinArchivo(puntero)){
                        throw new ErrorLexico(contadorLineas, contadorColumnas, "NO SE CERRO EL COMENTARIO MULTIPLE");
                        // REPORTAR ERROR: NO SE CERRO EL COMENTARIO MULTPLE
                    }else{
                        // charActual = '*' y charSig = '/'
                        avanzar(); // charActual = '/'
                        avanzar();
                    }

                // OPERADOR /= -----------------------------------------------------------------------------------------
                } else if (charSig == '=') {
                    almacenarToken("div1 ", "/=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                    avanzar();
                }

            // CADENAS DE TEXTO ----------------------------------------------------------------------------------------
            } else if ((int) charActual == 34){

                // Comillas dobles, almacena strings


                lexema = "" + charActual;

                avanzar();
                actualizarCharSig();


                while (!esFinArchivo(puntero) && (int) charActual != 34){

                    if (((int) charActual == 13 && (int) charSig == 10) | (int) charActual == 10 ) {
                        //hay un salto de lineas: incremento lineas y reinicio columnas
                        avanzar();
                        lexema += " ";
                        incrementarLineas();
                        reiniciarColumnas();

                    } else if ((int) charActual == 10) {

                        lexema += " ";
                        incrementarLineas();
                        reiniciarColumnas();

                    }else{
                        lexema += charActual; // se puede o no agregar el salto a la cadena (no lo agrego)
                    }

                    avanzar();
                    actualizarCharSig();
                }

                if (esFinArchivo(puntero)){
                    throw new ErrorLexico(contadorLineas, contadorColumnas, "NO SE CERRÓ LA CADENA DE CARACTERES");
                    // ERROR LEXICO: NO SE CERRO LA CADENA DE CARACTERES
                }else{
                    lexema += charActual;
                    almacenarToken("Str", lexema, String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                    avanzar();
                    // revisar si se muestra con comillas o sin comillas
                }

            // OPERADORES ARITMÉTICOS Y DE DECREMENTO ------------------------------------------------------------------
            } else if (esOperador(charActual)) {

                actualizarCharSig();

                switch (charActual){
                    case '+':
                        if (charSig == '='){
                            almacenarToken("masIgual", "+=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("mas","+", String.valueOf(contadorLineas), String.valueOf(contadorColumnas) );
                        }
                        break;

                    case '-':
                        if (charSig == '='){
                            almacenarToken("menosIgual", "-=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("menos", "-", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '*':
                        if (charSig == '='){
                            almacenarToken("porIgual", "*=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("por", "*", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '%':
                        if (charSig == '='){
                            almacenarToken("modIgual", "%=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("mod", "%", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '<':
                        if (charSig == '=') {
                            almacenarToken("menorIgual", "<=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("menor", "<", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '>':
                        if (charSig == '=') {
                            almacenarToken("mayorIgual", ">=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        } else {
                            almacenarToken("mayor", ">", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '=':
                        if (charSig == '=') {
                            almacenarToken("igualIgual", "==", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("igual", "=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '!':
                        if (charSig == '=') {
                            almacenarToken("diferente", "!=", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            almacenarToken("not", "!", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    case '&' :
                        if (charSig == '&'){
                            almacenarToken("andLog", "&&", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                            avanzar();
                        }else{
                            System.out.println(charActual);
                            almacenarToken("andBit", "&", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        }
                        break;

                    default:
                        almacenarToken("or", "|", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));

                }

                avanzar();

            // IDENTIFICADORES: PRESERVADAS, METVAR --------------------------------------------------------------------
            } else if (Character.isLetter(charActual) | charActual == '_') {
                // ID METODO O VARIABLE, PALABRA RESERVADA: nombre, _nombre, Nombre

                lexema = "";

                while (Character.isLetter(charActual) | Character.isDigit(charActual) | charActual == '_'){
                    lexema += charActual;
                    avanzar();
                }


                if (!esFinArchivo(puntero) && !Character.isWhitespace(charActual) && !esDelimitador(charActual) && !esOperador(charActual)){

                    lexemaError = lexema;

                    while (!esFinArchivo(puntero) && !Character.isWhitespace(charActual)){
                        lexemaError += charActual;
                        avanzar();
                    }


                    throw new ErrorLexico(contadorLineas, contadorColumnas, "IDENTIFICADOR INCORRECTO: " + lexemaError);

                    // REPORTAR ERROR LEXICO: IDENTIFICADOR INCORRECTO: nombre_Edad*.4


                }else{
                    // 1. Verifico si es palabra reservada
                    if (Keywords.esPr(lexema)){
                        almacenarToken("pr", lexema, String.valueOf(contadorLineas), String.valueOf(contadorColumnas-1));

                    } else {

                        // 2. Si empieza por mayúscula es identificador de clase
                        if (Character.isUpperCase(lexema.charAt(0))) {

                            almacenarToken("idClass", lexema, String.valueOf(contadorLineas), String.valueOf(contadorColumnas-1));

                            //3. Sino es identificador de metodo o variable
                        } else{

                            almacenarToken("idMetVar", lexema, String.valueOf(contadorLineas), String.valueOf(contadorColumnas-1));

                        }
                    }
                }
            } else if (Character.isDigit(charActual)) {

                lexema = "";

                while (Character.isDigit(charActual) | (charActual == '.' && !flagPto)){
                    // es un decimal
                    if (charActual == '.'){
                        flagPto = true;
                    }
                    lexema += charActual;
                    avanzar();

                }

                if (charActual == '.' | (!Character.isWhitespace(charActual) && !esDelimitador(charActual) && !esOperador(charActual))){
                    lexemaError = lexema;
                    while (!esFinArchivo(puntero) && !Character.isWhitespace(charActual)){
                        lexemaError += charActual;
                        avanzar();
                    }

                    throw new ErrorLexico(contadorLineas, contadorColumnas-1, "IDENTIFICADOR INCORRECTO: " + lexemaError);
                    //REPORTAR ERROR LEXICO IDENTIFICADOR INCORRECTO

                } else {

                    if (flagPto && !esDelimitador(charActual)){
                        almacenarToken("double", lexema, String.valueOf(contadorLineas), String.valueOf(contadorColumnas-1));
                    } else {
                        almacenarToken("int", lexema, String.valueOf(contadorLineas), String.valueOf(contadorColumnas-1));
                    }

                    flagPto = false;
                }

                // DELIMITADORES ---------------------------------------------------------------------------------------
            } else if (esDelimitador(charActual)) {

                switch (charActual){
                    case '(':
                        almacenarToken("parDer", "(", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case ')':
                        almacenarToken("parIzq", ")", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case '[':
                        almacenarToken("corcheteDer", "[", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case ']':
                        almacenarToken("corcheteIzq", "]", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case '{':
                        almacenarToken("llaveDer", "{", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case '}':
                        almacenarToken("llaveIzq", "}", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case ';':
                        almacenarToken("ptoComa", ";", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                    case ',':
                        almacenarToken("coma", ",", String.valueOf(contadorLineas), String.valueOf(contadorColumnas));
                        break;
                }

                avanzar();

            } else {
                throw new ErrorLexico(contadorLineas, contadorColumnas, "CARACTER DESCONOCIDO: " + charActual);
            }
        }
    }

    public void ejecutador(){
        System.out.print("CORRECTO: ANALISIS LEXICO\n" + "| TOKEN | LEXEMA | NÚMERO DE LÍNEA (NÚMERO DE COLUMNA) |\n");
        for (String s : token){
            System.out.println(s);
        }
    }

    private void almacenarToken(String token, String lexema, String linea, String columna){
        this.token.add("| " + token + " | " + lexema + " | " + "LINEA " + linea + " (COLUMNA " + columna + ") |");
    }

    private void incrementarLineas(){
        contadorLineas += 1;
    }

    private void incrementarColumnas(int cant){
        contadorColumnas += cant;
    }

    private void reiniciarColumnas(){
        contadorColumnas = 0;
    }

    private void avanzar(){

        puntero += 1;
        if (esFinArchivo(puntero)){
            charActual = '\0';
        }else{
            incrementarColumnas(1);
            charActual = codFuente.charAt(puntero);
        }


    }

    private void actualizarCharSig(){

        if (esFinArchivo(puntero + 1)){
            charSig = '\0';
        } else {
            charSig = codFuente.charAt(puntero+1);
        }
    }

    private void inicializarCharActual(){
        charActual = codFuente.charAt(puntero);
    }

    private boolean esOperador(char c){
        if (c == '+' | c == '-' | c == '*' | c == '%' | c == '<' | c == '>' | c == '!' | c == '=' | c == '&' | c == '|'){
            return true;
        }
        return false;
    }

    private boolean esDelimitador(char delim){
        if (delim == ')' | delim == '(' | delim == '[' | delim == ']' | delim == ';' | delim == ',' | delim == '{' | delim == '}'){
            return true;
        }
        return false;
    }

    private boolean esFinArchivo(int punt){
        if (punt >= codFuente.length()){
            return true;
        }
        return false;
    }


}
