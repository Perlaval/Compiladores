/*
// Array como ATRIBUTO---------------------------------------------------------------------------------
class Motor {
    Array Int potencias;
}
impl Motor {

    fn Int getPrimera() {
       ret self.potencias[0];  // OK -> debe devolver tInt
    }
}
impl Motor {
    .(){}
}
start{
    Array Int a;
   // a = new Int[5]; // no tengo el new implementado
    a[0] = 2;
    a[1] = 2;
    a[2] = 4;
    a[3] = 6;
    a[4] = 8;
    for (Int i in a){
    (IO.out_int(i)); // resolver estos llamados, deberia funcion todo ok, y romperse sino ( por ahora no hace ninguno de los dos)
    (IO.out_str("\n"));
    }
} */


/*
// Array como PARÁMETRO del método----------------------------------------------------------
class Utilidad {

}
impl Utilidad {
    fn Int sumarElemento(Array Int datos, Int x) {
        ret datos[x];  //
    }
}
impl Utilidad {
    .(){}
}
start {} */

/*
//  Array como VARIABLE LOCAL del método ----------------------------------------------
class Utilidad {

}
impl Utilidad {
    fn Int calcular() {
        Array Int temp;
        Int i;
        i = 2;
        ret temp[i];  // OK
    }

}
impl Utilidad {
    .(){}
}
start{}

*/

/*
// ERROR -> el id NO es de tipo Array----------------------------------------------------------
class Persona {
    Array Int edad;
}
impl Persona {
    fn Int test() {
        Bool flag;
        //ret self.edad[0]; // ERROR esperado: "El id: edad debe ser un Array"
        //ret edad[flag]; // error porque el indice debe ser int
        ret auto[0]; // error auto no esta definido
    }
}
impl Persona{
    .(){}
}
start {}

*/


//  variable local debe ganarle al atributo
class Test7 {
    Array Int datos;   // atributo, longitud/tipo A
}
class Test8 {
    Test7 test7;
}
impl Test8 {
    .(){}
}

impl Test8 {
    fn Int test(Int x) {
        //Bool datos;  // variable local le gana al atributo de la clase, entonces aca deberia largar error porq ahora datos es un bool
        ret self.test7.datos[0 + x];
    }
}
impl Test7 {
    .(){}
}
start {}




