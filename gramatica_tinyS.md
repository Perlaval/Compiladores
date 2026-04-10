# Gramatica TinyS

## reglas de produccion
```
1. Program -> ListaDefiniciones Start
2. Start -> start BloqueMetodo
3. ListaDefiniciones -> Class ListaDefiniciones | Impl ListaDefiniciones | lambda
4. Class -> class idClass HerenciaOpt {ListaAtributos}
5. HerenciaOpt -> Herencia | lambda
6. ListaAtributos -> Atributo ListaAtributos | lambda
7. Impl -> impl idClass { ListaMiembros }
8.ListaMiembros -> Miembro ListaMiembros | lambda
9. Herencia -> Tipo
10. Miembro -> Metodo | Constructor
11. Metodo -> FormaMetodoOpt fn TipoMetodoOpt idMetAt ArgumentosFormales BloqueMetodo
12. FormaMetodoOpt -> formaMetodo | lambda
13. TipoMetodoOpt -> TipoMetodo | lambda
14. ArgumentosFormales -> ( ListaArgumentosFormalesOpt )
15. Constructor -> . ArgumentosFormales BloqueMetodo
16. Atributo -> VisibilidadOpt Tipo ListaDeclaracionVar ;
17. VisibilidadOpt -> Visibilidad | lambda
18. Tipo -> TipoPrimitivo | TipoReferencia | TipoArreglo
19. ListaDeclaracionVar -> idMetAt ListaDeclaracionesVarRec
20. ListaDeclaracionVarRec -> , ListaDeclaracionVar | lambda
21. BloqueMetodo -> { ListaDeclaracioVarLocal ListaSentencia }
22. ListaDeclaracionVarLocal -> DeclaracionVarLocal ListaDeclaracionVarLocal | lambda
23. ListaSentencia -> Sentencia ListaSentencia | lambda
24. Visibilidad -> pub
25. FormaMetodo -> st
26. TipoPrimitivo -> Str | Bool | Int
27. TipoReferencia -> idClass
28. TipoArray -> Array TipoPrimitivo
29. DeclaracionVarLocal -> Tipo ListaDeclaracionVar ;
30. ListaArgumentosFormalesOpt -> ListaArgumentosFormales | lambda
31. ListaArgumentosFormales -> ArgumentoFormal ListaArgumentosFormalesRec
32. ListaArgumentosFormalesRec -> , ListaArgumentosFormales | lambda
33. ArgumentoFormal -> Tipo idMetAt
34. TipoMetodo -> Tipo | void
35. Sentencia -> ; | Asignacion | SentenciaSimple ; | if ( Expresion ) SentenciaRec | while ( Expresion ) Sentencia |
36. for ( TipoPrimitivo idMetAt in idMetAt) Sentencia | Bloque | ret ExpresionOpt
37. SentenciaRec -> Sentencia RecursivoElse
38. RecursivoElse -> else Sentencia | lambda
39. ExpresionOpt -> Expresion | lambda
40. SentenciaSimple -> ( Expresion )
41. Expresion -> ExpresionOr
42. BLoque -> { ListaSentencia }
43. Asignacion -> AccesoVarSimple = Expresion | AccesoSelfSimple = Expresion
44. AccesoVarSimple -> id AccesoVarSImpleRec
45. AccesoVarSimpleRec -> ListaEncadenadoSImple | [ Expresion ]
46. ListaEncadenadoSimple -> EncadenadoSimpple ListaEncadenadoSimple | lambda
47. AccesoSelfSimple -> self ListaEncadenadoSimple
48. EncadenadoSimple -> . id
49. ExpresionOr -> ExpresionAnd ExpresionOrRec
50. ExpresionOrRec -> || ExpresionAnd ExpresionOrRec | lambda
51. ExpresionAnd -> ExpIgual ExpAndRec
52. ExpresionAndRec -> && ExpIgual ExpresionAndRec | lamnda
53. ExpresionIgual -> ExpresionComp ExpresionIgualRec
54. ExpresionIgualRec -> OpIgual ExpresionComp ExpresionIgualRec | lambda
55. ExpresionComp -> ExpresionAd ExpresionCompRec
56. ExpresionCompRec -> OpComp ExpresionAd | lambda
57. ExpresionMul -> ExpresionUnario ExpresionMulRec
58. ExpresionMulRec -> OpMul ExpresionUnario ExpresionMulRec | lambda
59. ExpresionUnario -> OpUnario ExpresionUnario | Operando
60. ExpresionAd -> ExpresionMul ExpresionAdRec
61. ExpresionAdRec -> OpAd ExpresionMul ExpresionAdRec | lambda
62. OpIgual -> == | !=
63. opComp -> < | > | <= | >=
64. opAd -> + | -
65. opUnario -> + | - | ++ | -- 
66. OpMul -> * | /
67. Operando -> Literal | Primario | EncadenadoOpt
68. EncadenadoOpt -> Encadenado | lambda
69. Literal -> nil | true | false | intLiteral | strLiteral
70. Primario -> ExpresionParentizada | AccesoSelf | AccesoVar | LlamadaMetodo | LlamadaMetodoEstatico | LlamadaConClassor
71. ExpresionParentizada -> ( Expresion ) EncadenadoOpt
72. AccesoSelf -> self EncadenadoOpt
73. AccesoVar -> id AccesoVarRec
74. AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
75. LlamadaMetdo -> id ArgumentosActuales EncadenadoOpt
76. LlamadaMetodoEstatico -> idClass . LlamadaMetodo EncadenadoOpt
77. LlamadaConClassor -> new LLamadaConClassOrRec
78. LlamadaConClassorRec -> idClass ArgumentosActuales EncadenadoOpt | TipoPrimitivo [ Expresion ]
79. ArgumentosActuales -> ( ListaExpresionesOpt )
80. ListaExpresionesOpt -> ListaExpresiones | lambda
81. ListaExpresiones -> Expresion ListaExpresionesRec
82. ListaExpresionesRec -> , ListaExpresiones | lambda
83. Encadenado -> . EncadenadoRec
84. EncadenadoRec -> LlamadaMetodo | AccesVa
````

## Primeros
````
Prim(Program)= {class, imp, lambda}
Prim(Start)= {start}
Prim(ListaDefiniciones)= {class, impl, lambda}
Prim(Class)= {class}
Prim(HerenciaOpt)= {str, bool, int, IdClass, Array}
Prim(ListaAtributos)= {pub, lambda}
Prim(Impl)= {impl}
Prim(Herencia)= {str, bool, int, idClass, Array}
Prim(Miembro)= {st, . , lambda}
Prim(ListaMiembro)= {st, . , lambda}
Prim(Constructor)= {.}
Prim(Atributo)= {pub, lambda}
Prim(VisibilidadOpt)= {pub, lambda}
Prim(Metodo)= {st, lambda}
Prim(FormaMetodoOpt)= {st, lambda}
Prim(TipoMetodoOpt)= {str, ool, int, idClass, lambda}
Prim(Visibilidad)= {pub}
Prim(FormaMetodo)= {st}
Prim(BloqueMetodo)= {{}
Prim(ListaDeclaracionVariablesLocal)= {str, bool, int, idClass, Array, lambda}
Prim(ListaSentencia)= {;, . , if, while, for, {, ret, lambda}
Prim(DeclaracionVariablesLocales)= {str, bool, int, idClass, Array}
Prim(ListDeclaracionVariablesRec)= { , , lambda}
Prim(ListaDeclaracionVariables)= {idMetVar}
Prim(ArgumentosFormale)= { ( }
Prim(ListaArgumntosFormalesOpt)= {str, bool, int, idClass, Array, lambda}
Prim(ListaArgumentosFormales)= {str, bool, int, idClass, Array}
Prim(ArgumentoFormal)= {str, bool, int, idCLass, Array}
Prim(TipoMetodo)= {str, bool, int, idClass, Array}
Prim(TipoArreglo)= {Array}
Prim(Tipo)= {str, bool int, idClass, Array}
Prim(TipoPrimitivo)= {str, bool, int}
Prim(TipoReferencia)= {idClass}
Prim(Sentencia)= {;, id, self, ), if, while, for, {, ret}
Prim(ExpresionOpt)= Prim(Expresion) U {lambda}
Prim(Bloque)= { { }
Prim(Asignacion)= {id, self}
Prim(AccesoVariableSimpleRec)= { . , [, lambda}
Prim(AccesoVariableSimple)= {id}
Prim(ListaEncadenadoSimple)= { . , lambda}
Prim(AccesoSelfSimple)= {self}
Prim(EncadenadoSimple)= { . }
Prim(SentenciaSimple)= { ( }
Prim(OperadorMul)= {*, /}
Prim(OperadorUnario)= {+, -, !, ++, --}
Prim(OperadorAd)= {+, -}
Prim(OperadorCompuesto)= {<, >, <=, >=}
Prim(OperadorIgual)= {==, !=}
Prim(ExpresionMulRec)= Prim(OperadorMul) U {lambda}
Prim(ExpresionMul)= Prim(OperadorUnario) U Prim(Operando)
Prim(ExpresionCompuesta)= Prim(ExpresionMul)
Prim(ExpresionIgualRec)= Prim(OperadorIgual) U {lambda}
Prim(ExpresionIgual)= Prim(ExpresionMul)
Prim(ExpresionAndRec)= {&&, lambda}
Prim(ExpresionAnd)= Prim(ExpresionMul)
Prim(ExpresionOrRec)= {||, lambda}
Prim(ExpresionOr)= Prim(ExpresionMul)
Prim(Expreison)= Prim(ExpresionMul)
Prim(ExpresionAdRec)= Prim(OperadorAd) U {lambda}
Prim(ExpresionAd)= Prim(ExpresionMul)
Prim(ExpresionUnario)= Prim(OperadorUnario) U {lambda}
Prim(Operando)= Prim(Literal) U Prim(Primario) U  {. , lambda}
Prim(EncadenadoOpt)= { . , lambda}
Prim(Literal)= {nil, true, false, intLiteral, strLiteral}
Prim(Primario)= {(, self, id, idClass, new}
Prim(ExpresionParentizada)= { ( }
Prim(AccesoSelf)= {self}
Prim(AccesoVariable)= {id}
Prim(AccesoVariableRec)= { . , [, lambda}
Prim(LlamadaMetodo)= {id}
Prim(LlamadaMetodoEstatico)= {idClass}
PSigrim(LlamadaConClassOr)= {new}
Prim(LlamadaConClassOrRec)= {idClass, str, bool, int}
Prim(ArgumentosActuales)= { ( }
Prim(ListaExpresionesOpt)= Prim(Expresion) U {lambda}
Prim(ListaExpresiones)= Prim(Expreison)
Prim(ListaExpresionesRec)= { , , lambda}
Prim(Encadenado)= { . }
Prim(EncadenadoRec)= {id}
````

## Siguientes
````
Sig(Program)= {$}
Sig(Start)= {$}
Sig(ListaDefiniciones)= {start, } } 
Sig(Class)= {class, impl, start}
Sig(HerenciaOpt)= { { }
Sig(ListaAtributod)= { } }
Sig(Impl)= {class, impl, start}
Sig(Herencia)= { { }
Sig(Miembro)= {st, . , } }
Sig(ListaMiembro)= { } }
Sig(Constructor)= {st, ., } }
Sig(Atributo)= {pub, } }
Sig(VisibilidadOpt)= {pub, idMetVar}
Sig(Metodo)= {st, . , } }
Sig(FormaMetodoOpt)= {fn}
Sig(TipoMetodoOpt)= {idMetVar}
Sig(Visibilidad)= {pub, idMetVar}
Sig(FormaMetodo)= {fn}
Sig(BloqueMetodo)= {$, st, . } }
Sig(ListaDeclaracionVariablesLocal)= { , , . , if, while, for, {, ret, } }
Sig(ListaSentencia)= { } }
Sig(DeclaracionVariablesLocales)= {str, bool, int, idClass, Array, ; , . , if, while, for, {, ret, } }
Sig(ListaDeclaracionVariablesRec)= { ; }
Sig(ListaDeclaracionVariables)= { ; }
Sig(ArgumentosFormale)= { { }
Sig(ListaArgumntosFormalesOpt)= { ) }
Sig(ListaArgumentosFormales)= { ) }
Sig(ArgumentoFormal)= { , ) }
Sig(TipoMetodo)= {idMetVar}
Sig(TipoArreglo)= {void, idMetVar}
Sig(Tipo)= {void, idMetVar}
Sig(TipoPrimitivo)= {void, idMetVar}
Sig(TipoReferencia)= {void, idMetVar}
Sig(Sentencia)= { ; , . , if, while, for, {, ret, }, else}
Sig(ExpresionOpt)= { ; , . , if, while, for, {, ret, }, else}
Sig(Bloque)= { ; , . , if, while, for, {, ret, }, else}
Sig(Asignacion)= { ; , . , if, while, for, {, ret, }, else}
Sig(AccesoVariableSimpleRec)= { = }
Sig(AccesoVariableSimple)= { = }
Sig(ListaEncadenadoSimple)= { = }
Sig(AccesoSelfSimple)= { = }
Sig(EncadenadoSimple)= { . , =}
Sig(SentenciaSimple)= { ; }
Sig(OperadorMul)= Sig(OperadorUnario)
Sig(OperadorUnario)= {+, -, !, ++, --, (, *, /, ==, !=, &&, ), else, ], , }
Sig(OperadorAd)= Prim(Expresion)
Sig(OperadorCompuesto)= Prim(Expresion)
Sig(OperadorIgual)= Prim(Expresion)
Sig(ExpresionMulRec)= {==, !=, &&, ||, ), else, ], ,}
Sig(ExpresionMul)= {==, !=, &&, ||, ), else, ], ,}
Sig(ExpresionCompuesta)= {==, !=, &&, ||, ), else, ], ,}
Sig(ExpresionIgualRec)= {&&, ), ||, else, ], , }
Sig(ExpresionIgual)= {&&, ), ||, else, ], , }
Sig(ExpresionAndRec)= {||, ), else, ], , }
Sig(ExpresionAnd)= {||, ), else, ], , }
Sig(ExpresionOrRec)= { ), else, ], , }
Sig(ExpresionOr)= { ), else, ], , }
Sig(Expresion)= { ), else, ], , } 
Sig(ExpresionAdRec)= {<, >, <=, >=, ==, !=, &&, ), ||, else, ], , }
Sig(ExpresionAd)= {<, >, <=, >=, ==, !=, &&, ), ||, else, ], , }
Sig(ExpresionUnario)= {*, /, ==, !=, &&, ), ||, else, ], , }
Sig(Operando)= Sig(OperadorUnario)
Sig(EncadenadoOpt)= Sig(Operando)
Sig(Literal)= Sig(Operando)
Sig(Primario)= Sig(Operando)
Sig(ExpresionParentizada)= Sig(Operando)
Sig(AccesoSelf)= Sig(Operando)
Sig(AccesoVariable)= Sig(Operando)
Sig(AccesoVariableRec)= Sig(Operando)
Sig(LlamadaMetodo)= Sig(Operando)
Sig(LlamadaMetodoEstatico)= Sig(Operando)
Sig(LlamadaConClassOr)= Sig(Operando)
Sig(LlamadaConClassOrRec)= Sig(Operando)
Sig(ArgumentosActuales)= Sig(Operando) U { . }
Sig(ListaExpresionesOpt)= { ) }
Sig(ListaExpresiones)= { ) }
Sig(ListaExpresionesRec)= { ) }
Sig(Encadenado)= Sig(Operando)
Sig(EncadenadoRec)= Sig(Operando)
````

























