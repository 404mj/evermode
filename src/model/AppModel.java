package model;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;

import javax.swing.tree.TreeNode;

public class AppModel extends Observable implements TreeNode {

	private List<String> workspaceLocations;
	private String openedWorkspace;
	private Dimension windowDimension;
	private Point2D windowPosition;
	private String localization;
	private String theme;
	private Diagram selectedDiagram;
	private String selectedProject;
	private String openedDiagram;
	private List<ProjectModel> projectList;
	private List<String> openDiagrams;

	public AppModel() {
		workspaceLocations = new ArrayList();
		openDiagrams = new ArrayList();
		projectList = new ArrayList();
		// workspaceLocations.add("C:\\Users\\Milos\\Evermode\\Default
		// Workspace");
		workspaceLocations.add("/home/zsx/tmp");
		windowDimension = new Dimension(1366, 738);
		windowPosition = new Point2D.Double(0, 0);
		// 定义默认的语言和主题
		localization = "en";
		theme = "javax.swing.plaf.metal.MetalLookAndFeel";
	}

	public Diagram getOpenedDiagram(String diagram) {
		for (ProjectModel project : projectList) {
			String projectName = project.getProjectName();
			String projectDiag = (new File(diagram).getParent()).replace("\\", "/");
			if (projectName.equals(projectDiag)) {
				for (Diagram dia : project.getDiagrams()) {
					if (dia.getName().equals(diagram))
						return dia;
				}
			}
		}
		return null;
	}

	public void addDiagram(ProjectModel project, Diagram diagram) {
		// 添加新的画布
		project.addDiagram(diagram);
		setSelectedDiagram(diagram);
		// 通知订阅者！
		notifyViews();
	}

	public ProjectModel getSelectedProject() {
		for (ProjectModel project : projectList) {
			if (project.getProjectName().equals(selectedProject))
				return project;
		}
		return null;
	}

	public ProjectModel addNewProject(String projectName) {
		ProjectModel project = new ProjectModel(projectName, this);
		projectList.add(project);
		setSelectedProject(projectName);
		notifyViews();
		return project;
	}

	public void addWorkspaceLocation(String workspaceLocation) {
		workspaceLocations.add(workspaceLocation);
	}

	public void addOpenDiagram(String diagramName) {
		openDiagrams.add(diagramName);
	}

	public void setSelectedProject(String selectedProject2) {
		this.selectedProject = selectedProject2;
		setChanged();//先设置setChanged()才能通知
		notifyViews();
	}

	public String toString() {
		String name = new File(getOpenedWorkspace()).getName();
		return name;
	}

	public void notifyViews() {
		setChanged();
		notifyObservers();
	}

	/**
	 * Getters and Setters
	 */

	public void setWindowDimension(double w, double h) {
		windowDimension.setSize(w, h);
	}

	public Diagram getSelectedDiagram() {
		return selectedDiagram;
	}

	public void setSelectedDiagram(Diagram diagram) {
		selectedDiagram = diagram;
		notifyViews();
	}

	public void setOpenedDiagram(String diagram) {
		openedDiagram = diagram;
	}

	public String getOpenedDiagram() {
		return openedDiagram;
	}

	public Dimension getWindowDimension() {
		return windowDimension;
	}

	public void setWindowPosition(double x, double y) {
		windowPosition.setLocation(x, y);
	}

	public Point2D getWindowPosition() {
		return windowPosition;
	}

	public void setLocalization(String localization) {
		this.localization = localization;
	}

	public String getLocalizatoin() {
		return localization;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return theme;
	}

	public List<ProjectModel> getProjects() {
		return projectList;
	}

	public List<String> getWorkspaceLocations() {
		return workspaceLocations;
	}

	public void setOpenedWorksace(String openedWorkspace) {
		this.openedWorkspace = openedWorkspace;
		setChanged();
		notifyObservers();
	}

	public String getOpenedWorkspace() {
		return openedWorkspace;
	}

	public List<String> getOpenedDiagrams() {
		return openDiagrams;
	}

	@Override
	public Enumeration children() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount() {
		return projectList.size();
	}

	@Override
	public int getIndex(TreeNode arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	public void clean() {
		this.selectedDiagram = null;
		this.selectedProject = null;
		this.projectList.clear();
		this.openDiagrams.clear();
		this.openedDiagram = null;
	}

}
