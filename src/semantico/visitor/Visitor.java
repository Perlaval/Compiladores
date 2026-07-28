package semantico.visitor;

import semantico.ErrorSemantico;
import semantico.nodos.declaraciones.*;
import semantico.nodos.definiciones.*;
import semantico.nodos.expresion.*;
import semantico.nodos.miembro.*;
import semantico.nodos.programa.*;
import semantico.nodos.sentencia.*;

public interface Visitor {

    void visit(NodoProgram nodo) throws ErrorSemantico;
    void visit(NodoStart nodo) throws ErrorSemantico;
    void visit(NodoClase nodo) throws ErrorSemantico;
    void visit(NodoImpl nodo) throws ErrorSemantico;
    void visit(NodoMetodo nodo) throws ErrorSemantico;
    void visit(NodoBloqueMetodo nodoBloqueMetodo) throws ErrorSemantico;

    void visit(NodoAsignacion nodo) throws ErrorSemantico;
    void visit(NodoBloque nodo) throws ErrorSemantico;
    void visit(NodoFor nodo) throws ErrorSemantico;
    void visit(NodoIf nodo) throws ErrorSemantico;
    void visit(NodoRetorno nodo) throws ErrorSemantico;
    void visit(NodoSentencia nodo) throws ErrorSemantico;
    void visit(NodoSentenciaSimple nodo) throws ErrorSemantico;
    void visit(NodoWhile nodo) throws ErrorSemantico;

}
