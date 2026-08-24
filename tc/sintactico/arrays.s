class Persona {
    Int edad;
    Str nombre;
}

impl Persona {

    .() {
        edad = 0;
        nombre = "";
    }

    fn Int getEdad() {
        //ret edad;
    }

    fn setEdad(Int e) {
        edad = e;
    }
}

class Lista {
    Array Int numeros;
    Array Bool estados;
    Array Str nombres;
}

impl Lista {

    .() {
    }

    fn Int suma(Array Int v, Int pos) {

        if (v[pos] > 0 && pos < v.length()) {
            ret v[pos];
        }
        else {
            ret 0;
        }
    }

    fn Bool test(Array Bool b, Int i) {

        while (i < b.length()) {
            if (b[i]) {
                ret true;
            }
            i = i + 1;
        }

        ret false;
    }
}

start {

    Array Int numeros;
    Array Bool estados;
    Array Str nombres;

    Lista l;

    l = new Lista();

    numeros = new Int[10];
    estados = new Bool[20];
    nombres = new Str[5];

    //nombres = v[a + b * 2];

    numeros[0] = 10;
    numeros[1] = numeros[0] + 5;

    estados[3] = true;

    nombres[2] = "Juan";

    if (numeros[0] < numeros[1] && estados[3]) {
        (IO.out_int(numeros[1]));
    }

    while (numeros[0] < 100) {
        numeros[0] = numeros[0] + 1;
    }

    (IO.out_int(l.suma(numeros, 2)));
    (IO.out_bool(l.test(estados, 0)));

}