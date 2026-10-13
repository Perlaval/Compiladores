package semantico.nodos.declaraciones;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.registros.RegistroAtributo;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;

public class NodoAtributo extends NodoDeclaracion{
    public NodoAtributo(Token tdeclaracion) {
        super(tdeclaracion);
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        /*
        RegistroAtributo atr = ts.claseActual.getListaAtributos().get(token.getLexema());
        if (atr.getTipo().esTipoReferencia() && ts.noEstaTs(atr.getTipo().getNombreTipo())) {
            throw new ErrorSemantico(token, "La clase '"+atr.getTipo().getNombreTipo()+"' nunca fue definida");
        }
        return atr.getTipo();*/
        return null;
    }

}
