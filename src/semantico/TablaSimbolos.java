package semantico;

import semantico.registros.RegistroStart;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;

import java.util.HashMap;
import java.util.Map;

import static semantico.ValidarDeclaracion.Definicion.*;


public class TablaSimbolos implements ValidarDeclaracion{
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

    public RegistroClase getClaseActual() { return this.claseActual;}

    public void imprimirClases(){
        System.out.println("Clases guardadas en la lista de clases de la TS: ");
        for (RegistroClase c : tablaClases.values()) {
            System.out.println(c.getNombre());
        }
    }


    @Override
    public boolean validarNombre(Definicion def, String nombre) {
        if (def == METODO | def == VAR) {
            return !isNombreTipoEspecial(nombre);
        } else {
            return !isNombreClasePredefinida(nombre);
        }
    }

    @Override
    public boolean isNombreTipoEspecial(String nombre) {
        return nombre == "void" | nombre == "self";
    }

    @Override
    public boolean isNombreClasePredefinida(String nombre) {
        return nombre == "IO" | nombre == "Iterator" | nombre == "Array" | nombre == "Object";
        //en caso de que sea Int, Bool o Str no es necesario pq el lexico lo envia al sintactico como tInt, tBool y tStr
    }
}

// cada una de las clases Entrada... hacen referencia a la tabla con informacion de eso, por ejemplo la clase
// EntradaClase es la tabla que va a contener toda la info que contiene una clase. Seria como las mini bases de datos
// de cada una de las tablas internas que tiene la tabla de simbolos
