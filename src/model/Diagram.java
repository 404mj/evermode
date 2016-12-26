package model;

import helpers.DOMParser;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

import view.Grid;
import model.abstracts.AbstractNode;
import model.abstracts.Edge;
import model.abstracts.Node;

/**
 * 抽象图画类Model，被观察者
 * 
 * 图由Node和可选择的Edge组成。
 * 
 */
public abstract class Diagram extends Observable {

	@SuppressWarnings("rawtypes")
	private ArrayList tools;
	private int selectedTool = 0;
	private String name;
	private AppModel application;
	public Object lastSelected;
	public Set selectedItems;
	public boolean modified;
	public Point2D lastMousePoint;
	public Point2D mouseDownPoint;
	public double zoom = 1;

	public Diagram(AppModel application) {
		this();
		this.application = application;
	}

	/**
	 * 构造函数
	 */
	public Diagram() {
		/*
		 * 初始化用到的数据结构
		 */
		nodes = new ArrayList();
		edges = new ArrayList();
		nodesToBeRemoved = new ArrayList();
		edgesToBeRemoved = new ArrayList();
		needsLayout = true;
		lastMousePoint = new Point2D.Double(0, 0);
		tools = new ArrayList();
		tools.add(null);

		for (Node n : getNodePrototypes()) {
			tools.add(n);
		}
		for (Edge e : getEdgePrototypes()) {
			tools.add(e);
		}
		selectedItems = new HashSet();
	}

	public String getName() {
		return name;
	}

	public String toString() {
		String name = new File(this.name).getName();
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setChanged();
		notifyObservers();
	}

	public ArrayList getTools() {
		return tools;
	}

	public void addTool(Object tool) {
		tools.add(tool);
	}

	public Object getSelectedTool() {
		return tools.get(selectedTool);
	}

	public int getSelectedToolIndex() {
		return selectedTool;
	}

	public void setSelectedTool(int i) {
		selectedTool = i;
		application.notifyViews();
		setChanged();
		notifyObservers();
	}

	public void addSelectedItem(Object obj) {
		lastSelected = obj;
		selectedItems.add(obj);
		application.notifyViews();
	}

	public Set getSelectedItem() {
		return selectedItems;
	}

	public void removeSelectedItem(Object obj) {
		if (obj == lastSelected)
			lastSelected = null;
		selectedItems.remove(obj);
		// saveGraphIntoHistory();
	}

	public void setSelectedItem(Object obj) {
		selectedItems.clear();
		lastSelected = obj;
		if (obj != null)
			selectedItems.add(obj);
		application.notifyViews();
	}

	public void clearSelection() {
		selectedItems.clear();
		lastSelected = null;
	}

	@SuppressWarnings("unchecked")
	public void selectNext(int n) {
		ArrayList selectables = new ArrayList();
		selectables.addAll(getNodes());
		selectables.addAll(getEdges());
		if (selectables.size() == 0)
			return;
		java.util.Collections.sort(selectables, new java.util.Comparator() {
			public int compare(Object obj1, Object obj2) {
				double x1;
				double y1;
				if (obj1 instanceof Node) {
					Rectangle2D bounds = ((Node) obj1).getBounds();
					x1 = bounds.getX();
					y1 = bounds.getY();
				} else {
					Point2D start = ((Edge) obj1).getConnectionPoints().getP1();
					x1 = start.getX();
					y1 = start.getY();
				}
				double x2;
				double y2;
				if (obj2 instanceof Node) {
					Rectangle2D bounds = ((Node) obj2).getBounds();
					x2 = bounds.getX();
					y2 = bounds.getY();
				} else {
					Point2D start = ((Edge) obj2).getConnectionPoints().getP1();
					x2 = start.getX();
					y2 = start.getY();
				}
				if (y1 < y2)
					return -1;
				if (y1 > y2)
					return 1;
				if (x1 < x2)
					return -1;
				if (x1 > x2)
					return 1;
				return 0;
			}
		});
		int index;
		if (lastSelected == null)
			index = 0;
		else
			index = selectables.indexOf(lastSelected) + n;
		while (index < 0)
			index += selectables.size();
		index %= selectables.size();
		setSelectedItem(selectables.get(index));
		setChanged();
		notifyObservers();
	}

