package semantico.nodos.expresion.encadenables;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.nodos.expresion.encadenables.primario.acceso.NodoLlamadaMetodo;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroMetodo;
import semantico.registros.RegistroParametro;
import semantico.tipos.Tipo;
import semantico.tipos.TipoArreglo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class NodoEncadenable extends NodoExpresion {

    protected NodoEncadenable proxEncadenado;

    protected NodoEncadenable(Token token) {
        super(token);
    }

    public void setProxEncadenado(NodoEncadenable proxEncadenado){
        this.proxEncadenado = proxEncadenado;
    }

    public NodoEncadenable getProxEncadenado() { return proxEncadenado;}

    // voy a agregar un chequear extra cuando estoy en una cadena de .id.id.id...
    // ya que debo actualizar la clase actual e ir buscando ahi
    public Tipo chequear(TablaSimbolos ts, RegistroClase claseContexto) throws ErrorSemantico{
        return null;
    }

    protected Tipo continuarCadena(TablaSimbolos ts, Tipo tipo) throws ErrorSemantico {
        // veo encadenado
        if (proxEncadenado != null){
            // si el tipo es Array, entonces veo en los metodos que son los de Iterator
            if (tipo.esTipoArreglo()){
                // obtengo el tipo del arreglo y veo que metodo quiere implementar
                // si vine aca entonces lo que sigue es una llamada metodo de la interfaz iterator
                // por ej: v.length (siendo v de tipo Array)
                if (!(proxEncadenado instanceof NodoLlamadaMetodo)) {
                    throw new ErrorSemantico(token,
                            "Sobre un Array solo se pueden invocar métodos (hasNext/next)");
                }
                TipoArreglo tipoArreglo = (TipoArreglo) tipo;
                return ((NodoLlamadaMetodo) proxEncadenado).tipoMetodoArray(ts, tipoArreglo);

            }
            // si el tipo es Str veo los metodos de esa clase (length, concat)
            if (tipo.getNombreTipo().equals("tStr")){
                if (!(proxEncadenado instanceof NodoLlamadaMetodo)){
                    // seria por ejemplo si quiero hacer v.y, y v es Str
                    throw new ErrorSemantico(token, "Sobre Str solo se pueden invocar metodos");
                }
                RegistroClase claseStr = ts.getClase("Str");
                RegistroClase claseAnterior = ts.claseActual;
                ts.setClaseActual(claseStr);
                try {
                    return proxEncadenado.chequear(ts);
                } finally {
                    ts.setClaseActual(claseAnterior);
                }
            }

            // si tiene encadenado y no es de tipo referencia donde estoy entonces error
            if (!tipo.esTipoReferencia()){
                throw new ErrorSemantico(token, "No se puede acceder a un miembro de un tipo que no es una clase");
            }
            // obtengo la clase
            RegistroClase claseSig = ts.getClase(tipo.getNombreTipo());
            if (claseSig == null){
                throw new ErrorSemantico(token, "La clase: "+claseSig.getNombre()+" no ha sido declarada");
            }

            RegistroClase claseAnterior = ts.claseActual; // clase donde estaba
            ts.setClaseActual(claseSig); // entro a la clase siguiente
            try {
                return proxEncadenado.chequear(ts); // avanzo en la cadena con la clase actual actualizada
            } finally {
                ts.setClaseActual(claseAnterior); // sino restauro el contexto
            }
        }
        return tipo;
    }

    // funcion que verifica que los parametros coincidan ( es llamada por nodoLlamadaMetodo y nodoLlamadaMetodoEstatico )
    protected void verificarParametrosMetodo(TablaSimbolos ts,RegistroMetodo metodoActual, ArrayList<NodoExpresion> listaArg, String nombreMetodo) throws ErrorSemantico {

        if (metodoActual.getListaParametros().size() != listaArg.size()) {
            throw new ErrorSemantico(token, "La cantidad de parametros recibidos no coincide con los esperados");
        }
        // si si coincide verifico uno por uno que tengan el mismo tipo

        // param[0] == listaArg[0]
        // ordeno los parámetros por su posición real, no por el orden del HashMap
        List<RegistroParametro> parametrosOrdenados = new ArrayList<>(metodoActual.getListaParametros().values());
        parametrosOrdenados.sort(Comparator.comparingInt(RegistroParametro::getPos));

        int i = 0;
        for (RegistroParametro parametro: parametrosOrdenados){
            Tipo tipoParam = parametro.getTipo();
            Tipo tipoArgActual = listaArg.get(i).chequear(ts);

            if (tipoParam.esTipoArreglo()) {
                // el parámetro esperado es un arreglo: el argumento también tiene que serlo
                if (!tipoArgActual.esTipoArreglo()) {
                    throw new ErrorSemantico(token, "En los parametros de llamada al método '" + nombreMetodo
                            + "' se esperaba un tipo: " + tipoParam.getNombreTipo()
                            + " y se obtuvo: " + tipoArgActual.getNombreTipo());
                }

                TipoArreglo arrParam = (TipoArreglo) tipoParam;
                TipoArreglo arrArg = (TipoArreglo) tipoArgActual;

                Tipo internoParam = arrParam.getTipoInterno();
                Tipo internoArg = arrArg.getTipoInterno();

                if (!internoParam.getNombreTipo().equals(internoArg.getNombreTipo())) {
                    throw new ErrorSemantico(token, "En los parametros de llamada al método '" + nombreMetodo
                            + "' se esperaba un arreglo de tipo: " + internoParam.getNombreTipo()
                            + " y se obtuvo un arreglo de tipo: " + internoArg.getNombreTipo());
                }

            } else {
                // caso normal, el parámetro no es arreglo
                if (!tipoParam.getNombreTipo().equals(tipoArgActual.getNombreTipo())) {
                    throw new ErrorSemantico(token, "En los parametros de llamada al método '" + nombreMetodo
                            + "' se esperaba un tipo: " + tipoParam.getNombreTipo()
                            + " y se obtuvo: " + tipoArgActual.getNombreTipo());
                }
            }

            i++;
        }
    }

}
