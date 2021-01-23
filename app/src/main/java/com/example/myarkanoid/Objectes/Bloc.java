package com.example.myarkanoid.Objectes;

public class Bloc {

    private float x;
    private float y;
    private int costat;
    private boolean visible;


    public Bloc(float x, float y, int costat) {
        this.x = x;
        this.y = y;
        this.costat = costat;
        this.visible = true; // Per defecte
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

    public float getCostat() {
        return costat;
    }

    public void setCostat(int ample) {
        this.costat = ample;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
