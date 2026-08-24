// para probar el chequeo de retorno

class A  {
    Int m2;
    Bool z;
    Array Int a;
}

impl A {
    .(){}

    //metodo que retorna Bool
    st fn Bool m3(Int x, Str y){
        ret true;
    }
    // metodo que retirna void
    st fn m2(){
        ret;
    }
    // metodo que retorna Int
    st fn Int m4(Int x){
        ret 2;
    }
    // metodo que retrna Str
    fn Str m6(){
        ret "hola";
    }
}
start{}