package sap.ass01.solution.frontend.admin;

import javax.swing.JButton;

public interface RequestPlugin {

    public String pluginId();

    public void init(AdminControlPanelView owner, AdminControlPanelViewModel viewModel);

    public JButton getButton();
}
