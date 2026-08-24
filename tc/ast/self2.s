class Motor {
    Int potencia;
}

impl Motor {
    .() {
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
}

start {
}