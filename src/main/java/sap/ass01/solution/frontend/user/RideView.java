package sap.ass01.solution.frontend.user;

import java.awt.*;
import javax.swing.*;
import java.util.Optional;
import sap.ass01.solution.frontend.model.*;

public class RideView extends JFrame {
    private final RideViewModel viewModel;
    private JComboBox<String> bikeSelection;
    private JButton startRideButton;
    private JButton stopRideButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;

    private boolean isRideActive = false;

    public RideView(HTTPAPIs api, UserId userId) {
        viewModel = new RideViewModel(api, userId);

        setTitle("EBikeApp - Ride");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(280, 250);
        setResizable(false);
        setLayout(new FlowLayout());

        bikeSelection = new JComboBox<String>();
        bikeSelection.setSelectedIndex(-1);
        startRideButton = new JButton("Start Ride");
        stopRideButton = new JButton("Stop Ride");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        startRideButton.setEnabled(false);
        stopRideButton.setEnabled(false);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false); // Initially hidden

        bikeSelection.addActionListener(e -> {
            viewModel.setSelectBikeIndex(Optional.of(bikeSelection.getSelectedIndex()));
            updateView();
        });

        startRideButton.addActionListener(e -> {
            showLoadingIndicator();
            viewModel.startRide(res -> {
                SwingUtilities.invokeLater(() -> res.handle(r -> updateView(), this::showError));
                hideLoadingIndicator();
            });
        });

        stopRideButton.addActionListener(e -> {
            showLoadingIndicator();
            viewModel.stopRide(res -> {
                SwingUtilities.invokeLater(() -> res.handle(r -> updateView(), this::showError));
                hideLoadingIndicator();
            });
        });

        // Add components to the frame
        add(new JLabel("Select EBike:"));
        add(bikeSelection);
        add(startRideButton);
        add(stopRideButton);
        add(errorLabel);
        add(loadingLabel);

        setVisible(true);
        updateView();
        viewModel.fetchAvailableBikes(
                res -> SwingUtilities.invokeLater(() -> res.handle(bikes -> updateView(), this::showError)));
    }

    private void updateView() {
        bikeSelection.removeAllItems();
        viewModel.getAvailableBikes().forEach(b -> bikeSelection.addItem(b.id().id()));
        bikeSelection.setSelectedIndex(viewModel.getSelectBikeIndex().orElse(-1));
        bikeSelection.setEnabled(!viewModel.isRiding());
        startRideButton.setEnabled(viewModel.getSelectBikeIndex().isPresent() && !viewModel.isRiding());
        stopRideButton.setEnabled(viewModel.isRiding());
    }

    private void showError(Throwable error) {
        JOptionPane.showMessageDialog(this, error.getMessage());
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
