package semantico;

import semantico.registros.RegistroStart;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;

import java.util.HashMap;
import java.util.Map;


public class TablaSimbolos{
    // Hash con las clases
    public Map<String, RegistroClase> tablaClases;

    // clase actual
    public RegistroClase claseActual;

    // metodo actual
    public RegistroMetodo metodoActual;

    // start
    public RegistroStart bloqueStart;

    // clases predefinidas (IO, Object, Str, Bool, Int, Iterator)


    public TablaSimbolos() {
        this.tablaClases = new HashMap<>();
        //this.inicializarClasesPredefinidas();
    }

    // metodo para obtener la clase
    public RegistroClase getClase(String nombre){
        return tablaClases.get(nombre);
    }

    public void imprimirClases(){
        System.out.println("Clases guardadas en la lista de clases de la TS: ");
        for (RegistroClase c : tablaClases.values()) {
            System.out.println(c.getNombre());
        }
    }


}

// cada una de las clases Entrada... hacen referencia a la tabla con informacion de eso, por ejemplo la clase
// EntradaClase es la tabla que va a contener toda la info que contiene una clase. Seria como las mini bases de datos
// de cada una de las tablas internas que tiene la tabla de simbolos