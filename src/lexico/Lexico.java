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
    private boolean newLexema;
    private List<String> token;

    // agrego una lista de tokens para pasarle al sintactico
    private List<Token> listaTokens = new ArrayList<>();

    public Lexico (String codFuente) {
        this.codFuente = codFuente;
        this.contadorLineas = 1;
        this.contadorColumnas = 1;
        this.puntero = 0;
        this.newLexema = false;
        this.token = new ArrayList<>();
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

        //while (!esFinArchivo(puntero)){

            // ESPACIOS ------------------------------------------------------------------------------------------------
            if (Character.isWhitespace(charActual)){

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
                                        // REPORTAR ERROR: NO SE CERRO EL COMENTARIO MULTPLE
                                    }
                                    else {
                                        // charActual = '*' y charSig = '/'
                                        avanzar(); // charActual = '/'
                                        avanzar();
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
                                                    avanzar();
                                                    //almacenarToken("opMas", "+", contadorLineas, contadorColumnas);
                                                    return new Token("opMas", "+", contadorLineas, contadorColumnas);

                                                }
                                                // para los casos de opUniario que tiene ++ y --
                                                if (charSig == '+'){
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMasMas", "++", contadorLineas, contadorColumnas);
                                                    return new Token("opMasMas", "++", contadorLineas, contadorColumnas);
                                                    
                                                }
                                                break;

                                            case '-':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMenosIgual", "-=", contadorLineas, contadorColumnas);
                                                    return new Token("opMenosIgual", "-=", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    //almacenarToken("opMenos", "-", contadorLineas, contadorColumnas);
                                                    return new Token("opMenos", "-", contadorLineas, contadorColumnas);

                                                }
                                                if (charSig == '-'){
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMenosMenos", "--", contadorLineas, contadorColumnas);
                                                    return new Token("opMenosMenos", "--", contadorLineas, contadorColumnas);
                                                    
                                                }
                                                break;

                                            case '*':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opPorIgual", "*=", contadorLineas, contadorColumnas);
                                                    return new Token("opPorIgual", "*=", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    //almacenarToken("opPor", "*", contadorLineas, contadorColumnas);
                                                    return new Token("opPor", "*", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '%':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opModIgual", "%=", contadorLineas, contadorColumnas);
                                                    return new Token("opModIgual", "%=", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    //almacenarToken("opMod", "%", contadorLineas, contadorColumnas);
                                                    return new Token("opMod", "%", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '<':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMenorIgual", "<=", contadorLineas, contadorColumnas);
                                                    return new Token("opMenorIgual", "<=", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    //almacenarToken("opMenor", "<", contadorLineas, contadorColumnas);
                                                    return new Token("opMenor", "<", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '>':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opMayorIgual", ">=", contadorLineas, contadorColumnas);
                                                    return new Token("opMayorIgual", ">=", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opMayor", ">", contadorLineas, contadorColumnas);
                                                    return new Token("opMayor", ">", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '=':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    //almacenarToken("opIgualIgual", "==", contadorLineas, contadorColumnas);
                                                    return new Token("opIgualIgual", "==", contadorLineas, contadorColumnas);
                                                
                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opIgual", "=", contadorLineas, contadorColumnas);
                                                    return new Token("opIgual", "=", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '!':
                                                if (charSig == '=') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opDiferente", "!=", contadorLineas, contadorColumnas);
                                                    return new Token("opDiferente", "!=", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    avanzar();
                                                    //almacenarToken("opNot", "!", contadorLineas, contadorColumnas);
                                                    returnn new Token("opNot", "!", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '&':
                                                if (charSig == '&') {
                                                    avanzar();
                                                    avanzar();
                                                    //almacenarToken("opAndLog", "&&", contadorLineas, contadorColumnas);
                                                    return new Token("opAndLog", "&&", contadorLineas, contadorColumnas);
                                                    
                                                } else {
                                                    avanzar();
                                                    //System.out.println(charActual);
                                                    //almacenarToken("opAndBit", "&", contadorLineas, contadorColumnas);
                                                    return new Token("opAndBit", "&", contadorLineas, contadorColumnas);

                                                }
                                                break;

                                            case '|':
                                                if (charSig == '|') {
                                                    //almacenarToken("opOr", "||", contadorLineas, contadorColumnas);
                                                    new Token("opOr", "||", contadorLineas, contadorColumnas);
                                                    avanzar();
                                                } else {
                                                    throw new ErrorLexico(contadorLineas, contadorColumnas,
                                                            "OPERADOR INVALIDO: |");
                                                }
                                                break;

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

                                                            //almacenarToken("idClass", lexema, contadorLineas, contadorColumnas-1);
                                                            return new Token("idClass", lexema, contadorLineas, contadorColumnas-1);

                                                            //3. Sino es identificador de metodo o variable
                                                        } else {

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
                                                            //REPORTAR ERROR LEXICO IDENTIFICADOR INCORRECTO

                                                        } else {

                                                            //almacenarToken("literal_entero", lexema, contadorLineas, contadorColumnas-1);
                                                            return new Token("literal_entero", lexema, contadorLineas, contadorColumnas-1);

                                                        }
                                                    }
                                                    // DELIMITADORES ---------------------------------------------------------------------------------------
                                                    else {
                                                            if (esDelimitador(charActual)) {

                                                                Token token;

                                                                switch (charActual) {
                                                                    case '(':
                                                                        avanzar();
                                                                        //almacenarToken("parAbre", "(", contadorLineas, contadorColumnas);
                                                                        token = new Token("parAbre", "(", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case ')':
                                                                        avanzar();
                                                                        //almacenarToken("parCierra", ")", contadorLineas, contadorColumnas);
                                                                        token = new Token("parCierra", ")", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case '[':
                                                                        //almacenarToken("corcheteAbre", "[", contadorLineas, contadorColumnas);
                                                                        token = new Token("corcheteAbre", "[", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case ']':
                                                                        //almacenarToken("corcheteCierra", "]", contadorLineas, contadorColumnas);
                                                                        token = new Token("corcheteCierra", "]", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case '{':
                                                                        //almacenarToken("llaveAbre", "{", contadorLineas, contadorColumnas);
                                                                        token = new Token("llaveAbre", "{", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case '}':
                                                                        //almacenarToken("llaveCierra", "}", contadorLineas, contadorColumnas);
                                                                        token = new Token("llaveCierra", "}", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case ';':
                                                                        //almacenarToken("ptoComa", ";", contadorLineas, contadorColumnas);
                                                                        token = new Token("ptoComa", ";", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case ',':
                                                                        //almacenarToken("coma", ",", contadorLineas, contadorColumnas);
                                                                        token = new Token("coma", ",", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case ':':
                                                                        //almacenarToken("dosPuntos", ":", contadorLineas, contadorColumnas);
                                                                        token = new Token("dosPuntos", ":", contadorLineas, contadorColumnas);
                                                                        break;
                                                                    case '.':
                                                                        //almacenarToken("pto", ".", contadorLineas, contadorColumnas);
                                                                        token = new Token("pto", ".", contadorLineas, contadorColumnas);
                                                                }

                                                                avanzar();
                                                                return token;
                                                            }
                                                            else {
                                                                throw new ErrorLexico(contadorLineas, contadorColumnas, "CARACTER DESCONOCIDO: " + charActual);
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
        //}
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
        }
        else {
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
        charActual = codFuente.charAt(puntero);
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

    //Imprime por pantalla una lista con cada uno de los lexemas identificados en el codigo fuente asi como también el token, número de linea y numero de columna correspondiente a cada lexema
    /*
    public void ejecutador(){
        System.out.print("CORRECTO: ANALISIS LEXICO\n" + "| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |\n");
        for (String s : token){
            System.out.println(s);
        }
    }*/

   private Token nextToken(){

   }

    public void ejecutador(){
        //Pide next token
        //El analizador crea el token y se lo envia al ejecutador
        //Cada vez que el ejecutador pide next token el analizador avanza

        System.out.print("CORRECTO: ANALISIS LEXICO\n" +
                "| TOKEN | LEXEMA | NUMERO DE LINEA (NUMERO DE COLUMNA) |\n");

        

        while (!esFinArchivo(puntero)){
            Token token = analizador();
            almacenarToken(token);

        }

        for (Token t : listaTokens){
            System.out.println("| " + t.getTipo() + " | " + t.getLexema() +
                    " | LINEA " + t.getFila() + " (COLUMNA " + t.getColumna() + ") |");
        }
        listaTokens.add(new Token("EOF", "", contadorLineas, contadorColumnas));
    }
    public List<Token> getTokens() {
        return listaTokens;
    }


}
