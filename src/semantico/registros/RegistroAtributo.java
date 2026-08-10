package semantico.registros;

import lexico.Token;
import semantico.tipos.Tipo;

public class RegistroAtributo extends RegistroVariable {
    // visibilidad del atributo
    // true pub
    // false priv
    public boolean visibilidad;

    private Token tokenAtributo;

    public void setVisibilidad(boolean vis) {
        this.visibilidad = vis;
    }

    public RegistroAtributo(String nombre) {
        super(nombre);
        this.visibilidad = false; //por defecto es privado, a no ser que se indique lo contrari
    }

    public void setTokenAtributo(Token tokenAtributo){
        this.tokenAtributo = tokenAtributo;
    }
    public Token getTokenAtributo(){
        return this.tokenAtributo;
    }


    @Override
    public String toString() {
        return "Atributo{" +
                "nombre='" + nombre + '\'' +
                ", tipo=" + (tipo != null ? tipo.getNombreTipo() : "null") +
                ", pos=" + pos +
                ", pub=" + visibilidad +
                '}';
    }

    public boolean isVisibilidad() {
        return visibilidad;
    }
}



