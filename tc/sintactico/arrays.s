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

        if (v[pos] > 0 && pos < v.length()) { // llamada metodo de un metodo de iterator que lo implementa array
            ret v[pos];
        }
        else {
            ret 0;
        }
    }

    fn Bool test(Str b, Int i) {
        Int a;
        while (i < b.length()) {
            if (i == 1) {
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
    Str b;
    Str palabra;
    Lista l;

    palabra = b.concat("Ad");

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
    (IO.out_bool(l.test("2", 0)));

}