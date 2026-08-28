class Calculadora {
    Int resultado;
    Bool estado;

}

impl Calculadora {
    .(Int x) {
        resultado = 0;
        estado = true;  // aca me devuelve error, arreglarlo
    }

    fn Int calcular(Int x, Int y, Bool t) {
        // Prueba de precedencia
        if ((x + y) + 2 / -5 > 10 && !estado || x == y ) {
            ret ++x; // Operador unario prefijo
        } else {
            ret --y;
        }
/*
        if(x>2) while(x<2){
            ret true;
        }*/

    }
}

start {
    Calculadora c;
    c = new Calculadora();
    //(IO.out_int(c.calcular(10, 5))); // aca larga error, hay que analizar y resolver start

}