package semantico.registros;

import semantico.tipos.Tipo;

// con una variable guardo lo mismo que con un parametro mas la posicion
public class RegistroVariable  {

    // posicion del atributo
    public int pos;
    public static int contadorpos = 0;

    // nombre
    public String nombre;

    // tipo
    public Tipo tipo;

    public void asignarPos(){
        this.pos = contadorpos;
        contadorpos++;

    }
    public RegistroVariable(String nombre){
        this.nombre = nombre;
        this.pos = 0;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre(){
        return this.nombre;
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

}
