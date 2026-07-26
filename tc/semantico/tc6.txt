// en este verifico los metodos de las clases para la TS

// verifico que la clase de la que hereda existe

class A {
    Int m2;
    Bool z;
    Array Int a;
}

class A {}

impl A {
    .(Int b, Bool z){
        //.(){} probar despues de arreglar el sintactico
        Array Int x;
        z = 1;

    }

    st fn Int m2(Int c, Str y, Array Str hola, A m2){
        // defino variables locales para probar
        Bool d;
        Int v;
    }
    st fn m3(Int x, Str y){} //metodo que retorna void
    fn A m4(){} //metodo que retorna un objeto de la clase A
    fn A m5(){}

}

impl A {
    //.(){}
}
impl B{
    .(){}
    st fn Int m2(Int x){
        Bool a;
    }
}

/*class B: C { // C no existe deberia lanzar error

}

class C {}
impl C{
.() {}
} */
class B {} // deberia largar error porque B ya puse que hereda de C

start{}