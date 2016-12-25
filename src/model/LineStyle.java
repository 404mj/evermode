package model;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * 定义线条样式，可以使不同的形状
 * 
 * @author zsx
 *
 */
public class LineStyle {
	private LineStyle() {
	}

	/**
	 * 得到线条样式
	 */
	public Stroke getStroke() {
		if (this == DOTTED)
			return DOTTED_STROKE;
		return SOLID_STROKE;
	}

	private static Stroke SOLID_STROKE = new BasicStroke();
	private static Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
			new float[] { 3.0f, 3.0f }, 0.0f);

	public static final LineStyle SOLID = new LineStyle();
	public static final LineStyle DOTTED = new LineStyle();
}
