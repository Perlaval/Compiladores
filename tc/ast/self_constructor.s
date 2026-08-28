class UnaClase {
    Str greeting;
}

impl UnaClase {
    .(Str greeting) {
        self.greeting = "hola";
    }
}

start {}