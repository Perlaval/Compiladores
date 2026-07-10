package sintactico;

import lexico.Token;

public class Operador {

    //---------------------
    // CONJUNTO OPERADORES
    //---------------------

    //------------------------------------------------------------------------------------------------------------------
    // OPERADOR COMPARACION
    //------------------------------------------------------------------------------------------------------------------
    public static boolean esOpComp(Token token) {
        return token.getTipo().equals("opMenor") || token.getTipo().equals("opMenorIgual")
                || token.getTipo().equals("opMayor") || token.getTipo().equals("opMayorIgual");
    }

    //------------------------------------------------------------------------------------------------------------------
    // OPERADOR AD
    //------------------------------------------------------------------------------------------------------------------
    public static boolean esOpAd(Token token){
        return token.getTipo().equals("opMas") || token.getTipo().equals("opMenos");
    }

    //------------------------------------------------------------------------------------------------------------------
    // OPERADOR MUL
    //------------------------------------------------------------------------------------------------------------------
    public static boolean esOpMul(Token token){
        return token.getTipo().equals("opPor") || token.getTipo().equals("opdiv");
    }

    //------------------------------------------------------------------------------------------------------------------
    // OPERADOR UNARIO
    //------------------------------------------------------------------------------------------------------------------
    public static boolean esOpUnario(Token token){
        return token.getTipo().equals("opMas") || token.getTipo().equals("opMenos") ||
                token.getTipo().equals("opMasMas") || token.getTipo().equals("opMenosMenos") || token.getTipo().equals("opNot");
    }

    //------------------------------------------------------------------------------------------------------------------
    // OPERADOR IGUAL
    //------------------------------------------------------------------------------------------------------------------
    public static boolean esOpIgual(Token token){
        return token.getTipo().equals("opIgualIgual") || token.getTipo().equals("opDiferente") ;
    }
}
