package com.example.utils;

import java.util.ArrayList;
import java.util.List;

public class Project {
	private String projectName;
	private List<String> patterns;
	private List<String> nameTables;
	private List<String> palettes;
	
	public Project() {
		this.projectName = "";
		this.patterns = new ArrayList<String>();
		this.nameTables = new ArrayList<String>();
		this.palettes = new ArrayList<String>();
	}
	
	
	
	public Project(String projectName, List<String> patterns, List<String> nameTables, List<String> palettes) {
		super();
		this.projectName = projectName;
		this.patterns = patterns;
		this.nameTables = nameTables;
		this.palettes = palettes;
	}

	

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}



	public List<String> getPatterns() {
		return patterns;
	}
	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
	public void addPattern(String s) {
		this.patterns.add(s);
	}


	public List<String> getNameTables() {
		return nameTables;
	}
	public void setNameTables(List<String> nameTables) {
		this.nameTables = nameTables;
	}
	public void addNameTable(String s) {
		this.nameTables.add(s);
	}


	public List<String> getPalettes() {
		return palettes;
	}
	public void setPalettes(List<String> palettes) {
		this.palettes = palettes;
	}
	public void addPalette(String s) {
		this.palettes.add(s);
	}


	@Override
	public String toString() {
		String out =  "projectName: " + this.getProjectName() + "\n";
		out += "patterns: \n";
		if (this.patterns != null)
		for (String s : this.patterns) {
			out += "\t" + s + "\n";
		}
		out += "nameTables: \n";
		if (this.nameTables != null)
		for (String s : this.nameTables) {
			out += "\t" + s + "\n";
		}
		out += "palettes: \n";
		if (this.palettes != null)
		for (String s : this.palettes) {
			out += "\t" + s + "\n";
		}
		return out;
	}
}
