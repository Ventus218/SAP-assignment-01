package sap.ass01.solution.frontend.user;

import javax.swing.*;

public class EBikeUserApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView());
    }
}
