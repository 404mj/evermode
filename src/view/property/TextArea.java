package view.property;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;

/**
 * 字符串可以与多条线路进行拓展
 */
public class TextArea implements Cloneable, Serializable {

	public TextArea() {
		text = "";
		justification = CENTER;
		size = NORMAL;
		underlined = false;
	}

	public void setText(String newValue) {
		text = newValue;
		setLabelText();
	}

	public String getText() {
		return text;
	}

	/**
	 * 设置对准区域的值
	 * 
	 * @param newValue
	 */
	public void setJustification(int newValue) {
		justification = newValue;
		setLabelText();
	}

	public int getJustification() {
		return justification;
	}

	public boolean isUnderlined() {
		return underlined;
	}

	public void setUnderlined(boolean newValue) {
		underlined = newValue;
		setLabelText();
	}

	public void setSize(int newValue) {
		size = newValue;
		setLabelText();
	}

	public int getSize() {
		return size;
	}

	public String toString() {
		return text.replace('\n', '|');
	}

	private void setLabelText() {
		StringBuffer prefix = new StringBuffer();
		StringBuffer suffix = new StringBuffer();
		StringBuffer htmlText = new StringBuffer();
		prefix.append("&nbsp;");
		suffix.insert(0, "&nbsp;");
		if (underlined) {
			prefix.append("<u>");
			suffix.insert(0, "</u>");
		}
		if (size == LARGE) {
			prefix.append("<font size=\"+1\">");
			suffix.insert(0, "</font>");
		}
		if (size == SMALL) {
			prefix.append("<font size=\"-1\">");
			suffix.insert(0, "</font>");
		}
		htmlText.append("<html>");
		StringTokenizer tokenizer = new StringTokenizer(text, "\n");
		boolean first = true;
		while (tokenizer.hasMoreTokens()) {
			if (first)
				first = false;
			else
				htmlText.append("<br>");
			htmlText.append(prefix);
			htmlText.append(tokenizer.nextToken());
			htmlText.append(suffix);
		}
		htmlText.append("</html>");

		// replace any < that are not followed by {u, i, b, tt, font, br} with
		// &lt;

		List dontReplace = Arrays.asList(new String[] { "u", "i", "b", "tt", "font", "br" });

		int ltpos = 0;
		while (ltpos != -1) {
			ltpos = htmlText.indexOf("<", ltpos + 1);
			if (ltpos != -1 && !(ltpos + 1 < htmlText.length() && htmlText.charAt(ltpos + 1) == '/')) {
				int end = ltpos + 1;
				while (end < htmlText.length() && Character.isLetter(htmlText.charAt(end)))
					end++;
				if (!dontReplace.contains(htmlText.substring(ltpos + 1, end)))
					htmlText.replace(ltpos, ltpos + 1, "&lt;");
			}
		}

		label.setText(htmlText.toString());
		if (justification == LEFT)
			label.setHorizontalAlignment(JLabel.LEFT);
		else if (justification == CENTER)
			label.setHorizontalAlignment(JLabel.CENTER);
		else if (justification == RIGHT)
			label.setHorizontalAlignment(JLabel.RIGHT);
	}

	/**
	 */
	public Rectangle2D getBounds(Graphics2D g2) {
		if (text.trim().length() == 0)
			return new Rectangle2D.Double();
		// setLabelText();
		Dimension dim = label.getPreferredSize();
		return new Rectangle2D.Double(0, 0, dim.getWidth(), dim.getHeight());
	}

	/**
	 */
	public void draw(Graphics2D g2, Rectangle2D r) {
		// setLabelText();
		label.setFont(g2.getFont());
		label.setBounds(0, 0, (int) r.getWidth(), (int) r.getHeight());
		g2.translate(r.getX(), r.getY());
		label.paint(g2);
		g2.translate(-r.getX(), -r.getY());
	}

	public Object clone() {
		try {
			TextArea cloned = (TextArea) super.clone();
			cloned.label = new JLabel();
			cloned.setLabelText();
			return cloned;
		} catch (CloneNotSupportedException exception) {
			return null;
		}
	}

	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	public static final int LARGE = 3;
	public static final int NORMAL = 4;
	public static final int SMALL = 5;

	private static final int GAP = 3;

	private String text;
	private int justification;
	private int size;
	private boolean underlined;

	private transient JLabel label = new JLabel();
}
