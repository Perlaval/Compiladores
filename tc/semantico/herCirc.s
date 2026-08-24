// herencia ciruclar
// deberia dar error porque A -> B -> C -> A
/*
class A : B {
}

impl A {
    .() {}
}

class B : C {
}

impl B {
    .() {}
}

class C : A {
}

impl C {
    .() {}
} */


// herencia de si misma
/*
class A : A {
}

impl A {
    .() {}
}*/

// herencia circular desde mas abajo que A
/*
class A {
}

impl A {
    .() {}
}

class B : C {
}

impl B {
    .() {}
}

class C : D {
}

impl C {
    .() {}
}

class D : B {
}

impl D {
    .() {}
}
*/

// herencia que no deberia largar error
class A {
}

impl A {
    .() {}
}

class B : A {
}

impl B {
    .() {}
}

class C : B {
}

impl C {
    .() {}
}

class D : C {
}

impl D {
    .() {}
}
start {}