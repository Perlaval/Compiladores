package semantico.nodos;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroVariable;
import semantico.tipos.Tipo;
import semantico.tipos.TipoReferencia;

public abstract class Nodo {

    protected int nroLinea;
    protected int nroColumna;
    protected String lexema;

    //Durante la construccion del ast no necesito estos 3 atr---------
    protected RegistroVariable token;
    protected Tipo tipoSintetizado;
    protected RegistroClase tipoHeredado;
    //----------------------------------------------------


   public RegistroVariable getToken() {
        return token;
    }

    public int getNroLinea() {
        return nroLinea;
    }

    public void setNroLinea(int nroLinea) {
        this.nroLinea = nroLinea;
    }

    public int getNroColumna() {
        return nroColumna;
    }

    public void setNroColumna(int nroColumna) {
        this.nroColumna = nroColumna;
    }

    public void setToken(RegistroVariable token) {
        this.token = token;
    }

    public Tipo getTipoSintetizado() {
        return tipoSintetizado;
    }

    public void setTipoSintetizado(Tipo tipoSintetizado) {
        this.tipoSintetizado = tipoSintetizado;
    }

    public void setTipoHeredado(RegistroClase tipoHeredado) {
        this.tipoHeredado = tipoHeredado;
    }


    //public abstract Tipo chequear() throws ErrorSemantico;

    //public void chequear() throws ErrorSemantico{};
}
