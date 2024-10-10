package sap.ass01.solution.frontend.user;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JButton loginButton;
    private JButton signUpButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;

    public LoginView() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new FlowLayout());

        usernameField = new JTextField(15);
        loginButton = new JButton("Login");
        signUpButton = new JButton("Sign Up");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);

        loginButton.setEnabled(false);
        signUpButton.setEnabled(false);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false); // Initially hidden

        usernameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateButtonStates();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateButtonStates();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateButtonStates();
            }
        });

        loginButton.addActionListener(e -> {
            showLoadingIndicator();
            transitionToRideWindow();
        });

        signUpButton.addActionListener(e -> {
            showLoadingIndicator();
            transitionToRideWindow();
        });

        add(new JLabel("Username:"));
        add(usernameField);
        add(loginButton);
        add(signUpButton);
        add(errorLabel);
        add(loadingLabel);

        setVisible(true);
    }

    private void updateButtonStates() {
        boolean isEmpty = usernameField.getText().trim().isEmpty();
        loginButton.setEnabled(!isEmpty);
        signUpButton.setEnabled(!isEmpty);
        errorLabel.setText(""); // Clear error when text is entered
    }

    private void transitionToRideWindow() {
        // Simulate a loading delay by using SwingWorker to prevent UI freezing
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws InterruptedException {
                // Simulate loading time (e.g., 2 seconds)
                Thread.sleep(2000);
                return null;
            }

            @Override
            protected void done() {
                hideLoadingIndicator();
                new RideView(); // Transition to the second view
                dispose(); // Dispose the login window to free memory
            }
        };
        worker.execute();
    }

    // Show loading indicator and disable user interaction
    private void showLoadingIndicator() {
        loadingLabel.setVisible(true);
        usernameField.setEnabled(false);
        loginButton.setEnabled(false);
        signUpButton.setEnabled(false);
    }

    // Hide loading indicator and enable user interaction
    private void hideLoadingIndicator() {
        loadingLabel.setVisible(false);
    }
}
