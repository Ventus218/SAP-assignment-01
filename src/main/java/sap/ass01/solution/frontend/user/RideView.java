package sap.ass01.solution.frontend.user;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RideView extends JFrame {
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

        setTitle("EBike Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 250);
        setResizable(false);
        setLayout(new FlowLayout());

        bikeSelection = new JComboBox<>(bikes.toArray(new String[0]));
        startRideButton = new JButton("Start Ride");
        stopRideButton = new JButton("Stop Ride");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        startRideButton.setEnabled(false);
        stopRideButton.setEnabled(false);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false); // Initially hidden

        bikeSelection.addActionListener(e -> {
            if (!isRideActive) {
                startRideButton.setEnabled(bikeSelection.getSelectedItem() != null);
            }
        });

        startRideButton.addActionListener(e -> {
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
        });

        stopRideButton.addActionListener(e -> {
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
        });

        // Add components to the frame
        add(new JLabel("Select EBike:"));
        add(bikeSelection);
        add(startRideButton);
        add(stopRideButton);
        add(errorLabel);
        add(loadingLabel);

        setVisible(true);
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
