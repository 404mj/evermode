package model.abstracts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JLabel;

import model.LineArrow;
import model.LineStyle;

/**
 * 多个线段的边缘
 * 
 * 关系连线的设置
 */
public abstract class SegmentedEdge extends ShapeEdge {
	/**
	 * 构造边缘，无参数
	 */
	public SegmentedEdge() {
		lineStyle = LineStyle.SOLID;
		startArrowHead = LineArrow.NONE;
		endArrowHead = LineArrow.NONE;
		startLabel = "";
		middleLabel = "";
		endLabel = "";
	}

	/**
	 * 设置功能线条的样式
	 * 
	 * @param newValue
	 */
	public void setLineStyle(LineStyle newValue) {
		lineStyle = newValue;
	}

	public LineStyle getLineStyle() {
		return lineStyle;
	}

	/**
	 * 设置起始箭头
	 * 
	 * @param newValue
	 */
	public void setStartArrowHead(LineArrow newValue) {
		startArrowHead = newValue;
	}

	/**
	 * @return
	 */
	public LineArrow getStartArrowHead() {
		return startArrowHead;
	}

	public void setEndArrowHead(LineArrow newValue) {
		endArrowHead = newValue;
	}

	public LineArrow getEndArrowHead() {
		return endArrowHead;
	}

	/**
	 * 设置标签的起始
	 */
	public void setStartLabel(String newValue) {
		startLabel = newValue;
	}

	/**
	 */
	public String getStartLabel() {
		return startLabel;
	}

	/**
	 * 设置中标签
	 * 
	 * @param newValue
	 */
	public void setMiddleLabel(String newValue) {
		middleLabel = newValue;
	}

	public String getMiddleLabel() {
		return middleLabel;
	}

	public void setEndLabel(String newValue) {
		endLabel = newValue;
	}

	public String getEndLabel() {
		return endLabel;
	}

	public void draw(Graphics2D g2) {
		ArrayList points = getPoints();

		Stroke oldStroke = g2.getStroke();
		g2.setStroke(lineStyle.getStroke());
		g2.draw(getSegmentPath());
		g2.setStroke(oldStroke);
		startArrowHead.draw(g2, (Point2D) points.get(1), (Point2D) points.get(0));
		endArrowHead.draw(g2, (Point2D) points.get(points.size() - 2), (Point2D) points.get(points.size() - 1));

		drawString(g2, (Point2D) points.get(1), (Point2D) points.get(0), startArrowHead, startLabel, false);
		drawString(g2, (Point2D) points.get(points.size() / 2 - 1), (Point2D) points.get(points.size() / 2), null,
				middleLabel, true);
		drawString(g2, (Point2D) points.get(points.size() - 2), (Point2D) points.get(points.size() - 1), endArrowHead,
				endLabel, false);
	}

	/**
	 * 绘制字符串
	 * 
	 * @param g2
	 *            图形内容
	 * @param p
	 *            结束点
	 * @param q
	 *            第二个结束点
	 * @param s
	 *            要删除的字符串
	 * @param center
	 *            是否沿段中心
	 */
	private static void drawString(Graphics2D g2, Point2D p, Point2D q, LineArrow arrow, String s, boolean center) {
		if (s == null || s.length() == 0)
			return;
		label.setText("<html>" + s + "</html>");
		label.setFont(g2.getFont());
		Dimension d = label.getPreferredSize();
		label.setBounds(0, 0, d.width, d.height);

		Rectangle2D b = getStringBounds(g2, p, q, arrow, s, center);

		Color oldColor = g2.getColor();
		g2.setColor(g2.getBackground());
		g2.fill(b);
		g2.setColor(oldColor);

		g2.translate(b.getX(), b.getY());
		label.paint(g2);
		g2.translate(-b.getX(), -b.getY());
	}

