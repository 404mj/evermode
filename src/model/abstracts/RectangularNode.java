package model.abstracts;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.Direction;
import model.Diagram;
import view.Grid;

/**
 * 作为矩形图元父类
 * 
 * 抽象的
 * 
 * @author zsx
 *
 */
public abstract class RectangularNode extends AbstractNode {

	public Object clone() {
		RectangularNode cloned = (RectangularNode) super.clone();
		cloned.bounds = (Rectangle2D) bounds.clone();
		return cloned;
	}

	public void translate(double dx, double dy) {
		bounds.setFrame(bounds.getX() + dx, bounds.getY() + dy, bounds.getWidth(), bounds.getHeight());
		super.translate(dx, dy);
	}

	public boolean contains(Point2D p) {
		return bounds.contains(p);
	}

	public Rectangle2D getBounds() {
		return (Rectangle2D) bounds.clone();
	}

	public void setBounds(Rectangle2D newBounds) {
		bounds = newBounds;
	}

	public void layout(Diagram g, Graphics2D g2, Grid grid) {
		grid.snap(bounds);
	}

	public Point2D getConnectionPoint(Direction d) {
		double slope = bounds.getHeight() / bounds.getWidth();
		double ex = d.getX();
		double ey = d.getY();
		double x = bounds.getCenterX();
		double y = bounds.getCenterY();

		if (ex != 0 && -slope <= ey / ex && ey / ex <= slope) {
			// 在左右边界相交
			if (ex > 0) {
				x = bounds.getMaxX();
				y += (bounds.getWidth() / 2) * ey / ex;
			} else {
				x = bounds.getX();
				y -= (bounds.getWidth() / 2) * ey / ex;
			}
		} else if (ey != 0) {
			// 在上或底相交
			if (ey > 0) {
				x += (bounds.getHeight() / 2) * ex / ey;
				y = bounds.getMaxY();
			} else {
				x -= (bounds.getHeight() / 2) * ex / ey;
				y = bounds.getY();
			}
		}
		return new Point2D.Double(x, y);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		writeRectangularShape(out, bounds);
	}

	/**
	 * 
	 * 讲该窗体的参数输出到标准输出流
	 * 
	 * @param out
	 * @param s
	 */
	private static void writeRectangularShape(ObjectOutputStream out, RectangularShape s) throws IOException {
		out.writeDouble(s.getX());
		out.writeDouble(s.getY());
		out.writeDouble(s.getWidth());
		out.writeDouble(s.getHeight());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		bounds = new Rectangle2D.Double();
		readRectangularShape(in, bounds);
	}

	/**
	 * 从标准输入流中读入窗体的图形的参数
	 * 
	 * @param in
	 * @param s
	 */
	private static void readRectangularShape(ObjectInputStream in, RectangularShape s) throws IOException {
		double x = in.readDouble();
		double y = in.readDouble();
		double width = in.readDouble();
		double height = in.readDouble();
		s.setFrame(x, y, width, height);
	}

	public Shape getShape() {
		return bounds;
	}

	private transient Rectangle2D bounds;
}
