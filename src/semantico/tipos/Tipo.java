package semantico.tipos;

public abstract class Tipo {

    public String tipo;

    public abstract String getNombreTipo();
    public abstract boolean esTipoEspecial();
    public abstract boolean esTipoPrimitivo();
    public abstract boolean esTipoReferencia();
    public abstract boolean esTipoArreglo();

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof Tipo))
            return false;

        Tipo otro = (Tipo) obj;

        return this.getNombreTipo().equals(otro.getNombreTipo());
    }
}
