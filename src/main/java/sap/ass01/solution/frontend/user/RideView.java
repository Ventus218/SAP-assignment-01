package sap.ass01.solution.frontend.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RideView {
    private JFrame rideFrame;
    private JComboBox<String> bikeSelection;
    private JButton startRideButton;
    private JButton stopRideButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;

    private boolean isRideActive = false;
    private ArrayList<String> bikes;

    public RideView() {
        bikes = new ArrayList<>();
        bikes.add("EBike 1");
        bikes.add("EBike 2");
        bikes.add("EBike 3");

        rideFrame = new JFrame("EBike Selection");
        rideFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rideFrame.setSize(300, 250);
        rideFrame.setLayout(new FlowLayout());

        bikeSelection = new JComboBox<>(bikes.toArray(new String[0]));
        startRideButton = new JButton("Start Ride");
        stopRideButton = new JButton("Stop Ride");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        startRideButton.setEnabled(false);
        stopRideButton.setEnabled(false);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false); // Initially hidden

        bikeSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isRideActive) {
                    startRideButton.setEnabled(bikeSelection.getSelectedItem() != null);
                }
            }
        });

        startRideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoadingIndicator();
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws InterruptedException {
                        Thread.sleep(2000); // Simulate loading
                        return null;
                    }

                    @Override
                    protected void done() {
                        hideLoadingIndicator();
                        if (!isRideActive) {
                            isRideActive = true;
                            errorLabel.setText("");
                            startRideButton.setEnabled(false);
                            stopRideButton.setEnabled(true);
                            bikeSelection.setEnabled(false); // Disable bike selection during the ride
                        } else {
                            errorLabel.setText("Ride already started!");
                        }
                    }
                };
                worker.execute();
            }
        });

        stopRideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoadingIndicator();
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws InterruptedException {
                        Thread.sleep(2000); // Simulate loading
                        return null;
                    }

                    @Override
                    protected void done() {
                        hideLoadingIndicator();
                        if (isRideActive) {
                            isRideActive = false;
                            errorLabel.setText("");
                            startRideButton.setEnabled(true);
                            stopRideButton.setEnabled(false);
                            bikeSelection.setEnabled(true); // Re-enable bike selection after stopping the ride
                        } else {
                            errorLabel.setText("No ride to stop!");
                        }
                    }
                };
                worker.execute();
            }
        });

        // Add components to the frame
        rideFrame.add(new JLabel("Select EBike:"));
        rideFrame.add(bikeSelection);
        rideFrame.add(startRideButton);
        rideFrame.add(stopRideButton);
        rideFrame.add(errorLabel);
        rideFrame.add(loadingLabel);

        rideFrame.setVisible(true);
    }

    // Show loading indicator and disable user interaction
    private void showLoadingIndicator() {
        loadingLabel.setVisible(true);
        bikeSelection.setEnabled(false);
        startRideButton.setEnabled(false);
        stopRideButton.setEnabled(false);
    }

    // Hide loading indicator and enable user interaction
    private void hideLoadingIndicator() {
        loadingLabel.setVisible(false);
        if (!isRideActive) {
            bikeSelection.setEnabled(true);
            startRideButton.setEnabled(bikeSelection.getSelectedItem() != null);
        } else {
            stopRideButton.setEnabled(true);
        }
    }
}
