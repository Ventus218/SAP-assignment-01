package sap.ass01.solution.frontend.user;

import java.awt.*;
import javax.swing.*;
import sap.ass01.solution.frontend.model.HTTPAPIs;

public class LoginView extends JFrame {
    private final LoginViewModel viewModel;
    private JTextField usernameField;
    private JButton loginButton;
    private JButton signUpButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;

    public LoginView(HTTPAPIs api) {
        viewModel = new LoginViewModel(api);
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(250, 200);
        setResizable(false);
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
                updateViewModel();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateViewModel();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateViewModel();
            }
        });

        loginButton.addActionListener(e -> {
            showLoadingIndicator();
            viewModel.login(res -> res.handle(user -> {
                hideLoadingIndicator();
                new RideView();
                dispose();
            }, err -> {
                hideLoadingIndicator();
            }));
        });

        signUpButton.addActionListener(e -> {
            showLoadingIndicator();
            viewModel.signup(res -> res.handle(user -> {
                hideLoadingIndicator();
                new RideView();
                dispose();
            }, err -> {
                hideLoadingIndicator();
            }));
        });

        add(new JLabel("Username:"));
        add(usernameField);
        add(loginButton);
        add(signUpButton);
        add(errorLabel);
        add(loadingLabel);

        setVisible(true);
    }

    private void updateViewModel() {
        String username = usernameField.getText().trim();
        viewModel.setUsername(username);

        boolean usernameIsEmpty = username.isEmpty();
        loginButton.setEnabled(!usernameIsEmpty);
        signUpButton.setEnabled(!usernameIsEmpty);
        errorLabel.setText(""); // Clear error when text is entered
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
