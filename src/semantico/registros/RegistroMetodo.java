package semantico.registros;

import semantico.tipos.Tipo;

import java.util.HashMap;
import java.util.Map;

// Hash con todos los metodos de una clase
public class RegistroMetodo{
    // nombre del metodo
    public String nombre;

    // forma del metodo puede ser st o no
    // true estatico (st)
    // false no estatico
    public boolean esEstatico;

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

    public void setFormaMetodo(boolean estatico){
        this.esEstatico = estatico;
    }
    public void setTipoRetorno(Tipo t){
        this.tipoRetorno = t;
    }
    public void setNombre(String n){
        this.nombre =  n;
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
    public void imprimirMetodo(RegistroMetodo metodo){
        System.out.println("");
        System.out.println("Metodo:  "+metodo.getNombre());
        System.out.println("Tipo de retorno del metodo: "+metodo.getTipoRetorno().getNombre());
        System.out.println("Estatico: "+metodo.getFormaMetodo());
        // imprimo los parametros de ese metodo
        if (listaParametros != null && !listaParametros.isEmpty()){
            System.out.println("");
            System.out.println("Parametros: ");
            System.out.println("");
            for (RegistroParametro p : listaParametros.values()) {
                System.out.println("Posicion: "+p.getPos());
                System.out.println("Tipo: "+p.getTipo().getNombre());
                System.out.println("Nombre: "+p.getNombre());

                System.out.println("----");
            }
        }
        else {
            System.out.println("Sin parametros");
        }

    }
}