package sap.ass01.solution.frontend.admin;

import javax.swing.*;

import sap.ass01.solution.frontend.model.HTTPAPIs;

public class EBikeAdminApp {

    public static void main(String[] args) {
        HTTPAPIs apis = null; // TODO: set implementation
        SwingUtilities.invokeLater(() -> new AdminControlPanelView(new AdminControlPanelViewModel(apis)));
    }
}
