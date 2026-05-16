package semantico.tipos;

public abstract class Tipo {

    public String tipo;

    public abstract String getNombreTipo();
    public abstract boolean esTipoEspecial();
    public abstract boolean esTipoPrimitivo();
    public abstract boolean esTipoReferencia();
    public abstract boolean esTipoArreglo();
}
