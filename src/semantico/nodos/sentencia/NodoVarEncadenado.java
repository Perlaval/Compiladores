package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoVarEncadenado extends NodoEncadenado{

    public NodoVarEncadenado(int nroLinea, int nroColumna, String lexema) {
        this.nroLinea = nroLinea;
        this.nroColumna = nroColumna;
        this.lexema = lexema;
    }

    public void setProxEncadenado(NodoVarEncadenado proxEncadenado){
        this.proxEncadenado = proxEncadenado;
    }

    @Override
    public Tipo chequear(Tipo tipo) throws ErrorSemantico {
        return null;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        return null;
    }
}
