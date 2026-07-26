package sintactico.auxiliares;

import semantico.nodos.sentencia.NodoSentencia;

public class AuxRamasIf {

    // SentenciaRec -> Sentencia RecursivoElse
    private final NodoSentencia sentenciaThen;
    private final NodoSentencia sentenciaElse;

    public AuxRamasIf(NodoSentencia nodoThen, NodoSentencia nodoElse) {
        this.sentenciaThen = nodoThen;
        this.sentenciaElse = nodoElse;
    }

    public NodoSentencia getSentenciaThen() {
        return sentenciaThen;
    }

    public NodoSentencia getSentenciaElse() {
        return sentenciaElse;
    }

}
