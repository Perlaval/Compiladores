package semantico.tipos;

public class TipoArreglo extends Tipo{

    private Tipo tipoInterno;

    public TipoArreglo(Tipo tipoInterno){
        this.tipo = "tArray";
        this.tipoInterno = tipoInterno;
    }

    @Override
    public String getNombreTipo() {
        return this.tipo;
    }

    public Tipo getTipoInterno(){
        return this.tipoInterno;
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
        return true;
    }
}
