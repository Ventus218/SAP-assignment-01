package sap.ass01.solution.frontend.admin;

import javax.swing.JButton;
import sap.ass01.solution.frontend.model.HTTPAPIs;

public interface RequestPlugin {

    public String pluginId();

    public JButton makeButton(HTTPAPIs api, AdminControlPanelViewModel viewModel);
}
