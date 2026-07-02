package semantico;

import semantico.registros.*;
import semantico.tipos.*;

import javax.imageio.plugins.tiff.ExifInteroperabilityTagSet;
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

    public TablaSimbolos() {
        this.tablaClases = new HashMap<>();
        //clases predefinidas
        this.inicializarClasesPredefinidas();
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
            System.out.println("Clase: "+c.getNombre());
            System.out.println("Métodos:");
            for (RegistroMetodo m : c.listaMetodos.values()){
                m.imprimirMetodo(m,c);

            }
            System.out.println("");
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
        return nombre.equals("IO") | nombre.equals("Iterator") | nombre.equals("Array") | nombre.equals("Object") |
                nombre.equals("Str") | nombre.equals("Int") | nombre.equals("Bool");
        //en caso de que sea Int, Bool o Str no es necesario pq el lexico lo envia al sintactico como tInt, tBool y tStr
    }

    // metodo para verificar que una clase no esta
    public boolean noEstaTs(String id){
        //System.out.println("HOLAA CLASE " + claseActual.getNombre());

        //1. Verifico si la var esta en el met actual
        if (this.metodoActual != null){
            System.out.println("ENTRO A METODO");
            System.out.println("VARIABLE: " + id);
            // 1.1.2 Verifico si el metodo tiene variable local / parametro con ese nombre
            if (this.metodoActual.listaParametros.containsKey(id) | this.metodoActual.listaVarLocales.containsKey(id)){
                return false;
            }

        }
        //2. Si no esta en el met Actual lo busco en la clase Actual como un atr visible
        if (this.claseActual != null){
           if (this.claseActual.listaAtributos.containsKey(id)){
                //System.out.println("CLASE ACTUAL NO ES NULL LINEA 79 " + id);
                RegistroAtributo atr = this.claseActual.listaAtributos.get(id);

                    return !atr.isVisibilidad();

            }
        }

        return !(this.tablaClases.containsKey(id));

    }
    public boolean noEstaMetodoTs(String nombreMetodo){
        RegistroMetodo metodo = this.claseActual.listaMetodos.get(nombreMetodo);
        return (metodo == null);

        /*RegistroAtributo atr = clase.listaAtributos.get(id);
        if (atr == null){
            return true;
        }
        return !atr.isVisibilidad();*/
    }

    public RegistroVariable getVariable(String id){

        //1. Buscos en el met actual
        if (this.metodoActual.listaParametros.containsKey(id)){
            return this.metodoActual.listaParametros.get(id);
        }

        if (this.metodoActual.listaVarLocales.containsKey(id)){
            return this.metodoActual.listaVarLocales.get(id);
        }

        //2. Busco en la clase actual
        if (this.claseActual.listaAtributos.containsKey(id)){

            return this.claseActual.listaAtributos.get(id);
        }

        return null;
    }

    //NO LO USO
    public RegistroVariable getAtrDeClase(RegistroClase clase, String id){
    if (clase.listaAtributos.containsKey(id)){
        return clase.listaAtributos.get(id);
    }
    return null;

}


    // metodo para validar herencia
    public boolean herenciaValida(String id){
        if (id.equals("Int") || id.equals("tStr") || id.equals("tBool") || id.equals("IO") || id.equals("Iterator") || id.equals("Array")){
            return false;
        }
        return true;
    }

    // creo un registro de clase
    public RegistroClase crearRegClase(String id, String herencia){
        RegistroClase clase = new RegistroClase(id);
        clase.setHeredaDe(herencia);
        return clase;

    }

    // creo un registro de un parametro
    public RegistroParametro crearRegParametros(String id, Tipo tipo){
        RegistroParametro param = new RegistroParametro(id);
        param.setTipo(tipo);
        return param;
    }

    // creo un registro de metodo de una clase
    public RegistroMetodo crearRegMetodo(String id, boolean forma, Tipo tipoRetorno){
        RegistroMetodo metodo = new RegistroMetodo(id);
        metodo.setFormaMetodo(forma);
        metodo.setTipoRetorno(tipoRetorno);
        return metodo;
    }

    // guardo una clase en la TS
    public void guardar(RegistroMetodo metodo, RegistroParametro parametro, RegistroClase clase){
        if (parametro != null){
            metodo.listaParametros.put(parametro.getNombre(), parametro);
        }
        // si parametro es null ya inicializo en en registro del metodo a la lista vacia
        clase.listaMetodos.put(metodo.getNombre(), metodo);
    }

    // metodo que verifica que todos los impl tengan su clase antes de pasar a la consolidacion
    public void consolidar() throws ErrorSemantico {
        for (RegistroClase clase : tablaClases.values()) {
            if (clase.getEsPredefinida()){
                continue;
            }
            verificarDeclarada(clase);
            verificarImplementada(clase);
            verificarConstructor(clase);

            String padre = clase.getHeredaDe();
            verificarHerenciaDeclarada(clase, padre); // validacion de que las herencias esten declaradas

            consolidarAtributos(clase);
            consolidarMetodos(clase);

        }
    }

    public void verificarDeclarada(RegistroClase clase) throws ErrorSemantico{
        if (!clase.getDeclarada()) {
            throw new ErrorSemantico(clase.getTokenClase().getFila(), clase.getTokenClase().getColumna(),
                    "La clase " + clase.getNombre() + " no fue declarada");
        }
    }
    public void verificarImplementada(RegistroClase clase) throws ErrorSemantico{
        if (!clase.getImplementada()){
            throw new ErrorSemantico(clase.getTokenClase().getFila(), clase.getTokenClase().getColumna(),
                    "La clase "+clase.getNombre()+" debe tener al menos un impl");
        }
    }
    public void verificarConstructor(RegistroClase clase) throws ErrorSemantico{
        if (!clase.inConstructor){
            throw new ErrorSemantico(clase.getTokenClase().getFila(), clase.getTokenClase().getColumna(),
                    "La clase "+clase.getNombre()+" no posee constructor");
        }
    }
    public void verificarHerenciaDeclarada(RegistroClase clase, String padre) throws ErrorSemantico{
        if (padre != null && !tablaClases.containsKey(padre)){
            throw new ErrorSemantico(clase.getTokenClase().getFila(), clase.getTokenClase().getColumna(),
                    "La clase "+padre+" no fue declarada");
        }
        // le asigno object si no tiene herencia declarada
        if (padre == null){
            clase.setHeredaDe("Object");
        }
    }
    public void consolidarAtributos(RegistroClase clase) throws ErrorSemantico {
        // si no tiene padre no hago nada
        if (!clase.heredaDe.equals("Object")){
            RegistroClase padre = tablaClases.get(clase.getHeredaDe());
            int desplazamiento = padre.getListaAtributos().size();
            // actualizo la pos de los atributos del hijo
            for (RegistroAtributo atributohijo : clase.getListaAtributos().values()){
                atributohijo.setPos(atributohijo.getPos() + desplazamiento);
            }
            // agrego los atributos del padre al hijo
            for (RegistroAtributo atributo : padre.getListaAtributos().values()) {
                // si el atributo ya esta en la clase hija error, sino lo agrego
                if (clase.getListaAtributos().containsKey(atributo.getNombre())) {
                    throw new ErrorSemantico(clase.getTokenClase().getFila(), clase.getTokenClase().getColumna(),
                            "Atributo " + atributo.getNombre() + " redefinido en la clase " + clase.getNombre());
                }
                clase.getListaAtributos().put(atributo.getNombre(), atributo);
            }
        }
    }
    public void consolidarMetodos(RegistroClase clase){
        // la subclase hereda todos los metodos de la superclase
        // si un metodo de instancia tiene el mismo nombre que uno heredado lo puedo reescribir solo si:
            // - misma cantidad de parametros
            // - mismos tipos de parametros
            // - el tipo de retorno es igual
        // los metodos estaticos no se sobreescriben
    }

    // Inicializar clases predefinidas
    public void inicializarClasesPredefinidas() {
        // Clase Object, no posee ni metodos ni atributos
        RegistroClase claseObject = crearRegClase("Object", null);
        claseObject.setDeclarada(true);
        claseObject.setEsPredefinida(true);
        this.tablaClases.put(claseObject.getNombre(), claseObject);

        // --------------------------------------------------------------------------------------
        // Clase IO, contiene metodos utiles de E/S
        RegistroClase claseIO = crearRegClase("IO", "Object");

        // st fn out_str(Str s):
        RegistroMetodo metodoStr = crearRegMetodo("out_str", true, new TipoVoid());
        RegistroParametro paramStr = crearRegParametros("s", new TipoPrimitivo("String"));
        guardar(metodoStr, paramStr, claseIO);

        // st fn out_int(Int i):
        RegistroMetodo metodoInt = crearRegMetodo("out_int", true, new TipoVoid());
        RegistroParametro paramInt = crearRegParametros("i", new TipoPrimitivo("Int"));
        guardar(metodoInt, paramInt, claseIO);

        // st fn out_bool(Bool b):
        RegistroMetodo metodoBool = crearRegMetodo("out_bool", true, new TipoVoid());
        RegistroParametro paramBool = crearRegParametros("b", new TipoPrimitivo("Bool"));
        guardar(metodoBool, paramBool, claseIO);

        // st fn out_array_int(Array Int a):
        RegistroMetodo metodoAInt = crearRegMetodo("out_array_int", true, new TipoVoid());
        RegistroParametro paramAInt = crearRegParametros("a", new TipoArreglo(new TipoPrimitivo("Int")));
        guardar(metodoAInt, paramAInt, claseIO);

        // st fn out_array_str(Array Str a):
        RegistroMetodo metodoAStr = crearRegMetodo("out_array_str", true, new TipoVoid());
        RegistroParametro paramAStr = crearRegParametros("a", new TipoArreglo(new TipoPrimitivo("Str")));
        guardar(metodoAStr, paramAStr, claseIO);

        // st fn out_array bool(Array Bool a):
        RegistroMetodo metodoABool = crearRegMetodo("out_array_bool", true, new TipoVoid());
        RegistroParametro paramABool = crearRegParametros("a", new TipoArreglo(new TipoPrimitivo("Bool")));
        guardar(metodoABool, paramABool, claseIO);

        // st fn Str in_str():
        RegistroMetodo metodoInStr = crearRegMetodo("in_str", true, new TipoPrimitivo("Str"));
        guardar(metodoInStr, null, claseIO);

        // st fn Int in_int():
        RegistroMetodo metodoInInt = crearRegMetodo("in_int", true, new TipoPrimitivo("Int"));
        guardar(metodoInInt, null, claseIO);

        // st fn Bool in_bool():
        RegistroMetodo metodoInBool = crearRegMetodo("in_bool", true, new TipoPrimitivo("Bool"));
        guardar(metodoInBool, null, claseIO);

        claseIO.setDeclarada(true);
        claseIO.setEsPredefinida(true);
        this.tablaClases.put(claseIO.getNombre(), claseIO);
        // --------------------------------------------------------------------------------------
        /*
        // Clase Iterator, es una interfaz que define los métodos necesarios para iterar sobre una colección de elementos.
        //RegistroClase iterator = crearRegClase("Iterator", "Object");

        // fn Bool hasNext(): devuelve true si hay más elementos para iterar, de lo contrario devuelve false.
        RegistroMetodo hasNext = crearRegMetodo("hasNext", false, new TipoPrimitivo("Bool"));
        guardar(hasNext, null, iterator);

        // fn next <type>(): devuelve el siguiente elemento en la iteración. El tipo de retorno debe ser
        //el mismo que el tipo de los elementos de la colección que se está iterando

        // fn next_int():
        RegistroMetodo nextInt = crearRegMetodo("next_int", false, new TipoPrimitivo("Int"));
        guardar(nextInt, null, iterator);

        // fn next_str():
        RegistroMetodo nextStr = crearRegMetodo("next_str", false, new TipoPrimitivo("Str"));
        guardar(nextStr, null, iterator);

        this.tablaClases.put(iterator.getNombre(), iterator); */
        // --------------------------------------------------------------------------------------

        // Clase Array, La clase arreglo proporciona listas de tamaño estático de elementos de tipos primitivos
        RegistroClase claseArray = crearRegClase("Array", "Object");

        // fn Int length(), length devuelve la longitud del parámetro self y los métodos de la interfaz Iterator
        RegistroMetodo metodoLength = crearRegMetodo("length", false, new TipoPrimitivo("Int"));
        guardar(metodoLength, null, claseArray);

        // (hasNext() y next <type>()) para iterar sobre los elementos del arreglo.

        // fn Bool hasNext()
        RegistroMetodo hasNext = crearRegMetodo("hasNext", false, new TipoPrimitivo("Bool"));
        guardar(hasNext, null, claseArray);

        // fn next_int()
        RegistroMetodo nextInt = crearRegMetodo("next_int", false, new TipoPrimitivo("Int"));
        guardar(nextInt, null, claseArray);

        // fn next_str():
        RegistroMetodo nextStr = crearRegMetodo("next_str", false, new TipoPrimitivo("Str"));
        guardar(nextStr, null, claseArray);

        // fn next_bool():
        RegistroMetodo nextBool = crearRegMetodo("next_bool", false, new TipoPrimitivo("Bool"));
        guardar(nextBool, null, claseArray);

        claseArray.setDeclarada(true);
        claseArray.setEsPredefinida(true);
        this.tablaClases.put(claseArray.getNombre(), claseArray);

        // --------------------------------------------------------------------------------------
        // Clase Int, La clase Int proporciona números enteros. No hay métodos especiales para Int.
        RegistroClase claseInt = crearRegClase("Int", "Object");

        claseInt.setDeclarada(true);
        claseInt.setEsPredefinida(true);
        this.tablaClases.put(claseInt.getNombre(), claseInt);

        // --------------------------------------------------------------------------------------
        // Clase Str, La clase Str proporciona cadenas.
        RegistroClase claseStr = crearRegClase("Str", "Object");

        // fn Int length(). length devuelve la longitud del parámetro self.
        RegistroMetodo mStr = crearRegMetodo("length",false,new TipoPrimitivo("Int"));
        guardar(mStr, null, claseStr);

        // fn Str concat(Str s). El método concat devuelve la cadena formada al concatenar s después de self.
        RegistroMetodo concat = crearRegMetodo("concat", false, new TipoPrimitivo("Str"));
        RegistroParametro paramConcat = crearRegParametros("s", new TipoPrimitivo("Str"));
        guardar(concat, paramConcat, claseStr);

        claseStr.setDeclarada(true);
        claseStr.setEsPredefinida(true);
        this.tablaClases.put(claseStr.getNombre(), claseStr);

        // --------------------------------------------------------------------------------------
        // Clase Bool,La clase Bool brinda el true y false.
        RegistroClase claseBool = crearRegClase("Bool", "Object");

        claseBool.setDeclarada(true);
        claseBool.setEsPredefinida(true);
        this.tablaClases.put(claseBool.getNombre(), claseBool);
    }
}