	public void removeSelected() {
		Iterator iter = selectedItems.iterator();
		while (iter.hasNext()) {
			Object selected = iter.next();
			if (selected instanceof Node) {
				removeNode((Node) selected);
			} else if (selected instanceof Edge) {
				removeEdge((Edge) selected);
			}

			removeSelectedItem(selected);
		}

		setChanged();
		notifyObservers();
		application.notifyViews();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
		setChanged();
		notifyObservers();
		application.notifyViews();
	}

	/**
	 * 给两个Node增加一条关系
	 */
	public boolean connect(Edge e, Point2D p1, Point2D p2) {
		Node n1 = findNode(p1);
		Node n2 = findNode(p2);
		if (n1 != null) {
			e.connect(n1, n2);
			if (n1.addEdge(e, p1, p2) && e.getEnd() != null) {
				edges.add(e);
				if (!nodes.contains(e.getEnd()))
					nodes.add(e.getEnd());
				needsLayout = true;
				saveGraphIntoHistory();
				return true;
			}
		}
		return false;
	}

	/**
	 * 将一个节点添加到指定的坐标处
	 */
	public boolean add(Node n, Point2D p) {
		Rectangle2D bounds = n.getBounds();
		n.translate(p.getX() - bounds.getX(), p.getY() - bounds.getY());

		boolean accepted = false;
		boolean insideANode = false;
		for (int i = nodes.size() - 1; i >= 0 && !accepted; i--) {
			Node parent = (Node) nodes.get(i);
			if (parent.contains(p)) {
				insideANode = true;
				if (parent.addNode(n, p))
					accepted = true;
			}
		}
		if (insideANode && !accepted)
			return false;
		nodes.add(n);
		needsLayout = true;
		saveGraphIntoHistory();
		application.notifyViews();
		return true;
	}

	/**
	 * 找到含有指定点的Node
	 * 
	 * 分别根据坐标和Hash值
	 */
	public Node findNode(Point2D p) {
		for (int i = nodes.size() - 1; i >= 0; i--) {
			Node n = (Node) nodes.get(i);
			if (n.contains(p))
				return n;
		}
		return null;
	}

	public Node findNode(int hashCode) {
		for (int i = nodes.size() - 1; i >= 0; i--) {
			Node n = (Node) nodes.get(i);
			if (n.hashCode() == hashCode)
				return n;
		}
		return null;
	}

	/**
	 * 找到含有指定点edge
	 */
	public Edge findEdge(Point2D p) {
		for (int i = edges.size() - 1; i >= 0; i--) {
			Edge e = (Edge) edges.get(i);
			if (e.contains(p))
				return e;
		}
		return null;
	}

