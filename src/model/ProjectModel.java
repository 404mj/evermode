package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 项目的实体
 * 
 * 被观察者，project的model
 * 
 * 属于某个appModel，维护多个diagram
 */
public class ProjectModel extends Observable {
	private String projectName;
	private List<Diagram> diagramList = new ArrayList<Diagram>();
	private AppModel application;

	public String getProjectName() {
		return projectName;
	}

	public String toString() {
		String name = new File(projectName).getName();
		return name;
	}

	public void setProjectName(String projectName) {
		for (Diagram diagram : diagramList) {
			String old = toString();
			String newS = new File(projectName).getName();
			diagram.setName(diagram.getName().replace(toString(), new File(projectName).getName()));

		}

		this.projectName = projectName;

	}

	public ProjectModel(String projectName, AppModel application) {
		setProjectName(projectName);
		this.application = application;
	}

	public List<Diagram> getDiagrams() {
		return diagramList;
	}

	public void addDiagram(Diagram diagram) {
		diagramList.add(diagram);
		setChanged();
		notifyObservers();
	}

	public Diagram getDiagram(String name) {
		for (Diagram diagram : diagramList) {
			if (diagram.getName().equals(name))
				return diagram;
		}
		return null;
	}
}
