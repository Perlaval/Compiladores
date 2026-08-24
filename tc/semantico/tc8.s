// en este verifico los metodos de las clases para la TS


class A {
    Int x;
    Bool z;
    Array Int a; //Array a; //linea 7 columna 11 se esperaba un tipo primitivo - ERROR!
}

class A {}

impl A {
    .(Int b, Bool z){
        //.(){} probar despues de arreglar el sintactico
        Array Int x;
        z = 1 //No me devuelve el error!!
    }

    fn C m5(){} //No crea la clase C

    st fn Int m2(Int c, Str y, Array Str hola /*,*/){ //Se esperaba un tipo para el parametro: ) -> se encontraba un tipo y se encontro )
        // defino variables locales para probar
        Bool d; //se pierde el comentario multilinea al entrar al comentario simple: Se esperaba llaveCierra y se enontro tBool
        Int v;
    }
    st fn m3(Int x, Str y){} //metodo que retorna void
    fn A m4(){} //metodo que retorna un objeto de la clase A


    fn Int getM2(){
        ret self.m2
    }

    fn getZ(){ //No tira error!
            ret z;
    }

}

impl A {
    //.(){}
}

class B : A {

    Str y; //Se esperaba llave que cierra y se encontro idMetVar

}
impl B{
    .(){}
    st fn Int m6(Int x){//Se esperaba parCierra y se encontro idMetVar
        Bool a;
    }
    st fn Str m7(){}
}

//class C : A {}
start{}