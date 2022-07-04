package com.example.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ProjectController {
	private File projectJsonFile;
	
	public ProjectController() {
		
	}
	
	public void saveProject(Project p, String pathName) throws IOException, URISyntaxException {
		//projectJsonFile = new File("src\\main\\resources\\project.json");
		//String objectJsonString = new PrintReader()
		//List<String> patterns = Arrays.asList("patternTable1.chr", "patternTable2.chr", "patternTable3.chr");
		//List<String> nameTables = Arrays.asList("nameTable1.chr", "nameTable2.chr", "nameTable3.chr");
		//List<String> palettes = Arrays.asList("palette1.chr", "palette2.chr", "palette3.chr");
		//Project p = new Project("default-project", patterns, nameTables, palettes);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileString = gson.toJson(p);
		System.out.println(fileString);
		
		try (PrintWriter out = new PrintWriter(new FileWriter(pathName))) {
            out.write(fileString);
        } catch (Exception e) {
            e.printStackTrace();
        }
		//return gson.toJson(p);
	}
	
	public Project openProject(String projectPath) throws IOException, URISyntaxException{
		Gson gson = new Gson();
		File file = new File(projectPath);
		Reader reader = Files.newBufferedReader(Paths.get(file.toURI()));
		Type type = new TypeToken<Project>(){}.getType();
		Project p = gson.fromJson(reader, type);
		return p;
	}
}
