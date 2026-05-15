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
}
