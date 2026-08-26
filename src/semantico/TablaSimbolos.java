package semantico;

import lexico.Token;
import semantico.registros.*;
import semantico.tipos.*;

import javax.imageio.plugins.tiff.ExifInteroperabilityTagSet;
import java.util.*;

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

    // Getters
    public RegistroClase getClase(String nombre){
        return tablaClases.get(nombre);
    }
    public RegistroClase getClaseActual() { return this.claseActual;}
    public RegistroMetodo getMetodoActual() { return  this.metodoActual;}
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

    // Setters
    public void setMetodoActual(RegistroMetodo metodo){
        this.metodoActual = metodo;
    }
    public void setBloqueStart(RegistroStart bloqueStart){this.bloqueStart = bloqueStart;}
    public void setClaseActual(RegistroClase clase){this.claseActual = clase;}

    /*
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
    */

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
        /*if (bloqueStart != null){
            // si estoy en el bloque start seguramente estoy haciendo algo asi: IO.out_str ..., debo verificar que esa metodo existe
           // RegistroMetodo metodo = this..get(nombreMetodo);
        }
        else {*/
        if (bloqueStart != null){  // estoy en start, depende que lea como resuelvo, ya no tengo contexto de clase actual
            // HACER !!!!
            System.out.println("Estoy en start, en el metodo no esta de la ts");
            return true;

        } else {
            RegistroMetodo metodo = this.claseActual.listaMetodos.get(nombreMetodo);
            return (metodo == null);
        }

       // }


        /*RegistroAtributo atr = clase.listaAtributos.get(id);
        if (atr == null){
            return true;
        }
        return !atr.isVisibilidad();*/
    }

// -----------------------------------------Crear Registros-------------------------------------------------------------------------------------------
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

    // creo un registro de un atributo
    public RegistroAtributo crearRegAtributo(String id, Tipo tipo, boolean vis){
        RegistroAtributo atributo = new RegistroAtributo(id);
        atributo.setTipo(tipo);
        atributo.setVisibilidad(vis);
        return atributo;
    }


    // creo un registro de una variable
    public RegistroVariable crearRegVar(String id, Tipo tipo){
        RegistroVariable variable = new RegistroVariable(id);
        variable.setTipo(tipo);
        return variable;
    }
// ----------------------------------------------------------------------------------------------------------------------------------------------------

    // guardo una clase en la TS
    public void guardar(RegistroMetodo metodo, RegistroParametro parametro, RegistroClase clase){
        if (parametro != null){
            metodo.listaParametros.put(parametro.getNombre(), parametro);
        }
        // si parametro es null ya inicializo en en registro del metodo a la lista vacia
        clase.listaMetodos.put(metodo.getNombre(), metodo);
    }

