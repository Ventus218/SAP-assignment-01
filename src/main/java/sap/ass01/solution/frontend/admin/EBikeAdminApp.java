package sap.ass01.solution.frontend.admin;

import javax.swing.*;

public class EBikeAdminApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminControlPanelView());
    }
}
