package semantico.registros;

import semantico.tipos.Tipo;

import java.util.HashMap;
import java.util.Map;

// Hash con todos los metodos de una clase
public class RegistroMetodo{

    private int proxPosVarLocal = 0; // contador de las variables del metodo
    private int proxPosParametro = 0; // contador de los parametros del metodo

    public String nombre; // nombre del metodo

    // forma del metodo puede ser st o no
    public boolean esEstatico; // true: estatico

    // cada metodo tiene una lista de parametros (que serian los argumentos formales)
    public Map<String, RegistroParametro> listaParametros;

    // lista variables locales
    public Map<String, RegistroVariable> listaVarLocales;

    // tipo de retorno del metodo
    public Tipo tipoRetorno;

    public RegistroMetodo(String nombre){
        this.nombre = nombre;
        this.esEstatico = false; // por default no es estatico
        this.tipoRetorno = null; // por default le ponemos retorno null, que seria void
        this.listaParametros = new HashMap<>();
        this.listaVarLocales = new HashMap<>();
    }

    public int getProxPosVarLocal(){
        return proxPosVarLocal++;
    }
    public int getProxPosParametro(){
        return proxPosParametro++;
    }
    public void setFormaMetodo(boolean estatico){
        this.esEstatico = estatico;
    }
    public void setTipoRetorno(Tipo t){
        this.tipoRetorno = t;
    }
    public String getNombre(){
        return this.nombre;
    }
    public Tipo getTipoRetorno(){
        return this.tipoRetorno;
    }
    public boolean getFormaMetodo(){
        return this.esEstatico;
    }

    // funcion que me imprime el metodo y sus parametros
    public void imprimirMetodo(RegistroMetodo metodo, RegistroClase claseActual){
        System.out.println("");
        System.out.println("Metodo:  "+metodo.getNombre()+", de la clase: "+claseActual.getNombre()+", Tipo retorno: "+metodo.getTipoRetorno().getNombre()+", Estatico: "+metodo.getFormaMetodo());
        // imprimo los parametros de ese metodo
        if (listaParametros != null && !listaParametros.isEmpty()){
            System.out.println("Parametros: ");
            for (RegistroParametro p : listaParametros.values()) {
                System.out.println("Posicion: "+p.getPos()+", tipo: "+p.getTipo().getNombre()+", Nombre: "+p.getNombre());
                System.out.println("----");
            }
        }
        else {
            System.out.println("Sin parametros");
        }
        // imprimo las variables locales del metodo
        if (listaVarLocales != null && !listaVarLocales.isEmpty()){
            System.out.println("Variables locales");
            for (RegistroVariable v : listaVarLocales.values()){
                System.out.println("Posicion "+v.getPos()+", tipo: "+v.getTipo().getNombre()+ ", Nombre: "+v.getNombre());
                System.out.println("----");
            }
        }
        else {
            System.out.println("Sin variables locales");
        }
    }
}