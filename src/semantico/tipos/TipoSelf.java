package semantico.tipos;

public class TipoSelf extends Tipo{

    public TipoSelf() {
        this.tipo = tipo;
    }

    @Override
    public String getNombreTipo() {
        return this.tipo;
    }

    @Override
    public boolean esTipoEspecial() {
        return true;
    }

    @Override
    public boolean esTipoPrimitivo() {
        return false;
    }

    @Override
    public boolean esTipoReferencia() {
        return false;
    }

    @Override
    public boolean esTipoArreglo() {
        return false;
    }
}
