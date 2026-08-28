package semantico.nodos.expresion.encadenables.primario.acceso;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;
import semantico.tipos.TipoReferencia;
import semantico.tipos.TipoSelf;

public class NodoAccesoSelf extends NodoAcceso{
    //encadenadoOpt -> llamadaMetodo | accesoVar
    //private final NodoEncadenado encadenado;

    public NodoAccesoSelf(Token token) {
        super(token); //token.getLexema = "self"
    }

    // self representa a la instancia de clase
    // si tengo ret self.x;
    // voy a devolver la clase a la que pertenece x
    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        // devuelvo el tipo de la clase actual
        // si el metodoactual es estatico largo error -> no puede acceder a una variable de instancia (self)
        RegistroMetodo metodoActual = ts.getMetodoActual();
        RegistroClase claseActual = ts.getClaseActual();

        /*if (metodoActual.isConstructor()){
            System.out.println("Estoy en el constructor");
        }*/

        //System.out.println("Estoy en nodoSelf y obtuve la clase actual: "+claseActual.getNombre());
        //System.out.println("Y el tipo de self es: "+token.getLexema());


        if (metodoActual.esEstatico){
            throw new ErrorSemantico(token, "No se puede accceder a una variable de instancia en un contexto estatico");
        }

        if (proxEncadenado != null){
            //System.out.println("Entre a proxEncadenado de nodoSelf");
            // avanzo en la cadena y verifico el siguiente .id, hasta llegar al ultimo
            // todo dentro de el mismo contexto de claseActual
            //return proxEncadenado.chequear(ts, claseActual);
            return proxEncadenado.chequear(ts);
        }

        // si no es estatico devuelvo el tipo de la clase
        //RegistroClase claseActual = metodoActual.getClass();

        // debo retornar el tipo de la clase actual
        //return new TipoSelf();
        //System.out.println("Estoy en nodoself y retorno: "+claseActual.getNombre());
        return new TipoReferencia(claseActual.getNombre());
        //return null;
    }

}
