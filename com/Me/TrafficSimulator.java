package com.Me;
/*
 * Date: 03 August 2021
 * Filename: TrafficSimulator.java
 * Description: The main driving program of this traffic simulator application. The class extends JFrame, and its
 * constructor is used to launch the main application. The constructor method sets values for its frame, prompts the
 * user for input using a JOptionPane triggered by the getOptions() method, creates a Runnable JTextField from the
 * GUIClock class, a LayeredPane with two Runnable JPanels from the TrafficLights and CarsTraversing classes using the
 * initializeTrafficPane() method, and creates a button Panel with buttons created using the createButton() method.
 * After user inputs are in the system, the application initializes with the parameters, then allows the user to start,
 * pause, reset, and exit the simulation with the buttons provided. Additional methods are provided to naturally stop
 * all of the running (non-main) threads, as well as another to start them.
 */
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class TrafficSimulator extends JFrame {
    private final GUIClock guiClockRunnable;
    private TrafficLights trafficLightsRunnable;
    private CarsTraversing carsTraversingRunnable;
    private final GridBagConstraints gridBagConstraints;
    private final JLayeredPane layeredTrafficPane;
    private final JButton playButton, pauseButton;
    private int numberOfLights, numberOfCars;
    private boolean randomLightSpacing = false;

    private JButton createButton(String buttonType){
        JButton returnButton = new JButton(buttonType);
        switch (buttonType) {
            case "Pause" -> returnButton.addActionListener(e -> {
                stopThreads(false);
                swapEnabled(true);
            });
            case "Play" -> returnButton.addActionListener(e -> {
                startThreads();
                swapEnabled(false);
            });
            case "Reset" -> returnButton.addActionListener(e -> {
                stopThreads(true);
                layeredTrafficPane.removeAll();
                revalidate();
                repaint();
                // Prompt New Input, Initialize Panel, Start Threads
                getOptions();
                initializeTrafficPane();
                swapEnabled(true);
            });
            case "Exit" -> returnButton.addActionListener(e -> {
                stopThreads(false);
                System.exit(1);
            });
            default -> throw new RuntimeException();
        }
        return returnButton;
    }
    private void stopThreads(boolean isReset){
        guiClockRunnable.pauseClock(isReset);
        trafficLightsRunnable.pauseLights();
        carsTraversingRunnable.pauseCars();
    }
    private void startThreads(){
        Thread guiClockThread = new Thread(guiClockRunnable);
        guiClockThread.start();
        Thread trafficLightsThread = new Thread(trafficLightsRunnable);
        trafficLightsThread.start();
        Thread carsTraversingThread = new Thread(carsTraversingRunnable);
        carsTraversingThread.start();
    }
    private void swapEnabled(boolean setPlayEnabled){
        try {
            if(setPlayEnabled){
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
            } else{
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
            }
        } catch (NullPointerException ignored) {}
    }
    private void getOptions(){
        JTextField lightsField = new JTextField();
        JTextField carsField = new JTextField();
        JRadioButton randomSpacing = new JRadioButton("True");
        JRadioButton notRandomSpacing = new JRadioButton("False");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(randomSpacing);
        buttonGroup.add(notRandomSpacing);
        notRandomSpacing.setSelected(true); // Default Spacing = 1000m
        Object[] inputMessage = {
                "Number of Lights:", lightsField,
                "Number of Cars", carsField,
                "Random Light Spacing?", randomSpacing, notRandomSpacing,
                "Note: Default Light Spacing = 1000m\nMaximum # of Car Lanes = 5\n(Soft) Min Lights = 3, (Hard) Max Lights = 5\n(Soft) Min Cars = 3, (Hard) Max Cars = 10"
        };
        while (true){
            int option = JOptionPane.showConfirmDialog(this, inputMessage, "Traffic Options Input", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION){
                try{
                    numberOfLights = Integer.parseInt(lightsField.getText());
                    numberOfCars = Integer.parseInt(carsField.getText());
                    if(numberOfCars < 1 || numberOfLights < 1) // Soft min = 3 (for assignment), Hard min = 1
                        throw new IllegalArgumentException();
                    if(numberOfLights > 5 || numberOfCars > 10) // Max Lights = 5 (spacing assumptions), Max cars = 10 (2 in each lane)
                        throw new IllegalArgumentException();
                    randomLightSpacing = randomSpacing.isSelected();
                    break;
                } catch (IllegalArgumentException e){
                    JOptionPane.showMessageDialog(this, "ERROR! Invalid Input Received", "ERROR IN INPUT", JOptionPane.ERROR_MESSAGE);
                }
            } else
                System.exit(1);
        }
    }
    private void initializeTrafficPane(){
        gridBagConstraints.gridy = 1;
        // Add Traffic Lights to LayeredPane
        trafficLightsRunnable = new TrafficLights(numberOfLights, randomLightSpacing);
        layeredTrafficPane.add(trafficLightsRunnable, JLayeredPane.DEFAULT_LAYER);
        // Add Cars Traversing to LayeredPane
        carsTraversingRunnable = new CarsTraversing(trafficLightsRunnable, numberOfCars);
        layeredTrafficPane.add(carsTraversingRunnable, 1);
        layeredTrafficPane.moveToFront(carsTraversingRunnable);
        // Add Message Regarding Speeds to LayeredPane
        JTextField speedMessage = new JTextField("All Speeds Exaggerated 10-fold for Simulation");
        speedMessage.setFont(new Font("Dialog", Font.BOLD, 12));
        speedMessage.setEnabled(false);
        speedMessage.setBounds(330, 3, 340, 20);
        layeredTrafficPane.add(speedMessage, JLayeredPane.TOP_ALIGNMENT);
        layeredTrafficPane.moveToFront(speedMessage);
        add(layeredTrafficPane, gridBagConstraints);
    }

    private TrafficSimulator(){
        super("Traffic Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 500));
        setLayout(new GridBagLayout());
        Border border = BorderFactory.createLineBorder(Color.black);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        getOptions(); // User Input for Simulation

        // Create Clock Runnable & Add
        guiClockRunnable = new GUIClock(border);
        add(guiClockRunnable, gridBagConstraints);

        // Create Simulation Panel & Initialize
        layeredTrafficPane = new JLayeredPane();
        layeredTrafficPane.setPreferredSize(new Dimension(1000, 400));
        layeredTrafficPane.setMinimumSize(new Dimension(1000, 400));
        layeredTrafficPane.setBorder(border);
        initializeTrafficPane();

        // Create Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(border);
        gridBagConstraints.gridy = 2;
        // Create Play, Pause, Reset, Exit Buttons
        playButton = createButton("Play");
        pauseButton = createButton("Pause");
        pauseButton.setEnabled(false); // Start As Paused
        JButton resetButton = createButton("Reset");
        JButton exitButton = createButton("Exit");
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, gridBagConstraints);
    }

    public static void main(String[] args) {
        TrafficSimulator gui = new TrafficSimulator();
        gui.setVisible(true);
    }
}
