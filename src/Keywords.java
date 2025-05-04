import java.util.HashMap;

public class Keywords {
    private static final HashMap<String, String> tabla = new HashMap<>();


    public static void inicializar(){
        // palabras claves
        tabla.put("class", "pr");
        tabla.put("impl", "pr");
        tabla.put("else", "pr");
        tabla.put("false", "pr");
        tabla.put("if", "pr");
        tabla.put("ret", "pr");
        tabla.put("while", "pr");
        tabla.put("true", "pr");
        tabla.put("nil", "pr");
        tabla.put("new", "pr");
        tabla.put("fn", "pr");
        tabla.put("st", "pr");
        tabla.put("pub", "pr");
        tabla.put("self", "pr");
        tabla.put("div", "pr");

        //then, array

    }

    public static boolean esPr(String lexema){
        return tabla.containsKey(lexema);
    }
}
