package semantico.registros;

public class RegistroVariableStr extends RegistroVariable{

    public String value = "";

    public RegistroVariableStr(String nombre) {
        super(nombre);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
