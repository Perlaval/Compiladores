package semantico.tipos;

public class TipoVoid extends Tipo{
    @Override
    public String getNombreTipo() {
        return "Void";
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
