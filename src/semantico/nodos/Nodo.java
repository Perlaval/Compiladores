package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.tipos.Tipo;

public abstract class Nodo {

    protected final Token token;

    //Durante la construccion del ast no necesito estos 3 atr---------
    //protected RegistroVariable token;
    //protected Tipo tipoSintetizado;
    //protected RegistroClase tipoHeredado;

    //----------------------------------------------------
    protected Nodo(Token token) {
        this.token = token;
    }

    public Token getToken(){
        return token;
    }

    /*public RegistroVariable getToken() { return token;}
    public int getNroLinea() {
        return nroLinea;
    }

    public Tipo getTipoSintetizado() {
        return tipoSintetizado;
    }

    public void setTipoSintetizado(Tipo tipoSintetizado) {
        this.tipoSintetizado = tipoSintetizado;
    }

    public void setTipoHeredado(RegistroClase tipoHeredado) {
        this.tipoHeredado = tipoHeredado;
    }*/

    //public abstract Tipo chequear(TablaSimbolos ts) throws ErrorSemantico;

    //public void chequear() throws ErrorSemantico{};
}
