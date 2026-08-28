// aca voy a guardar las cosas que fui eliminando del codigo sintactico por si queremos volver atras

// AccesoVar -> id AccesoVarRec
    /*private NodoAccesoVar accesoVar(Tipo tipoContexto) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
        NodoId nodoId;
        //1. Solo entra aqui cuando tipoContexto == null
        // SI tipoCOntexto == null es la primera vez que entro a accesoVar
        if (tipoContexto == null){
            // En la primera interacion tengo que chequear que el id1 sea alguno de estos:
            //1.1 Una variable local del metodo
            //1.2 Un parametro del metodo
            //1.3 Un atributo pub de la clase
            if (ts.noEstaTs(token.getLexema())){
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "La variable " + token.getLexema() + " no ha sido declarada como variable local o parametro del metodo actual y tampoco es un atributo publico de la clase "+ ts.claseActual.getNombre() );
            }

            RegistroVariable variable = ts.getVariable(token.getLexema());
            nodoId = new NodoId(token.getFila(), token.getColumna(), variable);
            tipoContexto = nodoId.getTipoSintetizado();

        }
        //2. Si tipoContexto != null -> se accedio a este metodo de manera recursiva por medio de encadenadoOpt
        // por lo que estariamos en el cuerpo del encadenado: id1.id2.id3.id4 -> estariamos evaluando los ids del 2 al 4
        else {

            //3. Verificamos que el id pertenezca a un atr del tipo contexto:
            //3. Si tipoContexto es tipoReferencia entonces tenemos que buscar en la clase de tipoContexto el atributo del token actual
            //3.1 Si la variable no es un atributo publico de tipoContexto:
            if (tipoContexto.esTipoReferencia() && ts.noEstaTs(tipoContexto.getNombreTipo(), token.getLexema())) {
                throw new ErrorSemantico(token.getFila(), token.getColumna(), "La variable " + token.getLexema() + " no es un atributo de la clase " + tipoContexto.getNombreTipo() + " o su visibilidad es privada");
            }

            //4. Actualizamos el tipo contexto para tener el de id2
            //4.1 Si la variable es un atributo visible de tipoContexto, la busco:
            RegistroVariable variable = ts.getAtrDeClase(ts.getClase(tipoContexto.getNombreTipo()), token.getLexema());

            System.out.println("VARIABLW EN CLASE: " + ts.getClase(tipoContexto.getNombreTipo()).getNombre());
            System.out.println("VARIABLE EN ACCESOVAR: " + variable.getNombre());

            //2.1.2 Creamos el nodoId con los datos de la variable y el nro de fila y columna en el que se encuentra
            nodoId = new NodoId(token.getFila(), token.getColumna(), variable);

            //Actualizamos el tipoContexto
            //tipoContexto = nodoId.getTipoSintetizado();



        }

        match("idMetVar");


        //4. Llamo a nodoAccesoVarRec
        //Le paso el tipoContexto para que haga los chequeos correspondientes en caso de que haya encadenado
        NodoAccesoVarRec nodoAccesoVarRec = accesoVarRec(tipoContexto);
        NodoAccesoVar nodoAccesoVar = new NodoAccesoVar(nodoId, nodoAccesoVarRec);

        System.out.println("EN ACCESOVAR TIPO HEREDADO1 : " + nodoId.getTipoSintetizado().getNombreTipo());
        System.out.println("EN ACCESOVAR LINEA : " + nodoId.getNroLinea());

        /*if (tipoContexto.esTipoReferencia()){
            nodoAccesoVar.setTipoHeredado(ts.tablaClases.get(nodoId.getTipoSintetizado().getNombreTipo()));
            System.out.println("EN ACCESOVAR TIPO HEREDADO: " + nodoId.getTipoSintetizado().getNombreTipo());
        }*/




        //4. una vez que tengo los dos nodos que conforman al nodoAccesoVar hago el cheque para el caso:
        //AccesoVar -> id AccesoVarRec; AccesoVarRec -> [Expresion] EncadenadoOpt
        //nodoAccesoVar.chequear();
        //nodoAccesoVar.setTipoSintetizado(nodoId.getTipoSintetizado());
        //return nodoAccesoVar;
    //}

    //AccesoVarRec -> EncadenadoOpt | [ Expresion ] EncadenadoOpt
    /*private NodoAccesoVarRec accesoVarRec(Tipo tipoContexto) throws ErrorSintactico, ErrorLexico, ErrorSemantico {
            NodoAccesoVarRec nodoAccesoVarRec;
            if (token.getTipo().equals("corcheteAbre")){
                match("corcheteAbre");
                NodoExpresion nodoExpresion = expresion();
                match("corcheteCierra");
                //encadenadoOpt();

                NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt(tipoContexto);
                //2. Verifico que el tipoContexto actual de id sea array
                if (nodoEncadenadoOpt == null){
                    // AccesoVarRec -> [expresion]
                    nodoAccesoVarRec = new NodoAccesoVarRec(nodoExpresion);
                    nodoAccesoVarRec.chequear(tipoContexto);
                    return nodoAccesoVarRec;
                }

                //AccesoVarRec -> [Expresion] EncadenadoOpt
                //Este caso solo tiene sentido si EncadenadoOpt -> Encadenado -> EncadenadoRec -> LlamadaMetodo
                nodoAccesoVarRec = new NodoAccesoVarRec(nodoExpresion, nodoEncadenadoOpt);
                nodoAccesoVarRec.chequear(tipoContexto);
                return nodoAccesoVarRec;

            }
            else {
                //AccesoVarRec -> EncadenadoOpt
                NodoEncadenadoOpt nodoEncadenadoOpt = encadenadoOpt(tipoContexto);
                nodoAccesoVarRec = new NodoAccesoVarRec(nodoEncadenadoOpt);
                return nodoAccesoVarRec;

            }

        }*/

// Cambio todos los primarios para usar equals y no ==, enmtonces trabajo con el token entero no con el string
// cambio esto:
private boolean esPrimeroOperando(Token token){
        // token.getTipo().equals("")
        /*return tipo == "prNil" | tipo == "prTrue" | tipo == "prFalse" | tipo == "literal_entero" | tipo == "literal_cadena" |
                tipo == "parAbre" | tipo == "prSelf" | tipo == "idMetVar" | tipo == "idClass" | tipo == "prNew" | tipo == "pto";*/
// por esto:
private boolean esPrimeroOperando(Token token){
    return token.getTipo().equals("prNil") || token.getTipo().equals("prTrue") || token.getTipo().equals("prFalse")
                || token.getTipo().equals("literal_entero") || token.getTipo().equals("literal_cadena") || token.getTipo().equals("parAbre")
                || token.getTipo().equals("prSelf") || token.getTipo().equals("idMetVar") || token.getTipo().equals("idClass")
                || token.getTipo().equals("prNew") || token.getTipo().equals("pto");

// simplifique tipo primitivo, esto tenia antes:
/*
        switch (token.getTipo()){
            case "tStr":
                match("tStr");
                return new TipoPrimitivo("tStr");
            case "tBool":
                match("tBool");
                return new TipoPrimitivo("tBool");
            case "tInt":
                match("tInt");
                return new TipoPrimitivo("tInt");
            default:
                throw new ErrorSintactico(token.getFila(), token.getColumna(),
                        "Se esperaba un tipo primitivo (Int, Str, Bool), y se recibio: "+token.getLexema());
        }*/

// Simplifique el metodo Literal()
/*case "prFalse":
                NodoBool nodoBoolFalse = new NodoBool(token.getFila(), token.getColumna(), token.getLexema());
                match("prFalse");
                return nodoBoolFalse;*/