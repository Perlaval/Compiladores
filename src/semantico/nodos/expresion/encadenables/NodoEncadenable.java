package semantico.nodos.expresion.encadenables;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.registros.RegistroClase;
import semantico.tipos.Tipo;

public abstract class NodoEncadenable extends NodoExpresion {

    protected NodoEncadenable proxEncadenado;

    protected NodoEncadenable(Token token) {
        super(token);
    }

    public void setProxEncadenado(NodoEncadenable proxEncadenado){
        this.proxEncadenado = proxEncadenado;
    }

    public NodoEncadenable getProxEncadenado() { return proxEncadenado;}

    // voy a agregar un chequear extra cuando estoy en una cadena de .id.id.id...
    // ya que debo actualizar la clase actual e ir buscando ahi
    public Tipo chequear(TablaSimbolos ts, RegistroClase claseContexto) throws ErrorSemantico{
        return null;
    }

    protected Tipo continuarCadena(TablaSimbolos ts, Tipo tipo) throws ErrorSemantico {
        // veo encadenado
        if (proxEncadenado != null){
            // si tiene encadenado y no es de tipo referencia donde estoy entonces error
            if (!tipo.esTipoReferencia()){
                throw new ErrorSemantico(token, "No se puede acceder a un miembro de un tipo que no es una clase");
            }
            // obtengo la clase
            RegistroClase claseSig = ts.getClase(tipo.getNombreTipo());
            if (claseSig == null){
                throw new ErrorSemantico(token, "La clase: "+claseSig.getNombre()+"no ha sido declarada");
            }

            RegistroClase claseAnterior = ts.claseActual; // clase donde estaba
            ts.setClaseActual(claseSig); // entro a la clase siguiente
            try {
                return proxEncadenado.chequear(ts); // avanzo en la cadena con la clase actual actualizada
            } finally {
                ts.setClaseActual(claseAnterior); // sino restauro el contexto
            }
        }
        return tipo;
    }


}
