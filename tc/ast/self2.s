class Motor {
    Int potencia;
}

impl Motor {
    .() {
        Motor m; // esto es correcto, sintactica y semanticamente
        self.potencia = 100;
    }


    fn Int getPotencia() {
        ret self.potencia;
    }
}

class Auto {
    Motor motor;
}

impl Auto {
    .() {
        //self.motor = new Motor(); // hacer el new
    }

    //self.motor.potencia = 3;

    fn Int obtenerPotencia(Int x) {
        ret self.motor.getPotencia(); // verificar que los parametros coincidan con el metodo originl
    }

    fn Int obtenerPotencia2() {
        ret self.motor.potencia; // deberia fallar: potencia es privado y claseActual es Auto, no Motor
    }
}

start {
}