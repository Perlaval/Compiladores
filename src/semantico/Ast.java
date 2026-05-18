package semantico;

import semantico.nodos.Nodo;
import semantico.nodos.NodoProgram;

public class Ast {

    NodoProgram nodoRaiz;

    public Ast(NodoProgram nodoRaiz) {
        this.nodoRaiz = nodoRaiz;
    }

    public Ast() {
    }
}