// --------------------------CONSOLIDACION TS-----------------------------------------------------------------------------------------------------
    // metodo de consolidacion de la TS
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

            // verifico herencia circular
            verificarHerenciaCircular(clase);

            consolidarAtributos(clase);
            consolidarMetodos(clase);

        }
    }
    public void consolidarStart() throws ErrorSemantico {
        // verifico que de todas las variables que declare los tipos existan
        for (RegistroVariable variable: bloqueStart.listaVariables.values()){
            if (variable.tipo.esTipoReferencia()){
                String nombreClase = variable.tipo.getNombreTipo();
                if (noEstaTs(nombreClase)){
                    throw new ErrorSemantico(variable.getTokenVarLocal(),
                            "La clase: '"+nombreClase+"' nunca fue definida");
                }
            }

        }
    }//

    public void verificarDeclarada(RegistroClase clase) throws ErrorSemantico{
        if (!clase.getDeclarada()) {
            throw new ErrorSemantico(clase.getTokenClase(),
                    "La clase " + clase.getNombre() + " no fue declarada");
        }
    }
    public void verificarImplementada(RegistroClase clase) throws ErrorSemantico{
        if (!clase.getImplementada()){
            throw new ErrorSemantico(clase.getTokenClase(),
                    "La clase "+clase.getNombre()+" debe tener al menos un impl");
        }
    }
    public void verificarConstructor(RegistroClase clase) throws ErrorSemantico{
        if (!clase.inConstructor){
            throw new ErrorSemantico(clase.getTokenClase(),
                    "La clase "+clase.getNombre()+" no posee constructor");
        }
    }
    public void verificarHerenciaDeclarada(RegistroClase clase, String padre) throws ErrorSemantico{
        if (padre != null && !tablaClases.containsKey(padre)){
            throw new ErrorSemantico(clase.getTokenClase(),
                    "La clase "+padre+" no fue declarada");
        }
        // si hereda de si misma
        if (padre != null && clase.getHeredaDe().equals(clase.getNombre())){
            throw new ErrorSemantico(clase.getTokenClase(),
                    "La clase "+padre+" no puede heredar de si misma");
        }
        // le asigno object si no tiene herencia declarada
        if (padre == null){
            clase.setHeredaDe("Object");
        }
    }
    public void verificarHerenciaCircular(RegistroClase clase) throws ErrorSemantico{
        // recorro la cadena de herencia hacia arriba (hasta object)
        // si visito dos veces la misma clase -> error (circular)
        // A : B
        // B : C
        // C : Object
        // recorro desde A y no deberia repetir una clase
        Set<String> visitadas = new HashSet<>(); // uso hashset porque no permite elementos repetidos
        RegistroClase claseActual = clase;
        while(!claseActual.getNombre().equals("Object")){
            if (visitadas.contains(claseActual.getNombre())) {
                throw new ErrorSemantico(
                        clase.getTokenClase(),
                        "La clase '" + clase.getNombre() +
                                "' posee herencia circular."
                );
            }
            visitadas.add(claseActual.getNombre());
            claseActual = tablaClases.get(claseActual.getHeredaDe());
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
                    throw new ErrorSemantico(clase.getTokenClase(),
                            "Atributo " + atributo.getNombre() + " redefinido en la clase " + clase.getNombre());
                }
                clase.getListaAtributos().put(atributo.getNombre(), atributo);
            }
        }
        // si tengo un atributo de este estilo: A c; debo verificar que la clase A exista en mi ts
        for (RegistroAtributo atributo: clase.getListaAtributos().values()){
            if (atributo.tipo.esTipoReferencia()){
                // si es de tipo referencia (idClass) entonces es una clase, la busco en mi ts
                String nombreClase = atributo.tipo.getNombreTipo();
                if (noEstaTs(nombreClase)){
                    throw new ErrorSemantico(atributo.getTokenAtributo(),
                            "La clase: '"+nombreClase+"' nunca fue definida");
                }
            }
        }
    }