	/**
	 * 计算画字符串上依附的点
	 * 
	 * @param g2
	 *            图像内容
	 * @param p
	 *            结束点
	 * @param q
	 *            第二个结束点
	 * @param b
	 *            要删除的string的bnounds
	 * @param center
	 *            是否在segment的中心
	 * @return 返回line string的位置
	 */
	private static Point2D getAttachmentPoint(Graphics2D g2, Point2D p, Point2D q, LineArrow arrow, Dimension d,
			boolean center) {
		final int GAP = 3;
		double xoff = GAP;
		double yoff = -GAP - d.getHeight();
		Point2D attach = q;
		if (center) {
			if (p.getX() > q.getX()) {
				return getAttachmentPoint(g2, q, p, arrow, d, center);
			}
			attach = new Point2D.Double((p.getX() + q.getX()) / 2, (p.getY() + q.getY()) / 2);
			if (p.getY() < q.getY())
				yoff = -GAP - d.getHeight();
			else if (p.getY() == q.getY())
				xoff = -d.getWidth() / 2;
			else
				yoff = GAP;
		} else {
			if (p.getX() < q.getX()) {
				xoff = -GAP - d.getWidth();
			}
			if (p.getY() > q.getY()) {
				yoff = GAP;
			}
			if (arrow != null) {
				Rectangle2D arrowBounds = arrow.getPath(p, q).getBounds2D();
				if (p.getX() < q.getX()) {
					xoff -= arrowBounds.getWidth();
				} else {
					xoff += arrowBounds.getWidth();
				}
			}
		}
		return new Point2D.Double(attach.getX() + xoff, attach.getY() + yoff);
	}

	/**
	 * 计算沿区段抽取字符串的边界
	 * 
	 * @param g2
	 * @param p
	 * @param q
	 * @param s
	 * @param center
	 */
	private static Rectangle2D getStringBounds(Graphics2D g2, Point2D p, Point2D q, LineArrow arrow, String s,
			boolean center) {
		if (g2 == null)
			return new Rectangle2D.Double();
		if (s == null || s.equals(""))
			return new Rectangle2D.Double(q.getX(), q.getY(), 0, 0);
		label.setText("<html>" + s + "</html>");
		label.setFont(g2.getFont());
		Dimension d = label.getPreferredSize();
		Point2D a = getAttachmentPoint(g2, p, q, arrow, d, center);
		return new Rectangle2D.Double(a.getX(), a.getY(), d.getWidth(), d.getHeight());
	}

	public Rectangle2D getBounds(Graphics2D g2) {
		ArrayList points = getPoints();
		Rectangle2D r = super.getBounds(g2);
		r.add(getStringBounds(g2, (Point2D) points.get(1), (Point2D) points.get(0), startArrowHead, startLabel, false));
		r.add(getStringBounds(g2, (Point2D) points.get(points.size() / 2 - 1), (Point2D) points.get(points.size() / 2),
				null, middleLabel, true));
		r.add(getStringBounds(g2, (Point2D) points.get(points.size() - 2), (Point2D) points.get(points.size() - 1),
				endArrowHead, endLabel, false));
		return r;
	}

	public Shape getShape() {
		GeneralPath path = getSegmentPath();
		ArrayList points = getPoints();
		path.append(startArrowHead.getPath((Point2D) points.get(1), (Point2D) points.get(0)), false);
		path.append(
				endArrowHead.getPath((Point2D) points.get(points.size() - 2), (Point2D) points.get(points.size() - 1)),
				false);
		return path;
	}

	private GeneralPath getSegmentPath() {
		ArrayList points = getPoints();

		GeneralPath path = new GeneralPath();
		Point2D p = (Point2D) points.get(points.size() - 1);
		path.moveTo((float) p.getX(), (float) p.getY());
		for (int i = points.size() - 2; i >= 0; i--) {
			p = (Point2D) points.get(i);
			path.lineTo((float) p.getX(), (float) p.getY());
		}
		return path;
	}

	public Line2D getConnectionPoints() {
		ArrayList points = getPoints();
		return new Line2D.Double((Point2D) points.get(0), (Point2D) points.get(points.size() - 1));
	}

	/**
	 * 获取边缘线段的点
	 */
	public abstract ArrayList getPoints();

	private LineStyle lineStyle;
	private LineArrow startArrowHead;
	private LineArrow endArrowHead;
	private String startLabel;
	private String middleLabel;
	private String endLabel;

	private static JLabel label = new JLabel();
}
