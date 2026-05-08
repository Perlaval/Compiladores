package semantico.tipos;

public class TipoPrimitivo extends Tipo{
    private String nombre; // con nombre me refiero a Str, Bool, Int

    public TipoPrimitivo(String t) {
        this.nombre = t;
    }


    @Override
    public String getNombre() {
        return this.nombre;
    }
}
