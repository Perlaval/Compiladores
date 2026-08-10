class Nodo {

    Nodo sig;
    Int valor;

}

impl Nodo {

    .() {
    }

    fn Nodo siguiente() {
       // ret sig;
    }

    fn Int getValor() {
      //  ret valor;
    }

    fn setValor(Int x) {
        valor = x;
    }

    fn Bool test() {

        if (siguiente().getValor() > 0) {

            // setValor(10); // larga error, desde bloque no puedo llamar a una funcion, porque bloque llama a senten cia y sentencia si lee in idMetVar va a asignacion

            /* siguiente().setValor(
                getValor() + siguiente().getValor()
            ); */

        }

        ret true;
    }

}

start {

    Nodo n;

    n = new Nodo();

    //(n.test());

}