class A {}

impl A {
    .() {}
    fn m1(){}

    fn B retB(Int x) {
        if ( m1(x)){
        // if (== *5) { // deberia largar error sintactico (Ya lo larga)
        ret new B(0); // en el ast voy a verificar que esa B este en mi ts
        } else {
        ret new B(1);
        }
    }
}

class B : A {
}
impl B {
    .(){}
    fn Str f() {}
}

start {
    Int b;
    b = 4;
    c = b + b;
}
