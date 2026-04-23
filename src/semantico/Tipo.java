package semantico;

public abstract class Tipo{
    public abstract String getNombre();

}

public class TipoBool extends Tipo{
    public String getNombre() {
        return "tBool";
    }
}

public class TipoInt extends Tipo {
    public String getNombre(){
        return "tInt";
    }
}

public class TipoStr extends Tipo {
    public String getNombre(){
        return "tStr";
    }
}

// este nose si seria asi
public class TipoNil extends Tipo {
    public String getNombre(){
        return "tNil";
    }
}

