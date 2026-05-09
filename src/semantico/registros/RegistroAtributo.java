package semantico.registros;

import semantico.tipos.Tipo;

public class RegistroAtributo extends RegistroVariable {
    // visibilidad del atributo
    // true pub
    // false priv
    public boolean visibilidad;

    public void setVisibilidad(boolean vis) {
        this.visibilidad = vis;
    }

    public RegistroAtributo(String nombre) {
        super(nombre);
        this.visibilidad = false; //por defecto es privado, a no ser que se indique lo contrari
    }

    @Override
    public String toString() {
        return "Atributo{" +
                "nombre='" + nombre + '\'' +
                ", tipo=" + (tipo != null ? tipo.getNombre() : "null") +
                ", pos=" + pos +
                ", pub=" + visibilidad +
                '}';
    }


}



