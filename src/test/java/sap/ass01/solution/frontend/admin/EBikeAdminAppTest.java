package sap.ass01.solution.frontend.admin;

import javax.swing.*;

import sap.ass01.solution.frontend.model.HTTPAPIs;
import sap.ass01.solution.frontend.model.HTTPAPIsMock;

public class EBikeAdminAppTest {

    public static void main(String[] args) {
        HTTPAPIs apis = new HTTPAPIsMock(200);
        SwingUtilities.invokeLater(() -> new AdminControlPanelView(new AdminControlPanelViewModel(apis), 1000));
    }
}
