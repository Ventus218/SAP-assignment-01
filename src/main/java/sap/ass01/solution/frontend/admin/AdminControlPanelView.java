package sap.ass01.solution.frontend.admin;

import java.awt.*;
import javax.swing.*;

public class AdminControlPanelView extends JFrame {

	private final int POLL_TICK_MILLIS = 1000;
	private VisualiserPanel centralPanel;
	private JButton addEBikeButton;
	private JLabel loadingLabel;
	private int loadingRequests = 0;
	private final AdminControlPanelViewModel viewModel;
	private final Timer pollTimer;

	public AdminControlPanelView(AdminControlPanelViewModel viewModel) {
		this.viewModel = viewModel;
		setupView();
		setVisible(true);

		fetchAllData();
		pollTimer = new Timer(POLL_TICK_MILLIS, e -> fetchAllData());
		pollTimer.setRepeats(true);
		pollTimer.start();
	}

	protected void setupView() {
		setTitle("Admin control panel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setResizable(false);

		setLayout(new BorderLayout());

		loadingLabel = new JLabel("Loading...");
		loadingLabel.setVisible(false);

		addEBikeButton = new JButton("Add EBike");
		addEBikeButton.addActionListener(e -> {
			new AddEBikeDialog(this, ebike -> {
				startLoading();
				viewModel.createEBike(ebike, res -> {
					res.handle(nothing -> {
						SwingUtilities.invokeLater(this::refreshView);
						fetchBikes();
					}, this::showError);
					stopLoading();
				});
			}).setVisible(true);
		});

		JPanel topPanel = new JPanel();
		topPanel.add(addEBikeButton);
		topPanel.add(loadingLabel);
		add(topPanel, BorderLayout.NORTH);

		centralPanel = new VisualiserPanel(800, 500, viewModel);
		add(centralPanel, BorderLayout.CENTER);
	}

	private void fetchAllData() {
		fetchBikes();
		fetchUsers();
		fetchRides();
	}

	private void fetchBikes() {
		viewModel.fetchBikes(res -> {
			res.handle(bikes -> SwingUtilities.invokeLater(this::refreshView), this::showError);
		});
	}

	private void fetchUsers() {
		viewModel.fetchUsers(res -> {
			res.handle(users -> SwingUtilities.invokeLater(this::refreshView), this::showError);
		});
	}

	private void fetchRides() {
		viewModel.fetchRides(res -> {
			res.handle(rides -> SwingUtilities.invokeLater(this::refreshView), this::showError);
		});
	}

	private void startLoading() {
		loadingRequests++;
		refreshView();
	}

	private void stopLoading() {
		loadingRequests--;
		refreshView();
	}

	private void showError(Exception error) {
		JOptionPane.showConfirmDialog(this, error.getMessage());
	}

	public void refreshView() {
		loadingLabel.setVisible(loadingRequests != 0);
		addEBikeButton.setEnabled(loadingRequests == 0);

		centralPanel.refresh();
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

			model.getBikes().forEach(b -> {
				var p = b.loc();
				int x0 = (int) (dx + p.x());
				int y0 = (int) (dy - p.y());
				g2.drawOval(x0, y0, 20, 20);
				g2.drawString(b.id().id(), x0, y0 + 35);
				g2.drawString("(" + (int) p.x() + "," + (int) p.y() + ")", x0, y0 + 50);
			});

			var userIterator = model.getUsers().iterator();
			var y = 20;
			while (userIterator.hasNext()) {
				var u = userIterator.next();
				g2.drawRect(10, y, 20, 20);
				g2.drawString(u.id() + " - credit: " + u.credit(), 35, y + 15);
				y += 25;
			}
		}

		public void refresh() {
			repaint();
		}
	}
}
