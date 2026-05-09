package semantico.tipos;

public class TipoPrimitivo extends Tipo{
    //private String nombre; //con nombre me refiero a Str, Bool, Int

    public TipoPrimitivo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getNombreTipo() {
        return this.tipo;
    }

    @Override
    public boolean esTipoEspecial() {
        return false;
    }
}
