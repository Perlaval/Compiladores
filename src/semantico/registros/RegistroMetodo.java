package src.semantico.registros;

import src.semantico.tipos.Tipo;

import java.util.Map;

// Hash con todos los metodos de una clase
public class RegistroMetodo{
    // nombre del metodo
    public String nombre;

    // forma del metodo puede ser st o no
    // 0 estatico (st)
    // 1 no estatico
    public boolean formaMetodo;

    // cada metodo tiene una lista de parametros (que serian los argumentos formales)
    public Map<String, RegistroParametro> listaParametros;

    // lista variables locales
    public Map<String, RegistroVariable> listaVarLocales;

    // tipo de retorno del metodo
    public Tipo tipoRetorno;


}
