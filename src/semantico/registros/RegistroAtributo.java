package semantico.registros;

import semantico.tipos.Tipo;

public class RegistroAtributo {

    // posicion del atributo
    public int pos;
    public static int contadorpos = 0;

    // visibilidad del atributo
    // true pub
    // false priv
    public boolean visibilidad;

    // nombre
    public String nombre;

    // tipo
    public Tipo tipo;

    public RegistroAtributo(){
        this.pos = 0;
        this.visibilidad = false;//por defecto es privado, a no ser que se indique lo contrario
        this.nombre = nombre;
        this.tipo = tipo;
    }
    public void setVisibilidad(boolean vis){
        this.visibilidad = vis;
    }
    public void setNombre(String nom){
        this.nombre = nom;
    }
    public void setTipo(Tipo t){
        this.tipo = t;
    }
    public void asignarPos(){
        this.pos = contadorpos;
        contadorpos++;

    }

    public String getNombre() {
        return nombre;
    }

    public Tipo getTipoAtributo(){
        return tipo;
    }
    //metodo que uso para ver que me esta guardando
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
