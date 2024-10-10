package sap.ass01.solution.frontend.admin;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import sap.ass01.solution.frontend.model.*;

public class AdminControlPanelView extends JFrame {

	private VisualiserPanel centralPanel;
	private JButton addEBikeButton;
	// TODO: remove model from view
	private ConcurrentHashMap<String, EBike> bikes;
	private HashMap<String, User> users;
	private HashMap<String, Ride> rides;
	private AdminControlPanelView thisView = this;

	private int rideId;

	public AdminControlPanelView() {
		setupView();
		setupModel();
		setVisible(true);
	}

	protected void setupModel() {
		bikes = new ConcurrentHashMap<String, EBike>();
		users = new HashMap<String, User>();
		rides = new HashMap<String, Ride>();

		rideId = 0;
		var u1 = new User("u1", 100);
		this.users.put(u1.id(), u1);
		var b1 = new EBike(getName(), EBikeState.AVAILABLE, new P2d(0, 0), new V2d(1, 0), 0, 100);
		this.bikes.put("b1", b1);
	}

	protected void setupView() {
		setTitle("Admin control panel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setResizable(false);

		setLayout(new BorderLayout());

		addEBikeButton = new JButton("Add EBike");
		addEBikeButton.addActionListener(e -> {
			JDialog d = new AddEBikeDialog(thisView, ebike -> {
				bikes.put(ebike.id(), ebike);
				log("added new EBike " + ebike);
				centralPanel.refresh();
			});
			d.setVisible(true);
		});

		JPanel topPanel = new JPanel();
		topPanel.add(addEBikeButton);
		add(topPanel, BorderLayout.NORTH);

		centralPanel = new VisualiserPanel(800, 500, this);
		add(centralPanel, BorderLayout.CENTER);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}

	public EBike getEBike(String id) {
		return bikes.get(id);
	}

	public Enumeration<EBike> getEBikes() {
		return bikes.elements();
	}

	public Collection<User> getUsers() {
		return users.values();
	}

	public void refreshView() {
		centralPanel.refresh();
	}

	private void log(String msg) {
		System.out.println("[EBikeApp] " + msg);
	}

	public static class VisualiserPanel extends JPanel {
		private long dx;
		private long dy;
		private AdminControlPanelView app;

		public VisualiserPanel(int w, int h, AdminControlPanelView app) {
			setSize(w, h);
			dx = w / 2 - 20;
			dy = h / 2 - 20;
			this.app = app;
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2.clearRect(0, 0, this.getWidth(), this.getHeight());

			var it = app.getEBikes().asIterator();
			while (it.hasNext()) {
				var b = it.next();
				var p = b.loc();
				int x0 = (int) (dx + p.x());
				int y0 = (int) (dy - p.y());
				g2.drawOval(x0, y0, 20, 20);
				g2.drawString(b.id(), x0, y0 + 35);
				g2.drawString("(" + (int) p.x() + "," + (int) p.y() + ")", x0, y0 + 50);
			}

			var it2 = app.getUsers().iterator();
			var y = 20;
			while (it2.hasNext()) {
				var u = it2.next();
				g2.drawRect(10, y, 20, 20);
				g2.drawString(u.id() + " - credit: " + u.credit(), 35, y + 15);
				y += 25;
			}
			;

		}

		public void refresh() {
			repaint();
		}
	}

	public static void main(String[] args) {
		var w = new AdminControlPanelView();
		w.display();
	}

}
