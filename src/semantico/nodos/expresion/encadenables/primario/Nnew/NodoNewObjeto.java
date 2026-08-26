package semantico.nodos.expresion.encadenables.primario.Nnew;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.registros.Constructor;
import semantico.registros.RegistroClase;
import semantico.registros.RegistroParametro;
import semantico.tipos.Tipo;
import semantico.tipos.TipoReferencia;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class NodoNewObjeto extends NodoNew {

    //private NodoId nodoId;
    private ArrayList<NodoExpresion> listaArgumentos;

    public NodoNewObjeto(Token token, ArrayList<NodoExpresion> listaArgumentos) {
        super(token); //token = token del idClass
        this.listaArgumentos = listaArgumentos;
    }

    @Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {

       RegistroClase clase = ts.getClase(token.getLexema());
        Constructor constructor = clase.getConstructor();
        Map<String, RegistroParametro> listaParametros = constructor.getListaParametros();

        //1. Chequeamos que la cantidad de argumentos del constructor coincida con la longitud de listaArgumentos
        if (listaParametros.size() != listaArgumentos.size()) throw new ErrorSemantico(token, "Error Semantico la cantidad de argumentos del constructor de la clase " + token.getLexema() + " no coincide");

        //2. Chequeamos que el tipo de los argumentos actuales coincida con la posicion y el tipo de los agumentos que recibe el constructor de la clase
        Iterator<RegistroParametro> it = listaParametros.values().iterator();

        for (NodoExpresion expresion: listaArgumentos) {
            RegistroParametro reg = it.next();

            if (reg.getTipo().esTipoReferencia()) {
                if (!ts.conforma(reg.getTipo().getNombreTipo(), expresion.chequear(ts).getNombreTipo()))
                    throw new ErrorSemantico(reg.tokenVarLocal, "Error Semantico, el tipo de la variable " + expresion.getToken().getLexema() + " no coincide con el tipo esperado " + reg.getTipo());

            } else {
                if (!reg.getTipo().equals(expresion.chequear(ts)))
                    throw new ErrorSemantico(reg.tokenVarLocal, "Error Semantico, el tipo de la variable " + expresion.getToken().getLexema() + " no coincide con el tipo esperado " + reg.getTipo());
            }
        }

        //3. Devolvemos el tipo de la clase que se quiere instanciar
        return new TipoReferencia(clase.getNombre());
    }
}
