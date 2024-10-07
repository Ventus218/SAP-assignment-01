package sap.ass01.bbom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RideSimulationControlPanel extends JFrame {

    private JButton stopButton;
    // private EBikeApp app;
    // private Ride ride;

    public RideSimulationControlPanel(Ride ride, EBikeApp app) {
        super("Ongoing Ride: " + ride.getId());
        setSize(400, 200);

        // this.app = app;
        // this.ride = ride;

        stopButton = new JButton("Stop Riding");

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Rider name: " + ride.getUser().getId()));
        inputPanel.add(new JLabel("Riding e-bike: " + ride.getEBike().getId()));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(stopButton);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                app.endRide(ride.getId());
                dispose();
            }
        });
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

}
