package model.abstracts;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import model.Diagram;
import model.Direction;
import view.Grid;

/**
 * 在AbstractNode中实现
 * 
 * 节点 接口
 */
public interface Node extends Serializable, Cloneable {
	/**
	 * 绘制边框
	 * 
	 * @param parametar
	 */
	void draw(Graphics2D g2);

	/**
	 * 
	 * API: http://zetcode.com/gfx/java2d/transformations/
	 * 
	 * 讲图形转换到相对的另一个位置。
	 * 
	 * @param dx
	 * @param dy
	 */
	void translate(double dx, double dy);

	/**
	 * 是否包含指定点
	 * 
	 * @param parametar
	 */
	boolean contains(Point2D aPoint);

	/**
	 * 需要得到最好位置的连接点 以便与其他节点连接
	 * 
	 * @param d
	 */
	Point2D getConnectionPoint(Direction d);

	/**
	 * 获取矩形形状
	 * 
	 * @return
	 */
	Rectangle2D getBounds();

	/**
	 * 添加一个边框
	 * 
	 * @param p
	 * @param e
	 * @return
	 */
	boolean addEdge(Edge e, Point2D p1, Point2D p2);

	/**
	 * 为节点添加一个孩子节点
	 * 
	 * @param n
	 * @param p
	 * @return
	 */
	boolean addNode(Node n, Point2D p);

	/**
	 * 
	 * @param g
	 * @param e
	 */
	void removeEdge(Diagram g, Edge e);

	/**
	 * 
	 * @param g
	 * @param n
	 */
	void removeNode(Diagram g, Node n);

	/**
	 * 
	 * @param g
	 * @param g2
	 * @param grid
	 */
	void layout(Diagram g, Graphics2D g2, Grid grid);

	/**
	 * 
	 * @return
	 */
	Node getParent();

	/**
	 * 
	 * @param node
	 */
	void setParent(Node node);

	/**
	 * 
	 * @return
	 */
	List getChildren();

	/**
	 * 
	 * @param index
	 * @param node
	 */
	void addChild(int index, Node node);

	/**
	 * 
	 * @param node
	 */
	void removeChild(Node node);

	Object clone();
}
