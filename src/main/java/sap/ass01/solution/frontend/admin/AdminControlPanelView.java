package sap.ass01.solution.frontend.admin;

import java.awt.*;
import javax.swing.*;
import java.util.stream.*;

public class AdminControlPanelView extends JFrame implements AdminControlPanelViewModelListener {

	private final int POLL_TICK_MILLIS = 1000;
	private VisualiserPanel centralPanel;
	private JPanel pluginsPanel;
	private JButton addEBikeButton;
	private JLabel loadingLabel;
	private final AdminControlPanelViewModel viewModel;
	private final Timer pollTimer;

	public AdminControlPanelView(AdminControlPanelViewModel viewModel) {
		this.viewModel = viewModel;
		viewModel.addListener(this);
		setupView();
		setVisible(true);

		fetchAllData();
		pollTimer = new Timer(POLL_TICK_MILLIS, e -> fetchAllData());
		pollTimer.setRepeats(true);
		pollTimer.start();
	}

	protected void setupView() {
		setTitle("EBikeApp - Admin control panel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setResizable(false);

		setLayout(new BorderLayout());

		loadingLabel = new JLabel("Loading...");
		loadingLabel.setVisible(false);

		addEBikeButton = new JButton("Add EBike");
		addEBikeButton.addActionListener(e -> {
			new AddEBikeDialog(this, ebike -> {
				viewModel.createEBike(ebike, res -> {
					res.handle(bike -> {
						SwingUtilities.invokeLater(this::refreshView);
						fetchBikes();
					}, this::showError);
				});
			}).setVisible(true);
		});

		pluginsPanel = new JPanel();
		pluginsPanel.add(new JLabel("Plugin1"));
		pluginsPanel.add(new JLabel("Plugin2"));

		JPanel topPanel = new JPanel();
		var topPanelLayout = new BorderLayout();
		topPanel.setLayout(topPanelLayout);
		topPanel.add(new JButton("Add plugin"), BorderLayout.WEST);
		topPanel.add(pluginsPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		centralPanel = new VisualiserPanel(800, 500, viewModel);
		add(centralPanel, BorderLayout.CENTER);
	}

	@Override
	public void viewModelChanged() {
		SwingUtilities.invokeLater(() -> refreshView());
	}

	private void fetchAllData() {
		fetchBikes();
		fetchUsers();
		fetchRides();
	}

	private void fetchBikes() {
		viewModel.fetchBikes(res -> SwingUtilities.invokeLater(() -> {
			res.handle(bikes -> nop(), this::showError);
		}));
	}

	private void fetchUsers() {
		viewModel.fetchUsers(res -> SwingUtilities.invokeLater(() -> {
			res.handle(users -> nop(), this::showError);
		}));
	}

	private void fetchRides() {
		viewModel.fetchRides(res -> SwingUtilities.invokeLater(() -> {
			res.handle(rides -> nop(), this::showError);
		}));
	}

	private void refreshView() {
		loadingLabel.setVisible(viewModel.getRequestsInExecution() != 0);
		addEBikeButton.setEnabled(viewModel.getRequestsInExecution() == 0);

		centralPanel.refresh();
	}

	private void nop() {
	}

	private void showError(Throwable error) {
		JOptionPane.showMessageDialog(this, error.getMessage());
	}

	public static class VisualiserPanel extends JPanel {
		private long dx;
		private long dy;
		private final AdminControlPanelViewModel model;

		public VisualiserPanel(int w, int h, AdminControlPanelViewModel model) {
			this.model = model;
			setSize(w, h);
			dx = w / 2 - 20;
			dy = h / 2 - 20;
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2.clearRect(0, 0, this.getWidth(), this.getHeight());

			var rides = StreamSupport.stream(model.getRides().spliterator(), false).toList();
			model.getBikes().forEach(b -> {
				var p = b.loc();
				int x0 = (int) (dx + p.x());
				int y0 = (int) (dy - p.y());
				g2.drawOval(x0, y0, 20, 20);
				var userId = rides.stream()
						.filter(r -> r.ebikeId().equals(b.id()) && r.endDate().isEmpty())
						.findFirst()
						.map(r -> r.userId().id());
				g2.drawString(b.id().id() + userId.map(id -> " - " + id).orElse(""), x0, y0 + 35);
				g2.drawString("(" + (int) p.x() + "," + (int) p.y() + ")", x0, y0 + 50);
			});

			var userIterator = model.getUsers().iterator();
			var y = 20;
			while (userIterator.hasNext()) {
				var u = userIterator.next();
				g2.drawRect(10, y, 20, 20);
				g2.drawString(u.id().id() + " - credit: " + u.credit(), 35, y + 15);
				y += 25;
			}
		}

		public void refresh() {
			repaint();
		}
	}
}
