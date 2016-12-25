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
 * UML中的接口图元
 */
public class InterfaceNode extends RectangularNode {

	/**
	 * 初始化，画出接口 <<interface>>.
	 */
	public InterfaceNode() {
		name = new TextArea();
		name.setSize(TextArea.LARGE);
		name.setText("\u00ABinterface\u00BB");
		methods = new TextArea();
		methods.setJustification(TextArea.LEFT);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		midHeight = DEFAULT_COMPARTMENT_HEIGHT;
		botHeight = DEFAULT_COMPARTMENT_HEIGHT;
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(),
				getBounds().getHeight() - botHeight);
		g2.draw(top);
		name.draw(g2, top);

		Rectangle2D bot = new Rectangle2D.Double(top.getX(), top.getMaxY(), top.getWidth(), botHeight);
		g2.draw(bot);
		methods.draw(g2, bot);
	}

	public void layout(Diagram g, Graphics2D g2, Grid grid) {
		Rectangle2D min = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);

		Rectangle2D top = name.getBounds(g2);
		if (top.getHeight() == 0)
			top.add(min);

		Rectangle2D bot = methods.getBounds(g2);
		if (bot.getHeight() == 0)
			bot.add(min);
		botHeight = bot.getHeight();

		Rectangle2D b = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(),
				Math.max(top.getWidth(), bot.getWidth()), top.getHeight() + botHeight);
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

	public void setMethods(TextArea newValue) {
		methods = newValue;
	}

	public TextArea getMethods() {
		return methods;
	}

	public Object clone() {
		InterfaceNode cloned = (InterfaceNode) super.clone();
		cloned.name = (TextArea) name.clone();
		cloned.methods = (TextArea) methods.clone();
		return cloned;
	}

	public String toString() {
		return "Interface Node";
	}

	private double midHeight;
	private double botHeight;
	private TextArea name;
	private TextArea methods;

	private static int DEFAULT_COMPARTMENT_HEIGHT = 20;// 设置默认组件的高度
	private static int DEFAULT_WIDTH = 100;// 设置默认的宽度
	private static int DEFAULT_HEIGHT = 40;// 设置默认的高度
}
