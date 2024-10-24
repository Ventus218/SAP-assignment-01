package sap.ass01.solution.pluginsdevelopment;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import sap.ass01.solution.frontend.admin.AdminControlPanelView;
import sap.ass01.solution.frontend.model.*;

/**
 * 
 * Courteously implemented by ChatGPT
 * 
 * prompt:
 * 
 * "Hello ChatGPT. Could you write me a Java class
 * implementing a JDialog with title "Adding E-Bike",
 * including "OK" and "Cancel" buttons, and some input fields,
 * namely: an id input field (with label "E-Bike ID"),
 * an x input field (with label "E-Bike location - X coord:")
 * and an y input field (with label "E-Bike location - Y coord:").
 * Thanks a lot!"
 * 
 */
public class DeleteEBikeDialog extends JDialog {

    private JTextField idField;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;
    private final Consumer<EBikeId> deleteEBike;

    public DeleteEBikeDialog(AdminControlPanelView owner, Consumer<EBikeId> createEBike) {
        super(owner, "Adding E-Bike", true);
        this.deleteEBike = createEBike;
        initializeComponents();
        setupLayout();
        addEventHandlers();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        idField = new JTextField(15);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        loadingLabel = new JLabel("Loading...");
        loadingLabel.setVisible(false); // Initially hidden
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("E-Bike ID:"));
        inputPanel.add(idField);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);
        bottomPanel.add(errorLabel);
        loadingLabel.setAlignmentX(CENTER_ALIGNMENT);
        bottomPanel.add(loadingLabel);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addEventHandlers() {
        okButton.addActionListener(e -> {
            String id = idField.getText();
            deleteEBike.accept(new EBikeId(id));
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