// ------------------------------------METODOS EN CONSOLIDACION-----------------------------------------------------------------------------------------------------
// si un metodo de instancia tiene el mismo nombre que uno heredado lo puedo reescribir solo si:
// - misma cantidad de parametros (OK)
// - mismos tipos de parametros (OK)
// - el tipo de retorno es igual (OK)
// Un metodo de instancia (fn) se puede sobreescribir
// Un metodo estatico (st) no se puede sobreescribir
    public void consolidarMetodos(RegistroClase clase) throws ErrorSemantico {
        LinkedHashMap<String, RegistroMetodo> nuevosMetodos = new LinkedHashMap<>();
        if (!clase.heredaDe.equals("Object")){
            RegistroClase padre = tablaClases.get(clase.getHeredaDe());
            for (RegistroMetodo metodo: padre.getListaMetodos().values()){ // agrego los metodos del padre al hijo
                if (clase.getListaMetodos().containsKey(metodo.getNombre())){ // si el metodo es redefinido
                    RegistroMetodo metodoHijo = clase.getListaMetodos().get(metodo.getNombre());
                    verificarRedefinicionMetodo(metodo, metodoHijo, padre);
                    nuevosMetodos.put(metodo.getNombre(),clase.getListaMetodos().get(metodo.getNombre()));
                }
                else {
                    nuevosMetodos.put(metodo.getNombre(), metodo);
                }
            }
            // agrego los metodos de la subclase
            for (RegistroMetodo metodo : clase.getListaMetodos().values()) {
                if (!nuevosMetodos.containsKey(metodo.getNombre())) {
                    nuevosMetodos.put(metodo.getNombre(), metodo);
                }
            }
            clase.setListaMetodos(nuevosMetodos);
        }
        // para cada metodo verifico las var locales, si tengo alguna de este estilo: A c; debo verificar que A exista en mi TS
        for (RegistroMetodo metodo: clase.getListaMetodos().values()){
            for (RegistroVariable varLocal: metodo.getListaVarLocales().values()){
                if (varLocal.tipo.esTipoReferencia()){
                    String nombreClase = varLocal.tipo.getNombreTipo();
                    if (noEstaTs(nombreClase)){
                        throw new ErrorSemantico(varLocal.getTokenVarLocal(),
                                "La clase: '"+nombreClase+"' nunca fue definida");
                    }
                }
            }
        }
    }
    public void verificarRedefinicionMetodo(RegistroMetodo metodo, RegistroMetodo metodoHijo, RegistroClase padre) throws ErrorSemantico {
        // si son diferentes (instancia y estatico)
        if (metodo.esEstatico != metodoHijo.esEstatico){
            throw new ErrorSemantico(
                    metodoHijo.getTokenMetodo(),
                    "El metodo: "+metodoHijo.getNombre()+" no puede redefinirse como de instancia, ni viceversa");
        }
        // si el hijo es de instancia
        if (!metodoHijo.esEstatico){
            // chequeo parametros y tipos
            verificarCantParam(metodo, metodoHijo, padre);
            verificarTiposParam(metodo, metodoHijo, padre);
            verificarRetorno(metodo, metodoHijo);
        }
        // si es estatico el hijo se sobreescribe y me quedo con el del hijo (ya que oculto el del padre)
    }
    public void verificarCantParam(RegistroMetodo metodo, RegistroMetodo metodoHijo, RegistroClase padre) throws ErrorSemantico {
        // chequeo cant parametros
        if (metodoHijo.getListaParametros().size() != metodo.getListaParametros().size()){ // chequeo cant de parametros
            throw new ErrorSemantico(metodoHijo.getTokenMetodo(),
                    "El método " + metodoHijo.getNombre() +
                            " redefine al metodo heredado de la clase: "+padre.getNombre()+" ,con una cantidad distinta de parámetros. " +
                            "Se esperaban " + metodo.getListaParametros().size() +
                            " parámetros y se encontraron " + metodoHijo.getListaParametros().size() + ".");
        }
    }
    public void verificarTiposParam(RegistroMetodo metodo, RegistroMetodo metodoHijo, RegistroClase padre) throws ErrorSemantico{
        // si tiene la misma cant de parametros deben coincidir los tipos
        Map<String, RegistroParametro> paramsPadre = metodo.getListaParametros();
        Map<String, RegistroParametro> paramsHijo = metodoHijo.getListaParametros();
        // iterator es un objeto que recorre una coleccion elemento por elemento
        Iterator<RegistroParametro> itPadre = paramsPadre.values().iterator();
        Iterator<RegistroParametro> itHijo = paramsHijo.values().iterator();

        while (itPadre.hasNext() && itHijo.hasNext()) {

            RegistroParametro paramPadre = itPadre.next();
            RegistroParametro paramHijo = itHijo.next();

            if (!paramPadre.getTipo().equals(paramHijo.getTipo())) {
                throw new ErrorSemantico(
                        metodoHijo.getTokenMetodo(),
                        "Para el parámetro " + paramHijo.getNombre() +
                                " ,se esperaba: "+paramPadre.getTipo().getNombreTipo()+ " y se encontro: "+paramHijo.getTipo().getNombreTipo()
                );
            }
        }
    }
    public void verificarRetorno(RegistroMetodo metodo, RegistroMetodo metodoHijo) throws ErrorSemantico{
        // verifico que el retorno sea el mismo
        if (!metodo.getTipoRetorno().getNombreTipo()
                .equals(metodoHijo.getTipoRetorno().getNombreTipo())){
            throw new ErrorSemantico(
                    metodoHijo.getTokenMetodo(),
                    "Se esperaba un retorno de tipo: "+metodo.getTipoRetorno().getNombreTipo()+ " y se encontro: "+metodoHijo.getTipoRetorno().getNombreTipo()
            );
        }
    }
// -----------------------------------------Fin Consolidacion-----------------------------------------------------------

// -----------------------------------------Metodos de conformacion de tipos--------------------------------------------
    //Este metodo me sirve para determinar si hay polimorfismo al declarar un argumento como parametro de un metodo/constructor -> lo llamo en el metodo chequear de NodoNewObjeto
    public boolean conforma(String tipoDeclarado, String tipoEsperado){
        RegistroClase claseDeclarada = this.getClase(tipoDeclarado);

        if (!claseDeclarada.heredaDe.equals("Object")){
            if(!claseDeclarada.getHeredaDe().equals(tipoEsperado)){
                return conforma(claseDeclarada.getHeredaDe(),tipoEsperado);
            }
        }
        return tipoDeclarado.equals(tipoEsperado);
    }
// -----------------------------------------FIN Metodos de conformacion de tipos----------------------------------------

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

