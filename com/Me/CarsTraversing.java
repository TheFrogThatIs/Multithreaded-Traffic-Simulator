package com.Me;
/*
 * Date: 03 August 2021
 * Filename: CarsTraversing.java
 * Description: CarsTraversing is a class extending JPanel and implementing Runnable, which takes in a TrafficLights
 * object for communication between their respective Threads. The constructor sets initial required fields, includes
 * parameters for its JPanel, then initializes a number of Car objects in the first half of the simulation. For speeds
 * of cars, the program sets their speeds to 10*KPH for the sake of simulation time, but the timing for each cars
 * traversal across the screen is accurate when accounting for this speed increase. The class has a calculatePositions()
 * method which uses the speed formula: x = x0 + vt, to calculate each cars respective distance in pixels, converting
 * velocity from KPH to px/s using a conversion constant. The elapsedTimeSeconds() method is used to obtain time in
 * seconds. The pauseCars() method stops the Thread naturally, and saves the position of each car. The run() method
 * sets required values, checks if the Thread is starting from a pause and if so resets the state of each car, then
 * runs in a while-loop calculating Car positions, retrieving light statuses, checking light statuses for a stop/go
 * condition, and setting the respective car to that condition. The end of the while-loop also has an if-statement
 * to reset the car to the beginning of the panel if it reaches the end.
 */
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CarsTraversing extends JPanel implements Runnable {
    private final ArrayList<Car> carsArray = new ArrayList<>();
    private final int[] carsStartingX, carsSpeed; // x = Pixels (px), Speed = 10*KPH (for visual)
    private final boolean[] isCarStopped;
    private final long[] carStartTimes;
    private final TrafficLights trafficLights;
    private final int numberOfCars;
    private final double conversionConstant = 0.055555556; // Converts Kph to px/s by (1000m/3600s)(200px/1000m)
    private int[] lightStatuses;
    private boolean isPaused = false;

    // Constructor
    CarsTraversing(TrafficLights trafficLights, int numberOfCars){
        this.trafficLights = trafficLights;
        this.numberOfCars = numberOfCars;
        isCarStopped = new boolean[numberOfCars];
        carStartTimes = new long[numberOfCars];
        carsStartingX = new int[numberOfCars];
        carsSpeed = new int[numberOfCars];

        setPreferredSize(new Dimension(1000, 400));
        setMinimumSize(new Dimension(1000, 400));
        setLayout(null);
        setBounds(0,0,1000,400);
        setOpaque(false);

        // Create Cars
        Random random = new Random();
        for(int i = 0; i < numberOfCars; i++){
            carsStartingX[i] = random.nextInt(500)+30; // Pixels (px); Start in First Half
            carsSpeed[i] =  random.nextInt(800)+200; // 10*KPH, Range = 200 - 250
            carsArray.add(new Car(carsStartingX[i], (i+1), carsSpeed[i], (i+1), numberOfCars));
            repaint();
        }
    }

    private void calculatePositions(){
        // Formula: x = x0 + vt
        for(Car car : carsArray){
            int index = carsArray.indexOf(car);
            if(!isCarStopped[index]) { // Car is Not Stopped
                int position = (int)(carsStartingX[index] + (carsSpeed[index]*conversionConstant*elapsedTimeSeconds(index)));
                car.setX(position);
            }
        }
    }

    private double elapsedTimeSeconds(int car){
        long current = System.currentTimeMillis();
        return ((current - carStartTimes[car]) / (float)1000);
    }

    protected void pauseCars(){
        isPaused = true;
        for(int i = 0; i < numberOfCars; i++) {
            carsStartingX[i] = carsArray.get(i).getX();
            isCarStopped[i] = true;
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for(Car car : carsArray){
            try {
                car.draw(g);
            } catch (NullPointerException ignored){}
        }
    }

    @Override
    public void run() {
        Arrays.fill(isCarStopped, false);
        Arrays.fill(carStartTimes, System.currentTimeMillis());
        // Reset Car Values After Pause
        if(isPaused)
            for(Car car : carsArray){
                int index = carsArray.indexOf(car);
                car.setX(carsStartingX[index]);
                car.setSpeed(carsSpeed[index]);
            }
        isPaused = false;

        while (!isPaused) {
            calculatePositions();
            lightStatuses = trafficLights.getLightsStatus();
            for(Car car : carsArray){
                int index = carsArray.indexOf(car);
                for(int i = 0; i < lightStatuses.length; i+=2){ // Iterate Over Statuses Array
                    if((car.getX())+40 == lightStatuses[i] && lightStatuses[i+1] == 0){ // Stop at Lights Condition
                        car.setSpeed(0);
                        isCarStopped[index] = true;
                        carsStartingX[index] = car.getX();
                        carStartTimes[index] = System.currentTimeMillis();
                        repaint();
                    } else if((car.getX())+40 == lightStatuses[i] && lightStatuses[i+1] == 1) { // Go at Lights Condition
                        car.setSpeed(carsSpeed[index]);
                        isCarStopped[index] = false;
                        repaint();
                    }
                }
                if((car.getX())+40 >= 1000){ // Reset Cars to Beginning of Panel
                    car.setX(1);
                    carsStartingX[index]= 1;
                    carStartTimes[index] = System.currentTimeMillis();
                }

            }
            repaint();
        }
    }
}
