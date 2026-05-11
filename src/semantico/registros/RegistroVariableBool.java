package semantico.registros;

public class RegistroVariableBool extends RegistroVariable{

    public boolean value = false;

    public RegistroVariableBool(String nombre) {
        super(nombre);
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {

        return "Variable Local{" +
                "nombre='" + nombre + '\'' +
                ", tipo=" + (tipo != null ? tipo.getNombreTipo() : "null") +
                ", pos=" + pos +
                ", value=" + getValue() +
                '}';
    }
}
