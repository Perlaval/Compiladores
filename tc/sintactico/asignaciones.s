class Nodo {
    Int valor;
    Nodo sig;
    Array Int datos;
}

impl Nodo {

    .() {
    }

    fn Int getValor() {
       // ret valor;
       ret 2;
    }

    fn test() {

        valor = 10;

        sig = new Nodo();

        datos = new Int[20];

        datos[0] = valor;

        datos[1] = datos[0] + 5;

        sig.valor = datos[1];

        sig.sig = new Nodo();

        sig.sig.valor = sig.getValor() + datos[0];

        datos[valor] = sig.sig.valor;

    }
}

start {

    Nodo n;

    n = new Nodo();

    n.valor = 5;

    n.datos = new Int[10];

    //n.datos[0] = 20; // aca larga error porque no existe ese encadenado, es id.id no hay id.expresion[]

    n.sig = new Nodo();

    n.sig.valor = n.datos[0];

}