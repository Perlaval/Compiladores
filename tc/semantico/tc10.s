class Calculadora{
    //A c; // si A no esta declarado en la consolidacion deberia lanzar error
}

impl Calculadora{
    .(){}

    // metodo que deberia largar error al declarar una variable local con una clase que no existe
    fn f(){
        //A c;
    }
}

// al igual que con el metodo start, deberia largar error si A no esta en la TS
start {
    A c;
    Int a;
    Str b;

}