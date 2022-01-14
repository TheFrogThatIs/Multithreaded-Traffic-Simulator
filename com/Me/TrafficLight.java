package com.Me;
/*
 * Date: 03 August 2021
 * Filename: TrafficLight.java
 * Description: The TrafficLight class creates TrafficLight objects. It's constructor initializes them by default
 * to Color.RED. Its overloaded draw() method draws a circle and vertical rectangle beneath it as the light, in its
 * respective Color.
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class TrafficLight extends Shapes {
    protected TrafficLight(int x){
        this.x = x;
        this.color = Color.RED;
    }
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Ellipse2D.Double circle = new Ellipse2D.Double(x, 100, 20, 20);
        Rectangle2D.Double rectangle = new Rectangle2D.Double((x+5), 125, 10, 30);
        g2d.setColor(color);
        g2d.fill(circle);
        g2d.fill(rectangle);
    }
}
