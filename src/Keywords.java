import java.util.HashMap;

public class Keywords {
    private static final HashMap<String, String> tabla = new HashMap<>();


    public static void inicializar(){
        // palabras claves
        tabla.put("class", "prClass");
        tabla.put("impl", "prImpl");
        tabla.put("else", "prElse");
        tabla.put("false", "prFalse");
        tabla.put("if", "prIf");
        tabla.put("ret", "prRet");
        tabla.put("while", "prWhile");
        tabla.put("true", "prTrue");
        tabla.put("nil", "prNil");
        tabla.put("new", "prNew");
        tabla.put("fn", "prFn");
        tabla.put("st", "prSt");
        tabla.put("pub", "prPub");
        tabla.put("self", "prSelf");
        tabla.put("div", "prDiv");
        tabla.put("for", "prFor");

        // tipos
        tabla.put("int", "tInt");
        tabla.put("double", "tDouble");
        tabla.put("boolean","tboolean");
        tabla.put("str", "tStr");
        tabla.put("array", "tArray");

        //then, array, int, str, double

    }

    public static boolean esPr(String lexema){
        return tabla.containsKey(lexema);
    }
    public static String getToken(String lexema){ return tabla.get(lexema);}
}
