package semantico;

import semantico.nodos.programa.NodoProgram;

public class Ast {

    NodoProgram nodoRaiz;

    public Ast(NodoProgram nodoRaiz) {
        this.nodoRaiz = nodoRaiz;
    }

    public Ast() {
    }
}
