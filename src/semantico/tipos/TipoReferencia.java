package semantico.tipos;

public class TipoReferencia extends Tipo{
    //private String nombreClase; // para manejar la herencia

    public TipoReferencia(String tipo) {
        this.tipo = tipo;
    }

    /*public String getNombreClase(){
        return nombre;
    }*/

    @Override
    public String getNombreTipo() {
        return tipo;
    }

    @Override
    public boolean esTipoEspecial() {
        return false;
    }

    @Override
    public boolean esTipoPrimitivo() {
        return false;
    }

    @Override
    public boolean esTipoReferencia() {
        return true;
    }

    @Override
    public boolean esTipoArreglo() {
        return false;
    }

}
