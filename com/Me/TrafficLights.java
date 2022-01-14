package com.Me;
/*
 * Date: 03 August 2021
 * Filename: TrafficLights.java
 * Description: TrafficLights is a class extending JPanel and implementing Runnable, the constructor will initialize
 * required fields, a JPanel (itself) using input parameters, and create a number of TrafficLight objects using the
 * input parameters. The constructor also has the capability to set standard distances (1000m or 200px) between
 * TrafficLight objects, or use random distancing. Upon being ran, the run() method will run a check to see whether the
 * Thread is being started from a pause, or not, and set ending times respectively. It then proceeds to set countdown
 * timers above each TrafficLight object, and upon 0s, change their color. The class also has the ability to stop the
 * Thread naturally using the pauseLights() method. It also has a method, getLightStatuses(), used to communicate with
 * the CarsTraversing Thread by returning an array of light positions, and ability for a car to drive, for each light.
 */
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TrafficLights extends JPanel implements Runnable {
    private final ArrayList<Shapes> trafficLights = new ArrayList<>();
    private final int numberOfLights;
    private final int[] lightDurations;
    private final JTextField[] lightTimeTextFields;
    private final double[] timesElapsed;
    private final long[] endTimes;
    private final HashMap<Color, Color> colorChanges = new HashMap<>();
    private final Random random = new Random();
    private boolean isPaused = false;

    // Constructor
    TrafficLights(int numberOfLights, boolean randomSpacing){
        this.numberOfLights = numberOfLights;
        lightDurations = new int[numberOfLights];
        timesElapsed = new double[numberOfLights];
        endTimes = new long[numberOfLights];
        lightTimeTextFields = new JTextField[numberOfLights];
        for(int i = 0; i < numberOfLights; i++)
            lightTimeTextFields[i] = new JTextField();

        colorChanges.put(Color.GREEN, Color.YELLOW);
        colorChanges.put(Color.YELLOW, Color.RED);
        colorChanges.put(Color.RED, Color.GREEN);

        setBackground(Color.BLUE);
        setPreferredSize(new Dimension(1000, 400));
        setMinimumSize(new Dimension(1000, 400));
        setLayout(null);
        setBounds(0,0,1000,400);

        int[] tempArr; // Set Spacing for Lights
        if(randomSpacing)
            tempArr = chooseRandomLightXValues();
        else
            tempArr = chooseLightXValues();

        for(int i = 0; i < numberOfLights; i++){
            trafficLights.add(new TrafficLight(tempArr[i]));
            lightTimeTextFields[i].setBounds(tempArr[i]-15, 55, 55, 30);
            add(lightTimeTextFields[i]);
            lightDurations[i] = random.nextInt(10)+5;
            repaint();
        }
    }
    private int[] chooseLightXValues(){
        // Set All Distances 1000m Apart. 200px = 1000m
        int[] lightXValues = new int[numberOfLights];
        lightXValues[0] = (random.nextInt(100)+50);
        for(int i = 1; i < numberOfLights; i++)
            lightXValues[i] = lightXValues[i-1]+200;
        return lightXValues;
    }
    private int[] chooseRandomLightXValues(){
        int[] lightXValues = new int[numberOfLights];
        for(int i = 0; i < numberOfLights; i++){ // Rule: Random X Values at least 70 Apart
            int tempX;
            while (true){
                tempX = random.nextInt(929)+50;
                boolean valid = true;
                for(int j = 0; j < numberOfLights; j++){ // Search for Value that Invalidates Rule
                    if (Math.abs(tempX - lightXValues[j]) < 70) {
                        valid = false;
                        break;
                    }
                }
                if(valid)
                    break;
            }
            lightXValues[i] = tempX;
        }
        return lightXValues;
    }

    private void changeColor(int light){
        Color currentColor = trafficLights.get(light).getColor();
        trafficLights.get(light).setColor(colorChanges.get(currentColor));
        repaint();
    }

    private double elapsedTimeDeci(int light){
        long current = System.currentTimeMillis();
        return ((endTimes[light] - current) / (float)100);
    }

    protected void pauseLights(){
        isPaused = true;
        for(int i = 0; i < numberOfLights; i++)
            timesElapsed[i] = (lightDurations[i]*10) - elapsedTimeDeci(i);
    }

    protected int[] getLightsStatus(){
        // Format: [position, <1 or 0>, ...] (1 or 0 Correspond to Cars Ability to Drive)
        int[] statuses = new int[2*numberOfLights];
        int statusIndex = 0;
        for(Shapes trafficLight : trafficLights){
            statuses[statusIndex] = trafficLight.getX();
            statusIndex++;
            if(Color.RED.equals(trafficLight.getColor()))
                statuses[statusIndex] = 0;
            else
                statuses[statusIndex] = 1;
            statusIndex++;
        }
        return statuses;
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for(Shapes shape : trafficLights){
            shape.draw(g);
        }
    }

    @Override
    public void run() {
        isPaused = false;
        // Thread Has Not Been Paused
        boolean allTimesZero = true;
        for(int i = 0; i < numberOfLights; i++)
            if (timesElapsed[i] != 0)
                allTimesZero = false;

        if(allTimesZero) {
            for(int i = 0; i < numberOfLights; i++)
                endTimes[i] = System.currentTimeMillis() + (lightDurations[i]*1000);
        } else { // Thread Has Been Paused
            for(int i = 0; i < numberOfLights; i++)
                endTimes[i] = (long) (System.currentTimeMillis() + (lightDurations[i]*1000) - (timesElapsed[i]*100));
        }
        while (!isPaused) {
            for(int i = 0; i < numberOfLights; i++) {
                lightTimeTextFields[i].setText(((int) elapsedTimeDeci(i) / 10) + " : " + ((int) elapsedTimeDeci(i) % 10) + " s");
                if(elapsedTimeDeci(i) <= 0){
                    endTimes[i] = System.currentTimeMillis() + (lightDurations[i]*1000);
                    changeColor(i);
                }
            }
        }
    }
}
