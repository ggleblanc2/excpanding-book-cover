package com.ggl.testing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ExpandingBookCoverGUI implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new ExpandingBookCoverGUI());
	}
	
	private final DrawingPanel drawingPanel;
	
	private final ExpandingBookCoverModel model;
	
	public ExpandingBookCoverGUI() {
		this.model = new ExpandingBookCoverModel("Gilbert G. L.", 400, 600, 48);
		this.drawingPanel = new DrawingPanel(model);
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Expanding Book Cover");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(drawingPanel, BorderLayout.CENTER);
		
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		
		Timer timer = new Timer(500, new TimerListener(this, model));
		timer.start();
	}
	
	public void repaint() {
		drawingPanel.repaint();
	}
	
	public class DrawingPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		
		private final ExpandingBookCoverModel model;

		public DrawingPanel(ExpandingBookCoverModel model) {
			this.model = model;
			this.setBackground(Color.WHITE);
			
			int margin = model.getMargin();
			int width = model.getMaximumDrawingWidth() + margin + margin;
			int height = model.getMaximumDrawingHeight() + margin + margin;
			this.setPreferredSize(new Dimension(width, height));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			int margin = model.getMargin();
			int x = margin;
			int y = margin;
			g2d.setColor(Color.YELLOW);
			g2d.fillRect(x, y, model.getDrawingWidth(), model.getDrawingHeight());

			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(11f));
			g2d.drawRect(x, y, model.getDrawingWidth(), model.getDrawingHeight());

			g2d.setColor(Color.BLUE);
			g2d.fillPolygon(model.getCircle());

			g2d.setColor(Color.RED);
			g2d.fillPolygon(model.getPentagon());

			String text = model.getAuthorName();
			g2d.setFont(getFont().deriveFont(Font.BOLD, (float) model.getPointSize()));

			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D r = fm.getStringBounds(text, g2d);
			x = (model.getDrawingWidth() - (int) r.getWidth()) / 2 + margin;
			y = (model.getDrawingHeight() - (int) r.getHeight()) + fm.getAscent();
			g2d.setColor(Color.MAGENTA);
			g2d.drawString(text, x, y);
		}
	}
	
	public class TimerListener implements ActionListener {
		
		private int scaleIndex;
		private final int[] scales;
		
		private final ExpandingBookCoverGUI view;
		
		private final ExpandingBookCoverModel model;

		public TimerListener(ExpandingBookCoverGUI view, ExpandingBookCoverModel model) {
			this.view = view;
			this.model = model;
			this.scaleIndex = 1;
			this.scales = new int[] { 50, 60, 70, 80, 90, 100 };
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			model.setVariables(scales[scaleIndex++]);
			this.scaleIndex %= scales.length;
			view.repaint();
		}
		
	}
	
	public class ExpandingBookCoverModel {
		
		private final int maximumDrawingWidth, maximumDrawingHeight, maximumPointSize;
		private int drawingWidth, drawingHeight, margin, pointSize, radius, scale;
		
		private Point circleCenterPoint, pentagonCenterPoint;
		
		private Polygon circle, pentagon;
		
		private final String authorName;

		public ExpandingBookCoverModel(String authorName, int maximumDrawingWidth, 
				int maximumDrawingHeight, int maximumPointSize) {
			this.authorName = authorName;
			this.maximumDrawingWidth = maximumDrawingWidth;
			this.maximumDrawingHeight = maximumDrawingHeight;
			this.maximumPointSize = maximumPointSize;
			this.margin = 20;
			setVariables(50);
		}
		
		public void setVariables(int scale) {
			this.scale =  scale;
			this.drawingWidth = maximumDrawingWidth * scale / 100;
			this.drawingHeight = maximumDrawingHeight * scale / 100;
			this.pointSize = maximumPointSize * scale / 100;
			this.radius = drawingWidth * 20 / 100;
			this.circleCenterPoint = new Point(drawingWidth / 2 + margin, 
					drawingHeight / 3);
			this.pentagonCenterPoint = new Point(drawingWidth / 2 + margin, 
					drawingHeight * 2 / 3);
			this.circle = createCircle(circleCenterPoint, radius);
			this.pentagon = createPentagon(pentagonCenterPoint, radius);
		}
		
		private Polygon createCircle(Point centerPoint, int radius) {
			Polygon polygon = new Polygon();
			for (int angle = 0; angle < 360; angle += 5) {
				Point point = toCartesian(centerPoint, radius, angle);
				polygon.addPoint(point.x, point.y);
			}
			return polygon;
		}
		
		private Polygon createPentagon(Point centerPoint, int radius) {
			Polygon polygon = new Polygon();
			for (int angle = 54; angle < 360; angle += 72) {
				Point point = toCartesian(centerPoint, radius, angle);
				polygon.addPoint(point.x, point.y);
			}
			return polygon;
		}
		
		private Point toCartesian(Point centerPoint, int radius, int angle) {
			double theta = Math.toRadians(angle);
			int x = centerPoint.x + (int) Math.round(Math.cos(theta) * radius);
			int y = centerPoint.y + (int) Math.round(Math.sin(theta) * radius);
			return new Point(x, y);
		}

		public Point getCircleCenterPoint() {
			return circleCenterPoint;
		}

		public Point getPentagonCenterPoint() {
			return pentagonCenterPoint;
		}

		public Polygon getCircle() {
			return circle;
		}

		public Polygon getPentagon() {
			return pentagon;
		}

		public int getMaximumDrawingWidth() {
			return maximumDrawingWidth;
		}

		public int getMaximumDrawingHeight() {
			return maximumDrawingHeight;
		}

		public int getMaximumPointSize() {
			return maximumPointSize;
		}

		public String getAuthorName() {
			return authorName;
		}

		public int getScale() {
			return scale;
		}

		public int getDrawingWidth() {
			return drawingWidth;
		}

		public int getDrawingHeight() {
			return drawingHeight;
		}

		public int getMargin() {
			return margin;
		}

		public int getPointSize() {
			return pointSize;
		}
		
	}

}
