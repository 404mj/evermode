package model.diagram;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import model.Diagram;
import model.PointNode;
import model.abstracts.Node;
import model.abstracts.RectangularNode;
import view.Grid;
import view.property.TextArea;

/**
 * UML 中的类图元
 */
public class ClassNode extends RectangularNode {

	/**
	 * 
	 */
	public ClassNode() {
		name = new TextArea();
		name.setSize(TextArea.LARGE);
		attributes = new TextArea();
		attributes.setJustification(TextArea.LEFT);
		methods = new TextArea();
		methods.setJustification(TextArea.LEFT);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		midHeight = DEFAULT_COMPARTMENT_HEIGHT;
		botHeight = DEFAULT_COMPARTMENT_HEIGHT;
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(),
				getBounds().getHeight() - midHeight - botHeight);
		g2.draw(top);
		name.draw(g2, top);
		Rectangle2D mid = new Rectangle2D.Double(top.getX(), top.getMaxY(), top.getWidth(), midHeight);
		g2.draw(mid);
		attributes.draw(g2, mid);
		Rectangle2D bot = new Rectangle2D.Double(top.getX(), mid.getMaxY(), top.getWidth(), botHeight);
		g2.draw(bot);
		methods.draw(g2, bot);
	}

	public void layout(Diagram g, Graphics2D g2, Grid grid) {
		Rectangle2D min = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);
		Rectangle2D top = name.getBounds(g2);
		top.add(min);
		Rectangle2D mid = attributes.getBounds(g2);
		Rectangle2D bot = methods.getBounds(g2);

		midHeight = mid.getHeight();
		botHeight = bot.getHeight();

		mid.add(min);
		bot.add(min);
		midHeight = mid.getHeight();
		botHeight = bot.getHeight();

		Rectangle2D b = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(),
				Math.max(top.getWidth(), Math.max(mid.getWidth(), bot.getWidth())),
				top.getHeight() + midHeight + botHeight);
		grid.snap(b);
		setBounds(b);
	}

	public boolean addNode(Node n, Point2D p) {
		return n instanceof PointNode;
	}

	public void setName(TextArea newValue) {
		name = newValue;
	}

	public TextArea getName() {
		return name;
	}

	public void setAttributes(TextArea newValue) {
		attributes = newValue;
	}

	public TextArea getAttributes() {
		return attributes;
	}

	public void setMethods(TextArea newValue) {
		methods = newValue;
	}

	public TextArea getMethods() {
		return methods;
	}

	public Object clone() {
		ClassNode cloned = (ClassNode) super.clone();
		cloned.name = (TextArea) name.clone();
		cloned.methods = (TextArea) methods.clone();
		cloned.attributes = (TextArea) attributes.clone();
		return cloned;
	}

	public String toString() {
		return "Class Node";
	}

	private double midHeight;
	private double botHeight;
	private TextArea name;
	private TextArea attributes;
	private TextArea methods;

	private static int DEFAULT_COMPARTMENT_HEIGHT = 20;
	private static int DEFAULT_WIDTH = 100;
	private static int DEFAULT_HEIGHT = 60;
}
