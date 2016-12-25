package model.abstracts;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/*
Class which assumes that borders can give its shape and then takes advantage of 
the fact that the retention of testing can be done by hitting a shape jaèim coup.	
In an ideal case, you should be able pull the same format that is used for
 testing protective. 
*/
public abstract class ShapeEdge extends AbstractEdge {

	/**
	 * Return path that could be important for drawing these borders. Path does
	 * not include types of arrows or labels.
	 * 
	 * @return return path along the edge of the
	 */
	public abstract Shape getShape();

	public Rectangle2D getBounds(Graphics2D g2) {
		return getShape().getBounds();
	}

	public boolean contains(Point2D aPoint) {
		final double MAX_DIST = 3;

		Line2D conn = getConnectionPoints();
		if (aPoint.distance(conn.getP1()) <= MAX_DIST || aPoint.distance(conn.getP2()) <= MAX_DIST)
			return false;

		Shape p = getShape();
		BasicStroke fatStroke = new BasicStroke((float) (2 * MAX_DIST));
		Shape fatPath = fatStroke.createStrokedShape(p);
		return fatPath.contains(aPoint);
	}
}
