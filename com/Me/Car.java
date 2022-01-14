package com.Me;
/*
 * Date: 03 August 2021
 * Filename: Car.java
 * Description: The Car class, a child class of Shapes, creates Car objects and provides functionality to draw them.
 * It initializes required fields, and calculates its a HashMap of respective lanes (height y in px) for later use.
 * Its overloaded draw() method draws the car in the form of a rectangle, with it's name imprinted on it's front, and
 * details (name, position, speed) printed beneath it in whichever lane it is supposed to be in.
 *
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class Car extends Shapes {
    private final String name;
    private int speed;
    public final double speedMultiplier = 10;
    private final int lane;
    private final HashMap<Integer, Integer> laneHeightY = new HashMap<>();

    protected Car(int x, int name, int speed, int lane, int numberOfLanes) {
        this.x = x;
        this.color = Color.BLACK;
        this.name = Integer.toString(name);
        this.speed = speed;
        if(lane > 5) // 5 Lanes Max
            this.lane = lane % 5;
        else
            this.lane = lane;

        for(int i = 0; i < numberOfLanes; i++)
            laneHeightY.put((i+1), (190+(i*40)));
    }

    protected void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle2D.Double rectangle = new Rectangle2D.Double(x, laneHeightY.get(lane), 40, 20);
        g2d.setColor(color);
        g2d.fill(rectangle);
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, (float) (x+35), laneHeightY.get(lane)+15);
        g2d.drawString(name + ": " + x + "m, " + (speed/speedMultiplier) + " kph", (float) x, laneHeightY.get(lane)+30);
    }
}
