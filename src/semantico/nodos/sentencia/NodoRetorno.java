package semantico.nodos.sentencia;

import lexico.Token;
import semantico.ErrorSemantico;
import semantico.TablaSimbolos;
import semantico.nodos.expresion.NodoExpresion;
import semantico.registros.RegistroMetodo;
import semantico.tipos.Tipo;
import semantico.visitor.Visitor;


public class NodoRetorno extends NodoSentencia{

    private NodoExpresion nodoExpresionOpt;

    public NodoRetorno(Token tRet, NodoExpresion nodoExpresionOpt) {
        super(tRet);
        this.nodoExpresionOpt = nodoExpresionOpt;
    }

    public NodoExpresion getNodoExpresionOpt() {
        return nodoExpresionOpt;
    }

    /*@Override
    public Tipo chequear(TablaSimbolos ts) throws ErrorSemantico {
        //System.out.println("Entre a chequear de ret");
        RegistroMetodo metodo = ts.getMetodoActual();
        Tipo tipoRet = metodo.getTipoRetorno();

        // ret;
        if (nodoExpresionOpt == null){
            // verifico que el retorno sea void
            if (!tipoRet.getNombreTipo().equals("Void")){
                throw new ErrorSemantico(token,
                        "El metodo: "+metodo.getNombre()+ " deberia retornar: "+metodo.getTipoRetorno().getNombreTipo());
            }
            return tipoRet;
        }

        // ret expresionOpt;
        // el ret de expresionOpt debe coincidir con tipoRet

        //System.out.println("Expresion del ret: " + nodoExpresionOpt.getClass().getSimpleName());

        Tipo tipoExpresion = nodoExpresionOpt.chequear(ts);
        if (tipoExpresion == null){
            throw new ErrorSemantico(token,
                    "El metodo: "+ts.getMetodoActual().getNombre()+", deberia retornar: "+ts.getMetodoActual().getTipoRetorno().getNombreTipo());
        }
        //System.out.println("Retorno de la expresion: "+tipoExpresion.getNombreTipo());
        // si no devuelve null debe devolver el mismo tipo
        if (!tipoRet.getNombreTipo().equals(tipoExpresion.getNombreTipo())){
            throw new ErrorSemantico(token,
                    "Se esperaba un retorno: "+ts.getMetodoActual().getTipoRetorno().getNombreTipo()+", y se recibio: "
                            +tipoExpresion.getNombreTipo());
        }
        return tipoRet;
    }*/

    @Override
    public void accept(Visitor visitor) throws ErrorSemantico {
        visitor.visit(this);
    }

}
