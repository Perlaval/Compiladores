package semantico.registros;

import java.util.Map;

public class Constructor extends RegistroMetodo {

    //public boolean active = false;
    //private final boolean isConstructor = true;

    public Constructor() {
        super();
    }

    @Override
    public void imprimirMetodo(RegistroMetodo metodo, RegistroClase claseActual){
        System.out.println("");
        System.out.println("Metodo: constructor  "+", de la clase: "+claseActual.getNombre());
        // imprimo los parametros de ese metodo
        if (listaParametros != null && !listaParametros.isEmpty()){
            System.out.println("Parametros: ");
            for (RegistroParametro p : listaParametros.values()) {
                System.out.println("Posicion: "+p.getPos()+", tipo: "+p.getTipo().getNombreTipo()+", Nombre: "+p.getNombre());
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
                System.out.println("Posicion "+v.getPos()+", tipo: "+v.getTipo().getNombreTipo()+ ", Nombre: "+v.getNombre());
                System.out.println("----");
            }
        }
        else {
            System.out.println("Sin variables locales");
        }
    }
}
