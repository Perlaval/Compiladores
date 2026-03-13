// ver que se reconocen los metodos correctamente

class B {
    Bool x;
    pub Otra y;
}

impl B {
    .(Int x) {
        if (x > 5) {
        self .x = false ;
        }
        else {
            self .x = true ;
        }
        self .y = new Otra();
    }
}

// salida esperada: exitosa