package src.semantico.registros;

import src.semantico.tipos.Tipo;

public class RegistroParametro  {
    // tipo  (int, bool, str, array)
    private Tipo tipo;

    // nombre del atributo
    private String nombre;

    // numero de linea y col para errores
    public int nroLinea;
    public int nroColumna;
}

