package at.tomtasche.mapsracer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Collection;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.painter.Painter;

import at.tomtasche.mapsracer.data.Cluster;
import at.tomtasche.mapsracer.map.BoundingBox;

public class ClusterPainter implements Painter<JXMapViewer> {
	private boolean antiAlias = true;

	private Collection<Cluster> clusters;

	public void initialize(Collection<Cluster> clusters) {
		this.clusters = clusters;
	}

	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		if (clusters == null) {
			return;
		}

		g = (Graphics2D) g.create();

		// convert from viewport to world bitmap
		Rectangle rect = map.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		if (antiAlias)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

		// do the drawing
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke(4));

		for (Cluster cluster : clusters) {
			if (cluster == null) {
				continue;
			}

			drawCluster(g, map, cluster);
		}

		g.dispose();
	}

	private void drawCluster(Graphics2D g, JXMapViewer map, Cluster cluster) {
		BoundingBox boundingBox = cluster.getBoundingBox();

		GeoPosition topLeftPosition = boundingBox.getTopLeft();
		GeoPosition topRightPosition = boundingBox.getTopRight();
		GeoPosition bottomRightPosition = boundingBox.getBottomRight();
		GeoPosition bottomLeftPosition = boundingBox.getBottomLeft();

		Point2D topLeftPoint = positionToPoint(map, topLeftPosition);
		Point2D topRightPoint = positionToPoint(map, topRightPosition);
		Point2D bottomRightPoint = positionToPoint(map, bottomRightPosition);
		Point2D bottomLeftPoint = positionToPoint(map, bottomLeftPosition);

		double width = topLeftPoint.distance(topRightPoint);
		double height = topLeftPoint.distance(bottomLeftPoint);

		g.drawRect((int) topLeftPoint.getX(), (int) topLeftPoint.getY(),
				(int) width, (int) height);
	}

	private Point2D positionToPoint(JXMapViewer map, GeoPosition position) {
		return map.getTileFactory().geoToPixel(position, map.getZoom());
	}
}