	/**
	 * draw the graphics
	 * 
	 * @param g2
	 *            the content of Graphics
	 */
	public void draw(Graphics2D g2, Grid g) {
		layout(g2, g);

		for (int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			n.draw(g2);
		}

		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);
			e.draw(g2);
		}
	}

	/**
	 * 删除一个Node
	 * 
	 * 与之相对应的Edge和与之关联的另一个点也要处理
	 * 
	 * @param n
	 */
	public void removeNode(Node n) {
		if (nodesToBeRemoved.contains(n))
			return;
		nodesToBeRemoved.add(n);
		// notify nodes of removals
		for (int i = 0; i < nodes.size(); i++) {
			Node n2 = (Node) nodes.get(i);
			n2.removeNode(this, n);
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);
			if (e.getStart() == n || e.getEnd() == n)
				removeEdge(e);
		}
		needsLayout = true;
	}

	/**
	 * 删除Edge
	 * 
	 * @param e
	 */
	public void removeEdge(Edge e) {
		if (edgesToBeRemoved.contains(e))
			return;
		edgesToBeRemoved.add(e);// 为了实现撤销功能，先不真删除
		for (int i = nodes.size() - 1; i >= 0; i--) {
			Node n = (Node) nodes.get(i);
			n.removeEdge(this, e);
		}
		needsLayout = true;
	}

	public void layout() {
		needsLayout = true;
	}

	protected void layout(Graphics2D g2, Grid g) {
		if (!needsLayout)
			return;
		nodes.removeAll(nodesToBeRemoved);
		edges.removeAll(edgesToBeRemoved);
		nodesToBeRemoved.clear();
		edgesToBeRemoved.clear();

		for (int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			n.layout(this, g2, g);
		}
		needsLayout = false;
	}

	/**
	 * 
	 */
	public Rectangle2D getBounds(Graphics2D g2) {
		Rectangle2D r = minBounds;
		for (int i = 0; i < nodes.size(); i++) {
			Node n = (Node) nodes.get(i);
			Rectangle2D b = n.getBounds();
			if (r == null)
				r = b;
			else
				r.add(b);
		}
		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);
			r.add(e.getBounds(g2));
		}
		return r == null ? new Rectangle2D.Double()
				: new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth() + AbstractNode.SHADOW_GAP,
						r.getHeight() + AbstractNode.SHADOW_GAP);
	}

	public Rectangle2D getClipBounds() {
		Rectangle2D r = minBounds;
		for (Node n : nodes) {
			Rectangle2D b = n.getBounds();
			if (r == null)
				r = b;
			else
				r.add(b);
		}
		return r == null ? new Rectangle2D.Double()
				: new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public void changeZoom(int steps) {
		final double FACTOR = 1.5;
		for (int i = 1; i <= steps; i++)
			zoom *= FACTOR;
		for (int i = 1; i <= -steps; i++)
			zoom /= FACTOR;
		setChanged();
		notifyObservers();
	}

	public Rectangle2D getMinBounds() {
		return minBounds;
	}

	public void setMinBounds(Rectangle2D newValue) {
		minBounds = newValue;
	}

	/**
	 * 获得特定节点的类型
	 * 
	 * @return
	 */
	public abstract Node[] getNodePrototypes();

	/**
	 * 获取特定边的类型
	 * 
	 * @return
	 */
	public abstract Edge[] getEdgePrototypes();

	/**
	 * 获取当前图标的节点S
	 * 
	 * @return
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * 获取当前图形的边
	 * 
	 * @return
	 */
	public ArrayList getEdges() {
		return edges;
	}

	/**
	 * 
	 * 把节点添加到当前图形中
	 * 
	 * 只有当读取文件时候才调用此方法
	 * 
	 * @param n
	 * @param p
	 */
	public void addNode(Node n, Point2D p) {
		Rectangle2D bounds = n.getBounds();
		n.translate(p.getX() - bounds.getX(), p.getY() - bounds.getY());
		nodes.add(n);

	}

	/**
	 * 为当前图像添加一个边框。
	 * 
	 * @param e
	 * @param start
	 * @param end
	 */
	public void connect(Edge e, Node start, Node end) {
		e.connect(start, end);
		edges.add(e);
	}

	/**
	 * 把当前Node加入到History 空间内
	 */
	public void saveGraphIntoHistory() {
		int size = graphHistory.size();
		if (graphHistoryCursor < size - 1) {
			int nextPosition = graphHistoryCursor;
			for (int i = size - 1; i > nextPosition; i--) {
				graphHistory.remove(i);
			}
		}
		size = graphHistory.size();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DOMParser.writeDiagram(this, byteArrayOutputStream);
		graphHistory.add(byteArrayOutputStream);
		graphHistoryCursor = graphHistory.size() - 1;
		while (size > GRAPH_HISTORY_CAPACITY) {
			graphHistory.remove(0);
			size = graphHistory.size();
		}
	}

	private static final int GRAPH_HISTORY_CAPACITY = 50;

	public ArrayList graphHistory = new ArrayList();
	public int graphHistoryCursor = 0;
	public ArrayList<Node> nodes;
	public ArrayList<Edge> edges;
	public transient ArrayList nodesToBeRemoved;
	public transient ArrayList edgesToBeRemoved;
	public transient boolean needsLayout;
	public transient Rectangle2D minBounds;

	public void setDiagram(Diagram d) {
		this.edges = d.edges;
		this.nodes = d.nodes;
		this.selectedItems.clear();
		this.layout();
	}

	public void setLastMousePoint(Point2D mousePoint) {
		lastMousePoint = mousePoint;
		application.notifyViews();
	}

}
