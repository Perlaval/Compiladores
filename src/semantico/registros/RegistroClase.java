package semantico.registros;

import semantico.tipos.Tipo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroClase {

    private int proxPosAtributo = 0;

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

    public RegistroClase(String nombre) {
        this.nombre = nombre;
        //this.heredaDe = "Object"; // por default
        this.listaMetodos = new HashMap<>();
        this.listaAtributos = new HashMap<>();
        //this.constructor = null;

    }

    public int getProxPosAtributo(){
        return proxPosAtributo++;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setHeredaDe(String superClase) {
        this.heredaDe = superClase;
    }

    // metodo utilizado paea las clases predefinidas
    public void setListaMetodos(Map<String, RegistroMetodo> metodos){
        this.listaMetodos = metodos;
    }

    public String getHeredaDe() {
        return heredaDe;
    }

    public Map<String, RegistroAtributo> getListaAtributos() {
        return listaAtributos;
    }


}
