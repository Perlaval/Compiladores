package lexico;
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
        tabla.put("in", "prIn");
        tabla.put("do", "prDo");
        tabla.put("void", "prVoid");


        // tipos
        tabla.put("Int", "tInt");
        tabla.put("Bool","tBool");
        tabla.put("Str", "tStr");
        tabla.put("Array", "tArray");


    }

    public static boolean esPr(String lexema){
        return tabla.containsKey(lexema);
    }
    public static String getToken(String lexema){ return tabla.get(lexema);}
}
