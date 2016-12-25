package model.abstracts;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * 图像中的边
 * 
 * 在AbstractEdge中调用
 * 
 * 接口
 */
public interface Edge extends Serializable, Cloneable {
	/**
	 * 绘制边框
	 */
	void draw(Graphics2D g2);

	/**
	 * 是否包含指定点
	 * 
	 * @param aPoint
	 * 
	 * @return
	 */
	boolean contains(Point2D aPoint);

	/**
	 * 链接两个边缘节点
	 * 
	 * @param aStart
	 * @param anEnd
	 */
	void connect(Node aStart, Node anEnd);

	/**
	 * @return
	 */
	Node getStart();

	/**
	 * @return
	 */
	Node getEnd();

	/**
	 * 获得定义为此边缘上的节点
	 * 
	 * @return
	 */
	Line2D getConnectionPoints();

	/**
	 * 获取Edge周围的边
	 * 
	 * @return 一个定义好的举行
	 */
	Rectangle2D getBounds(Graphics2D g2);

	Object clone();

	public String getName();

	public void setName(String name);
}
