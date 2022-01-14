package com.Me;
/*
 * Date: 03 August 2021
 * Filename: Shapes.java
 * Description: The Shapes class servers as a parent class for Car and TrafficLight, and provides fields and methods
 * that would otherwise be duplicated by those classes, as well as providing a draw() method intended to be overloaded
 * by both classes.
 */
import java.awt.Color;
import java.awt.Graphics;

public class Shapes {
    protected int x;
    protected Color color;
    protected void setX(int x) {
        this.x = x;
    }
    protected int getX() {
        return x;
    }
    protected void setColor(Color color) {
        this.color = color;
    }
    protected Color getColor() {
        return color;
    }
    public void draw(Graphics g){}
}
