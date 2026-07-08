package semantico.registros;

import semantico.tipos.Tipo;

// con una variable guardo lo mismo que con un parametro mas la posicion
public class RegistroVariable{

    public int pos; // posicion del atributo
    public String nombre; // nombre
    public Tipo tipo; // tipo

    public RegistroVariable(String nombre){
        this.nombre = nombre;
    }

    public void setPos(int pos){
        this.pos = pos;
    }
    public void setTipo(Tipo t){
        this.tipo = t;
    }

    public int getPos(){
        return this.pos;
    }
    public Tipo getTipo(){
        return tipo;
    }
    public String getNombre(){
        return this.nombre;
    }

    @Override
    public String toString() {
        return "Variable Local{" +
                "nombre='" + nombre + '\'' +
                ", tipo=" + (tipo != null ? tipo.getNombreTipo() : "null") +
                ", pos=" + pos +
                '}';
    }

}
