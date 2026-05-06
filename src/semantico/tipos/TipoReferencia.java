package src.semantico.tipos;

public class TipoReferencia extends Tipo{
    private String nombreClase; // para manejar la herencia

    public TipoReferencia(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    public String getNombreClase(){
        return nombreClase;
    }

    @Override
    public String getNombre() {
        return nombreClase;
    }
}
