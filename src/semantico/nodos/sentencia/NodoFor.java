package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;


public class NodoFor extends NodoSentencia{

    // for ( tipoPrim i in iterador)
    private Tipo tipoVar;
    private Token variable;
    private Token iterador;
    private NodoSentencia cuerpo;

    public NodoFor(Token tFor, Tipo tipoVar, Token variable, Token iterador, NodoSentencia cuerpo) {
        super(tFor);
        this.tipoVar = tipoVar;
        this.variable = variable;
        this.iterador = iterador;
        this.cuerpo = cuerpo;
    }

    public Tipo getTipoVar() {
        return tipoVar;
    }

    public Token getNodoVariable() {
        return variable;
    }

    public Token getNodoIterador() {
        return iterador;
    }

    public NodoSentencia getCuerpo() {
        return cuerpo;
    }

    /*@Override
    public Tipo chequear(TablaSimbolos ts) {

        return null;
    }*/

    @Override
    public void accept(Visitor visitor) throws ErrorSemantico {
        visitor.visit(this);
    }

}
