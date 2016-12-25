package model.diagram;

import java.util.ArrayList;

import model.LineType;
import model.abstracts.SegmentedEdge;

/**
 * 类之间关系的edge
 */
public class ClassRelationshipEdge extends SegmentedEdge {

	/**
	 * 构造 edge
	 */
	public ClassRelationshipEdge() {
		lineType = LineType.STRAIGHT;
	}

	public void setBentStyle(LineType newValue) {
		lineType = newValue;
	}

	public LineType getBentStyle() {
		return lineType;
	}

	public ArrayList getPoints() {
		return lineType.getPath(getStart().getBounds(), getEnd().getBounds());
	}

	private LineType lineType;

}
