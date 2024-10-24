package sap.ass01.solution.pluginsdevelopment;

import java.util.Optional;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import sap.ass01.solution.frontend.admin.*;

public class DeleteEBikePlugin implements RequestPlugin, AdminControlPanelViewModelListener {

    private boolean didInit = false;
    private AdminControlPanelView owner;
    private AdminControlPanelViewModel viewModel;
    private Optional<JButton> button = Optional.empty();

    @Override
    public void init(AdminControlPanelView owner, AdminControlPanelViewModel viewModel) {
        checkNotInit();
        this.didInit = true;
        this.owner = owner;
        this.viewModel = viewModel;
        this.viewModel.addListener(this);
    }

    @Override
    public JButton getButton() {
        checkInit();
        button = Optional.of(button.orElseGet(() -> {
            var b = new JButton("Delete EBike");
            b.addActionListener(e -> {
                new DeleteEBikeDialog(owner, ebikeId -> {
                    viewModel.getApi().deleteEBike(ebikeId, res -> SwingUtilities.invokeLater(() -> {
                        res.handle(ebike -> viewModel.fetchBikes((r) -> {
                        }), owner::showError);
                    }));
                }).setVisible(true);
            });
            return b;
        }));
        return button.get();
    }

    @Override
    public void viewModelChanged() {
        SwingUtilities.invokeLater(() -> {
            button.ifPresent(b -> b.setEnabled(viewModel.getRequestsInExecution() == 0));
        });
    }

    @Override
    public String pluginId() {
        return this.getClass().getSimpleName();
    }

    private void checkInit() {
        if (!didInit) {
            throw new IllegalStateException("Init method was not called on plugin " + pluginId());
        }
    }

    private void checkNotInit() {
        if (didInit) {
            throw new IllegalStateException("Init method was already called on plugin " + pluginId());
        }
    }
}
