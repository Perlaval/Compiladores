package semantico;

public interface ValidarDeclaracion {
    boolean validarNombre(Definicion def, String nombre);
    boolean isNombreTipoEspecial(String nombre);
    boolean isNombreClasePredefinida(String nombre);

    enum Definicion{
        METODO,
        VAR
    }
}


