package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.tipos.Tipo;

public class NodoVarEncadenado extends NodoEncadenado{

    public NodoVarEncadenado(int nroLinea, int nroColumna, String lexema) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.lexema = lexema;
    }

    public NodoVarEncadenado() {
    }

    public void setProxEncadenado(NodoVarEncadenado proxEncadenado){
        this.proxEncadenado = proxEncadenado;
    }

    @Override
    public Tipo chequear(Tipo tipo) throws ErrorSemantico {
        return null;
    }
}
