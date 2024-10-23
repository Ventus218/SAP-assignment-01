package sap.ass01.solution.frontend.admin.plugins;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import sap.ass01.solution.frontend.admin.AdminControlPanelView;
import sap.ass01.solution.frontend.model.*;
import sap.ass01.solution.frontend.model.dto.CreateEBikeDTO;

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
public class AddEBikeDialog extends JDialog {

    private JTextField idField;
    private JTextField xCoordField;
    private JTextField yCoordField;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;
    private final Consumer<CreateEBikeDTO> createEBike;

    public AddEBikeDialog(AdminControlPanelView owner, Consumer<CreateEBikeDTO> createEBike) {
        super(owner, "Adding E-Bike", true);
        this.createEBike = createEBike;
        initializeComponents();
        setupLayout();
        addEventHandlers();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents() {
        idField = new JTextField(15);
        xCoordField = new JTextField(15);
        yCoordField = new JTextField(15);
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
        inputPanel.add(new JLabel("E-Bike location - X coord:"));
        inputPanel.add(xCoordField);
        inputPanel.add(new JLabel("E-Bike location - Y coord:"));
        inputPanel.add(yCoordField);

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
            String xCoord = xCoordField.getText();
            String yCoord = yCoordField.getText();
            P2d location = new P2d(Integer.parseInt(xCoord), Integer.parseInt(yCoord));
            V2d direction = new V2d(1, 0);
            double speed = 0;
            int batteryLevel = 100;
            createEBike
                    .accept(new CreateEBikeDTO(new EBikeId(id), location, direction, speed, batteryLevel));
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
