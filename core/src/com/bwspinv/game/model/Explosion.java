package com.bwspinv.game.model;

/**
 * Created by wp90195 on 21/07/2017.
 */
public class Explosion {

    private final float intervalo = 1f / 17f;

    private float acomulado = 0;
    private int trabajo = 1;
    private float x;
    private float y;

    public void atualizar(float delta) {
        // acomula el tiempo recorrido entre un cuadro y otro
        acomulado += delta;
        // Comprueba si el acompañamiento es mayor o igual al intervalo configurado entre los intercambios

        if (acomulado >= intervalo) {
            acomulado = 0;
            trabajo = trabajo + 1;
        }
    }


    public int getEstaciones() {
        return trabajo;
    }

    public int getTrabajo() {
        return trabajo;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
