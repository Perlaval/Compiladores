package sintactico;
import lexico.Token;
import lexico.Lexico;


// analizador sintactico
public class Sintactico {
    private List<Token> listaTokens; //Lista de tokens que obtuve del lexico
    private int puntero;
    private Token token;

    //constructor
    public Sintactico(List<Token>listaTokens){
        this.listaTokens = listaTokens;
        this.puntero = 0;
        this.token = listaTokens.get(0);
    }

    //clase
    public void analizador() throws ErrorSintactico {
        // Program -> ListaDefiniciones Start
        program();

    }

    // Gramatica ----------------------------------------------------------------------------------------------

    //Program -> ListaDefiniciones Start
    private void program() throws  ErrorSintactico{
        listaDefiniciones(); // si es lambda va directo a start
        start();
        match("$"); // ver si tiene que ser $ o EOF
    }

    // Start -> start BloqueMetodo
    private void start(){
        // matcheo start asi avanza
        match("prStart"); // esto tmb verificar porque nose si start era una palabra reservada (pregintar a profe)
        bloqueMetodo();
    }

    // ListaDefiniciones -> Clase ListaDefiniciones | Implementacion ListaDefiniciones | lambda
    private listaDefiniciones() throws  ErrorSintactico{
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
    private void clase() throws  ErrorSintactico{
        match("prClass");
        match("idClass");
        herenciaOpt(); //metodo creado a partir del ?, ya que puede o no estar
        match("llaveAbre");
        listaAtributos(); // si lo que viene es } es porque era lambda
        match("llaveCierra");
    }

    // HerenciaOpt -> Herencia | lambda
    // aca como puede ser opcional si va a herencia o no, necesito los primeros y siguientes
    private void herenciaOpt(){
        // Sig(HerenciaOpt) = { { }
        // si el token que viene no esta en los primeros de herencia es porque o vino {, entonces aca no hace nada, o vino algo mal
        // entonces verifico con los primeros
        if (esPrimeroHerencia(token.getTipo())){
            herencia();
        }
    }

    // ListaAtributos -> Atributo ListaAtributos | lambda
    private void listaAtributos(){
        // si lo que viene no esta en los primeros de Atributo es porque listaAtributos es lambda entonces aca no hace nada
        // es recursiva, por lo tanto siempre que venga alguno de los primeros de A vuelvo a entrar
        while (esPrimeroAtributo(token.getTipo())){
            atributo();
        }
    }

    // Impl -> impl idClass { ListaMiembros }
    private void impl() throws  ErrorSintactico{
        match("prImpl");
        match("idClass");
        match("llaveAbre");
        listaMiembros();
        match("llaveCierra");
    }

    // ListaMiembros -> Miembro ListaMiembros | lambda
    private void listaMiembros() throws  ErrorSintactico{
        // si lo que viene esta en los primeros de miembro es porque lista miembro no es lambda
        while (esPrimeroMiembro(token.getTipo())){
            miembro();
        }
    }

    // Herencia -> Tipo
    private herencia(){
        tipo();
    }

    // Miembro -> Metodo | Constructor
    private void miembro(){
        // tengo dos opciones o voy a metodo o voy a constructor
        if (esPrimeroMetodo(token.getTipo())){
            metodo();
        }
        else {
            constructor();
        }
    }

    // Metodo -> FormaMetodoOpt fn TipoMetodoOpt idMetAt ArgumentosFormales BloqueMetodo
    private void metodo() throws  ErrorSintactico{
        formaMetodoOpt();
        match("prFn");
        tipoMetodoOpt();
        match("idMetAt");
        argumentosFormales();
        bloqueMetodo();
    }

    // formaMetodoOpt -> formaMetodo | lambda
    private void formaMetodoOpt(){
        // si el token que viene esta en los primeros de formaMetodo tengo que entrar
        // si viene otra cosa no hace nada y si no viene nada no entra y es valido
        if (esPrimeroFormaMetodo(token.getTipo())){
            formaMetodo();
        }
    }

    // TipoMetodoOpt -> TipoMetodo | lambda
    private void tipoMetodoOpt(){
        // si el tokoen esta en los primeros de tipoMetodo entro
        if (esPrimeroTipoMetodo(token.getTipo())){
            tipoMetodo();
        }
    }

    // ArgumentosFormales -> ( ListaArgumentosFormalesOpt )
    private void argumentosFormales() throws  ErrorSintactico{
        match("parAbre");
        listaArgumentosFormalesOpt();
        match("parCierra");
    }

    // Constructor -> . ArgumentosFormales BloqueMetodo
    private void constructor() throws  ErrorSintactico{
        match("pto");
        argumentosFormales();
        bloqueMetodo();
    }

    // Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar ;
    private void atributo() throws  ErrorSintactico{
        visibilidadOpt();
        tipo();
        listaDeclaracionVar();
        match("ptoComa");
    }

    // VisibilidadOpt -> Visibilidad | lambda
    private void visibilidadOpt(){
        // si lo que viene esta en los primeros de visibilidad entro
        if (esPrimeroVisibilidad(token.getTipo())){
            visibilidad();
        }
    }

    //TipoMetodoOpt -> TipoMetodo | lambda
    private void tipoMetodoOpt(){
        if (esPrimeroTipoMetodo(token.getTipo())){
            tipoMetodo();
        }
    }

    // Tipo -> TipoPrimitivo | TipoReferencia | TipoArreglo
    private tipo(){
        // si lo que viene esta en los primeros de tipo primitivo entro ahi
        if (esPrimeroTipoPrimitivo(token.getTipo())){
            tipoPrimitivo();
        }
        else {
            if (esPrimeroTipoReferencia(token.getTipo())){
                tipoReferencia();
            }
            else {
                if (esPrimeroTipoArreglo(token.getTipo())){
                    tipoArreglo();
                }
            }
        }
    }

    // ListaDeclaracionVar -> idMetAt ListaDeclaracionesVarRec
    private void listaDeclaracionVar() throws  ErrorSintactico{
        match("idMetAt");
        listaDeclaracionVarRec();
    }

    // ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
    private void listaDeclaracionVarRec() throws  ErrorSintactico{
        match("coma");
        listaDeclaracionVar();
    }

    // BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
    private void bloqueMetodo() throws  ErrorSintactico{
        match("llaveAbre");
        listaDeclaracionVarLocal();
        listaSentencia();
        match("llaveCierra");

    }

    // ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal
    private void listaDeclaracionVarLocal(){
        // recursiva
        // si lo que viene esta en los primeros de declaracionVarLocal es porque no es lambda
        while (esPrimeroDeclaracionVarLocal(token.getTipo()){
            declaracionVarLocal();
        }
    }

    // Visibilidad -> pub
    private void visibilidad(){
        match("prVoid");
    }

    // FormaMetodo -> st
    private void formaMetodo(){
        match("prSt");
    }


    // Conjuntos de primeros --------------------------------------------------------------------------------

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

    // conjunto de primeros tipo referencia
    private boolean esPrimeroTipoReferencia(String tipo){
        // Prim(TipoReferencia) = {idClass}
        if (tipo == "idClass"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros tipo Arreglo
    private boolean esPrimeroTipoArreglo(String tipo){
        // Prim(TipoArreglo) = {Array}
        if (tipo == "tArray"){
            return true;
        }
        else {
            return false;
        }
    }


    // conjunto de primeros de visibilidad
    private boolean esPrimeroVisibilidad(String tipo){
        if (tipo == "prPub"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros de tipo metodo
    private boolean esPrimeroTipoMetodo(String tipo){
        // Prim(TipoMetodo) = {Str, BOol, Int, idClass, Array, lambda}
        if (tipo == "tStr" || tipo == "tBool" || tipo == "tInt" || tipo == "idClass" || tipo = "tArray"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto primeros forma metodo
    private boolean esPrimeroFormaMetodo(String tipo){
        // Prim(FormaMetodo) = {st}
        if (tipo == "prSt"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros de Metodo
    private boolean esPrimeroMetodo(String tipo){
        // Prim(Metodo): {st, lambda}
        if (tipo == "prSt"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros declaracion variable local
    private boolean esPrimeroDeclaracionVarLocal(String tipo){
        // Prim(DeclaracionVarLocal) = {Str, BOol, Int, idClass, Array, lambda}
        if (tipo == "tStr" || tipo == "tBool" || tipo == "tInt" || tipo == "idClass" || tipo = "tArray"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros miembro
    private boolean esPrimeroMiembro(String tipo){
        if (tipo == "prSt" || tipo == "pto"){
            return true;
        }
        else {
            return false;
        }
    }

    //conjunto de primeros atributo
    private boolean esPrimeroAtributo(String tipo){
        // Prim(Atributo) = {pub, lambda}
        if (tipo == "pub"){
            return true;
        }
        else {
            return false;
        }
    }

    // conjunto de primeros herenciaOpcional
    private boolean esPrimeroHerencia(String tipo) {
        // Prim(Herencia) = {Str, Bool, Int, idClass, Array}
        if (tipo == "tStr" || tipo == "tBool" || tipo == "tInt" || tipo == "idClass" || tipo = "tArray"){
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
            // solo avanzo si matcheo, en ninguna otra parte del codigo deberia avanzar
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
        if (puntero < listaTokens.size){
            token = listaTokens.get(puntero);
        }
    }


}