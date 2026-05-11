package semantico.registros;

import semantico.tipos.Tipo;
import semantico.tipos.TipoPrimitivo;

import java.util.ArrayList;

public class RegistroVariableArray extends RegistroVariable{

    public ArrayList<TipoPrimitivo> value;
    //cuando se crea no esta inicializado por lo que tiene el valor por defecto null

    public RegistroVariableArray(String nombre) {
        super(nombre);
    }

    public ArrayList<TipoPrimitivo> getValue() {
        return value;
    }

    public void setValue(ArrayList<TipoPrimitivo> value) {
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
