package semantico.registros;

import lexico.Token;
import semantico.tipos.Tipo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegistroClase {

    private int proxPosAtributo = 0;

    private Token tokenClase;

    private boolean esPredefinida;

    //nombre de la clase
    public String nombre;

    // herencia voy a guardar el idClass de la clase a la que hereda
    // nose si nos conviene aca tener el objeto clase mas que el string
    public String heredaDe;

    // metodos de la clase
    // voy a guardar el nombre de la clase y sus metodos
    public Map<String, RegistroMetodo> listaMetodos;

    // atributos de la clase
    public Map<String, RegistroAtributo> listaAtributos;

    // constructor de la clase
    // es un metodo porque tmb va a tener parametros y var locales, y el tipo de retorno va a ser void
    public Constructor constructor;

    public boolean inConstructor = false;

    // utilizado para verificacion en la consolidacion de la TS
    public boolean declarada;

    // cada clase debe tener al menos un impl
    public boolean implementada;

    public RegistroClase(String nombre) {
        this.nombre = nombre;
        //this.heredaDe = "Object"; // por default
        this.listaMetodos = new LinkedHashMap<>();
        this.listaAtributos = new LinkedHashMap<>();
        //this.constructor = null;

    }

    public void setDeclarada(boolean esdeclarada){
        this.declarada = esdeclarada;
    }
    public void setTokenClase(Token token){ this.tokenClase = token; }
    public void setHeredaDe(String superClase) {
        this.heredaDe = superClase;
    }
    public void setEsPredefinida(boolean predefinida){ this.esPredefinida = predefinida; }
    public void setImplementada(boolean implementada){ this.implementada = implementada; }
    // metodo utilizado paea las clases predefinidas
    public void setListaMetodos(Map<String, RegistroMetodo> metodos){
        this.listaMetodos = metodos;
    }

    public boolean getDeclarada(){
        return this.declarada;
    }
    public boolean getImplementada() {return this.implementada; }
    public int getProxPosAtributo(){
        return proxPosAtributo++;
    }
    public String getNombre() {
        return this.nombre;
    }
    public String getHeredaDe() {
        return heredaDe;
    }
    public Map<String, RegistroAtributo> getListaAtributos() {
        return listaAtributos;
    }
    public Map<String, RegistroMetodo> getListaMetodos() {
        return listaMetodos;
    }
    public Token getTokenClase(){ return this.tokenClase; }
    public boolean getEsPredefinida(){return this.esPredefinida; }




}
