package src.semantico.registros;

import src.semantico.tipos.Tipo;

public class RegistroParametro  {
    // tipo  (int, bool, str, array)
    private Tipo tipo;
    private int pos;
    public static int contadorpos = 0;
    private String nombre;  // nombre del atributo

    // numero de linea y col para errores
    public int nroLinea;
    public int nroColumna;

    public RegistroParametro(){
        this.pos = 0;
        this.nombre = nombre;
        this.tipo = tipo;
    }
    public void setNombre(String n){
        this.nombre = n;
    }
    public void setTipoParametro(Tipo t){
        this.tipo = t;
    }
    public void asignarPos(){
        this.pos = contadorpos;
        contadorpos++;
    }
    public String getNombre(){
        return this.nombre;
    }
    public Tipo getTipoParametro(){
        return this.tipo;
    }
    public int getPos(){
        return this.pos;
    }
}

