package model;

import java.awt.geom.Point2D;

/**
 * 描述二维上的曲线
 * 
 * 是一个向量，长度为1，角度介于0-360
 */
public class Direction {

	/**
	 * 重建这个Direction。返回长度为1
	 * 
	 * @param dx
	 * @param dy
	 */
	public Direction(double dx, double dy) {
		x = dx;
		y = dy;
		double length = Math.sqrt(x * x + y * y);
		if (length == 0)
			return;
		x = x / length;
		y = y / length;
	}

	/**
	 * 两点之间构造路线！！！
	 * 
	 * @param p
	 *            起始点
	 * @param q
	 *            结束点
	 */
	public Direction(Point2D p, Point2D q) {
		this(q.getX() - p.getX(), q.getY() - p.getY());
	}

	/**
	 * 在角的地方转换线
	 * 
	 * @param angle
	 *            以度为单位的角度
	 */
	public Direction turn(double angle) {
		double a = Math.toRadians(angle);
		return new Direction(x * Math.cos(a) - y * Math.sin(a), x * Math.sin(a) + y * Math.cos(a));
	}

	/**
	 * 得到这个方向的X分量
	 * 
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * 得到这个方向的Y分量
	 * 
	 * @return
	 */
	public double getY() {
		return y;
	}

	private double x;
	private double y;

	public static final Direction NORTH = new Direction(0, -1);
	public static final Direction SOUTH = new Direction(0, 1);
	public static final Direction EAST = new Direction(1, 0);
	public static final Direction WEST = new Direction(-1, 0);
}
