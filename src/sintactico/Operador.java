package sintactico;

import lexico.Token;

public class Operador {

    public static boolean esOpComp(Token token) {
        return token.getTipo().equals("opMenor") || token.getTipo().equals("opMenorIgual")
                || token.getTipo().equals("opMayor") || token.getTipo().equals("opMayorIgual");
    }
    public static  boolean esOpAd(Token token){
        return token.getTipo().equals("opMas") || token.getTipo().equals("opMenos");
    }
    public static  boolean esOpMul(Token token){
        return token.getTipo().equals("opPor") || token.getTipo().equals("opdiv");
    }
    public static  boolean esOpUnario(Token token){
        return token.getTipo().equals("opMas") || token.getTipo().equals("opMenos") ||
                token.getTipo().equals("opMasMas") || token.getTipo().equals("opMenosMenos") || token.getTipo().equals("opNot");
    }
    public static boolean esOpIgual(Token token){
        return token.getTipo().equals("opIgualIgual") || token.getTipo().equals("opDiferente") ;
    }
}
