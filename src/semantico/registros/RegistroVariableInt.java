package semantico.registros;

public class RegistroVariableInt extends RegistroVariable{

    public int value = 0;

    public RegistroVariableInt(String nombre) {
        super(nombre);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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
