package semantico.registros;

import lexico.Token;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Hash con todos los metodos de una clase
public class RegistroMetodo {

    private int proxPosVarLocal = 0; // contador de las variables del metodo
    private int proxPosParametro = 0; // contador de los parametros del metodo

    public String nombre; // nombre del metodo
    private Token tokenMetodo;
    // forma del metodo puede ser st o no
    public boolean esEstatico; // true: estatico


    // cada metodo tiene una lista de parametros (que serian los argumentos formales)
    public Map<String, RegistroParametro> listaParametros;

    // lista variables locales
    public Map<String, RegistroVariable> listaVarLocales;

    // tipo de retorno del metodo
    public Tipo tipoRetorno;

    private boolean isConstructor;

    public RegistroMetodo(String nombre){
        this.nombre = nombre;
        this.esEstatico = false; // por default no es estatico
        this.tipoRetorno = null; // por default le ponemos retorno null, que seria void
        this.listaParametros = new HashMap<>();
        this.listaVarLocales = new HashMap<>();
    }

    //Constructor vacio para crear un metodo constructor
    public RegistroMetodo() {
        this.esEstatico = false; // por default no es estatico
        this.tipoRetorno = null; // por default le ponemos retorno null, que seria void
        this.listaParametros = new LinkedHashMap<>();
        this.listaVarLocales = new HashMap<>();
        this.isConstructor = true;
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
    public void setTokenMetodo(Token token){ this.tokenMetodo = token; }

    public String getNombre(){
        return this.nombre;
    }
    public Tipo getTipoRetorno(){
        return this.tipoRetorno;
    }
    public boolean getFormaMetodo(){
        return this.esEstatico;
    }
    public Token getTokenMetodo(){ return this.tokenMetodo; }
    public Map<String, RegistroVariable> getListaVarLocales() {
        return listaVarLocales;
    }
    public Map<String, RegistroParametro> getListaParametros() {
        return listaParametros;
    }
    public boolean isConstructor(){ return this.isConstructor; }

    // funcion que me imprime el metodo y sus parametros
    public void imprimirMetodo(RegistroMetodo metodo, RegistroClase claseActual){
        System.out.println("");
        System.out.println("Metodo:  "+metodo.getNombre()+", de la clase: "+claseActual.getNombre()+
                ", Tipo retorno: "+metodo.getTipoRetorno().getNombreTipo()+", Estatico: "+metodo.getFormaMetodo());
        // imprimo los parametros de ese metodo
        if (listaParametros != null && !listaParametros.isEmpty()){
            System.out.println("Parametros: ");
            for (RegistroParametro p : listaParametros.values()) {
                if (p.getTipo().getNombreTipo().equals("tArray")){
                    TipoArreglo tipoArray= (TipoArreglo) p.getTipo();
                    System.out.println("Posicion: "+p.getPos()+", tipo: "+ tipoArray.getNombreTipo()+ ", tipoInterno: "+ tipoArray.getTipoInterno().getNombreTipo() + ", Nombre: "+p.getNombre());
                } else {
                    System.out.println("Posicion: "+p.getPos()+", tipo: "+p.getTipo().getNombreTipo()+", Nombre: "+p.getNombre());
                }

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
                if (v.getTipo().getNombreTipo().equals("tArray")){
                    TipoArreglo tipoArray= (TipoArreglo) v.getTipo();
                    System.out.println("Posicion: "+v.getPos()+", tipo: "+ tipoArray.getNombreTipo()+ ", tipoInterno: "+ tipoArray.getTipoInterno().getNombreTipo() + ", Nombre: "+v.getNombre());
                } else {
                    System.out.println("Posicion: "+v.getPos()+", tipo: "+v.getTipo().getNombreTipo()+", Nombre: "+v.getNombre());
                }

                System.out.println("----");
            }
        }
        else {
            System.out.println("Sin variables locales");
        }
    }

    // funcion que me devuelve si un metodo ya esta definido con ese nombre para esa clase
    public boolean noEstaMetodo(RegistroClase clase, String nombreMetodo){
        if (clase.listaMetodos.containsKey(nombreMetodo)){
            return false; // ya esta el metodo
        }
        return true;
    }

}