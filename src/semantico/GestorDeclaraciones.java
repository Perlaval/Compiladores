package semantico;

import lexico.Token;
import semantico.nodos.declaraciones.NodoDeclaracion;
import semantico.nodos.definiciones.NodoClase;
import semantico.registros.RegistroClase;
import sintactico.ErrorSintactico;

import java.util.ArrayList;

public class GestorDeclaraciones {

    private final TablaSimbolos ts;

    public GestorDeclaraciones(TablaSimbolos ts){
        this.ts = ts;
    }

    public RegistroClase registrarClaseTs(Token id, String herencia) throws ErrorSemantico{
        // si el id esta en las clases predefinidas -> error
        if (ts.isNombreClasePredefinida(id.getLexema())){
            throw new ErrorSemantico(id.getFila(), id.getColumna(), "La clase: "+id.getLexema()+" No se puede redefinir, es una clase predefinida");
        }
        RegistroClase clase;
        if (ts.noEstaTs(id.getLexema())){ // no esta guardada la clase en la TS
            // le colocamos que hereda de null, en la consolidacion se le coloca que hereda de Object
            String superClase = herencia;
            clase = ts.crearRegClase(id.getLexema(), superClase);
            ts.tablaClases.put(clase.getNombre(), clase);
        }
        else { // si ya esta en la TS verifico que no haya redefinicion de herencia
            clase = ts.getClase(id.getLexema());
            if (herencia != null){
                if (clase.declarada){
                    if (!clase.heredaDe.equals(herencia)){
                        throw new ErrorSemantico(id.getFila(), id.getColumna(),
                                "Redefinicion de herencia para la clase: "+clase.getNombre());
                    }
                }
                else {
                    // si no esta declarada, la guarde desde impl entonces le seteo la herencia
                    clase.setHeredaDe(herencia);
                    System.out.println("La clase: "+clase.getNombre()+" hereda de: "+clase.heredaDe);
                }
            }
        }
        //REVISAR
        clase.setTokenClase(id); // token utilizado para largar errores durante la consolidacion
        clase.setDeclarada(true); // la declaro
        return clase;

    }

    /*public RegistroClase registrarImplClaseTs(Token idClass) throws ErrorSemantico {
        if (ts.isNombreClasePredefinida(idClass.getLexema())){
            throw new ErrorSemantico(idClass.getFila(), idClass.getColumna(), "La clase: "+idClass.getLexema()+" No se puede redefinir, es una clase predefinida");
        }
        if (ts.noEstaTs(idClass.getLexema())){
            // no esta esa clase, la agrego
            RegistroClase clase = new RegistroClase(idClass.getLexema());
            System.out.println("La clase: "+clase.getNombre()+ "  hereda de: "+clase.getHeredaDe());
            // le seteo declarada a false porque la guarde desde un impl
            clase.setDeclarada(false);
            // le seteo el token por si luego no se declara para lanzar el error
            //clase.setTokenClase(tImpl);

            // le seteo que tiene un impl esa clase
            clase.setImplementada(true);
            ts.tablaClases.put(clase.getNombre(), clase);
            ts.claseActual = clase;

    }*/
}
