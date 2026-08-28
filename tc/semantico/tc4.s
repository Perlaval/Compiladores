// en este verifico los metodos de las clases para la TS


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
impl B{ //como no tengo la clase B creada la creo para poder agregar el impl pero sin herencia entonces cuando llega a la linea 39 me dice que estoy haciendo "Redefinicion de herencia inconsistente"
    st fn Int m2(){
        Bool a;
    }
}
class B : A {
    Bool m2;
}
start{}