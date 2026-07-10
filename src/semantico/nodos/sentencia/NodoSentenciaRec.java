package semantico.nodos.sentencia;

import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public class NodoSentenciaRec extends NodoSentencia {

    // SentenciaRec -> Sentencia RecursivoElse
    private NodoSentencia sentenciaThen;
    private NodoSentencia sentenciaElse;

    public NodoSentenciaRec(NodoSentencia nodoThen, NodoSentencia nodoElse) {
        this.sentenciaThen = nodoThen;
        this.sentenciaElse = nodoElse;
    }

    public NodoSentencia getSentenciaThen() {
        return sentenciaThen;
    }

    public NodoSentencia getSentenciaElse() {
        return sentenciaElse;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

        return null;
    }
}
