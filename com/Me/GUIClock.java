package com.Me;
/*
 * Date: 03 August 2021
 * Filename: GUIClock.java
 * Description: GUIClock is a small class, extending JTextField and implementing Runnable, that once ran will display
 * a running timer/stopwatch in itself. It uses a method to calculate the number of deci-seconds from its start, and
 * set it's text in seconds and deci-seconds. It has a method to pause the clock, or if a reset condition, reset the
 * initial start time.
 */
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import java.awt.Font;

public class GUIClock extends JTextField implements Runnable {
    private long startTime;
    private boolean isPaused = false;
    private double pauseTime = 0;

    // Constructor
    GUIClock(Border border) {
        setFont(new Font("Dialog", Font.BOLD, 20));
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(border);
    }

    private double elapsedTimeDeciSeconds(){
        long current = System.currentTimeMillis();
        if(pauseTime == 0)
            return ((current - startTime) / (float)100);
        else
            return ((current - startTime) / (float)100) + pauseTime;
    }
    public void pauseClock(boolean isReset){
        isPaused = true;
        if(isReset)
            pauseTime = 0;
        else
            pauseTime = elapsedTimeDeciSeconds();
    }
    @Override
    public void run() {
        isPaused = false;
        startTime = System.currentTimeMillis();
        while (!isPaused){
            setText(((int)elapsedTimeDeciSeconds() / 10) + " : " + ((int)elapsedTimeDeciSeconds() % 10) + " seconds");
        }
    }
}
