package semantico.registros;

import semantico.registros.RegistroVariable;

import java.util.HashMap;
import java.util.Map;

public class RegistroStart {

    private int proxPosVarLocal = 0;

    // lista de las variables del metodo start
    public Map<String, RegistroVariable> listaVariables;


    public RegistroStart(){
        this.listaVariables = new HashMap<>();
    }

    public int getProxPosVarLocal(){
        return proxPosVarLocal++;
    }
}
