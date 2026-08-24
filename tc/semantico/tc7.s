// en este verifico los metodos de las clases para la TS


class A  {
    Int m2;
    Bool z;
    Array Int a;
}

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
        ret 2;
    }
    //metodo que retorna void
    st fn m3(Int x, Str y){
        ret;
    }

    //metodo que retorna un objeto de la clase A
    fn A m4(){
        ret a;
    }

    fn A m5(){
        ret a;
    }

    fn Int getM2(){
        ret self.m2
    }

    fn Int getZ(){
        ret z;
    }

}

impl A {
    //.(){}
}
impl B{
    .(){}
    st fn Int m6(Int x){
        Bool a;
        ret x;
    }
    // metodo con el mismo nombre que A
    st fn Str m3(Int a, Str y){
        ret y;
    }
    fn Int getZ(){
        ret z;
    }
}
impl C{
    .(){}
}
class C : B{
    Int dia;

}
class B : A{
    Int x;
}

start{}