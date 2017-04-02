package com.andresflores.taxicementerio;

/**
 * Created by Andres on 01-04-2017.
 */

public class Pedido {

    private String Conductor ;
    private String Creado;
    private String USER_ID;


    public String getConductor() {
        return Conductor;
    }

    public void setConductor(String conductor) {
        Conductor = conductor;
    }

    public String getCreado() {
        return Creado;
    }

    public void setCreado(String creado) {
        Creado = creado;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }
}
