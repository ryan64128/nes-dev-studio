package com.example;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.example.model.NameTableComposite;
import com.example.model.PatternTableComposite;
import com.example.utils.Project;
import com.example.utils.ProjectController;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class AppMain {

	protected Shell shell;
	
	// palette data --------------------------------------------------------------------------------------------------------
	private PaletteDialog paletteDialog;
	private Color[][] palettes = {{new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)},
								  {new Color(84,84,84), new Color(48,0,136), new Color(8,58,0), new Color(0,0,0)}};
	private int paletteWidth = 128;
	private int paletteHeight = 32;
	private int currentPaletteIndex = 0;
	private int currentPalette = 0;
	
	// mouse fields --------------------------------------------------------------------------------------------------------
	private int mouseX = 0;
	private int mouseY = 0;
	private boolean mouseDown = false;
	
	// file browser fields -------------------------------------------------------------------------------------------------
	private ScrolledComposite fileBrowserComposite;
	protected Tree fileBrowserTree;
	private static Project currentProject;
	private File projectFile;
	private List<File> patternFiles;
	private List<File> nameTableFiles;
	private List<File> paletteFiles;
	private PatternTableComposite patternTableComposite;
	private NameTableComposite nameTableComposite;
	

	public static void main(String[] args) {
		try {
			AppMain window = new AppMain();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void open() {
		patternFiles = new ArrayList<File>();
		nameTableFiles = new ArrayList<File>();
		paletteFiles = new ArrayList<File>();
		Display display = Display.getDefault();
		shell = new Shell();
		shell.setSize(1300, 1000);
		shell.setLayout(null);
		
		// Build Main Section Tabs - Pattern Tables, Name Tables, Palettes -------------------------------------------------
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(200, 0, 1100, 1000);
		Group patternGroup = new Group(tabFolder, SWT.NONE);
		Group nameTableGroup = new Group(tabFolder, SWT.NONE);
		Group paletteGroup = new Group(tabFolder, SWT.NONE);
		
		TabItem patternTab = new TabItem(tabFolder, SWT.NONE);
		patternTab.setText("Pattern Tables");
		patternTab.setControl(patternGroup);
		
		patternTableComposite = new PatternTableComposite(patternGroup, SWT.NONE, shell);
		patternTableComposite.setBounds(0, 0, 1100, 1000);
		
		TabItem nameTableTab = new TabItem(tabFolder, SWT.NONE);
		nameTableTab.setText("Name Tables");
		nameTableTab.setControl(nameTableGroup);
		
		nameTableComposite = new NameTableComposite(nameTableGroup, SWT.NONE, shell);
		nameTableComposite.setBounds(0, 0, 1100, 1000);
		
		TabItem paletteTab = new TabItem(tabFolder, SWT.NONE);
		paletteTab.setText("Palettes");
		paletteTab.setControl(paletteGroup);
		
		paletteDialog = new PaletteDialog(shell);
		
		fileBrowserComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fileBrowserComposite.setBounds(0, 0, 194, 773);
		fileBrowserComposite.setExpandHorizontal(true);
		fileBrowserComposite.setExpandVertical(true);
		
		fileBrowserTree = loadFileTree();
		fileBrowserComposite.setContent(fileBrowserTree);
		fileBrowserComposite.setMinSize(fileBrowserTree.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("File");
		
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
	    fileMenuItem.setMenu(fileMenu);
	    
	    MenuItem fileSaveAsItem = new MenuItem(fileMenu, SWT.NONE);
	    fileSaveAsItem.setText("Save Project As");
	    fileSaveAsItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				try {
					saveProject();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	   
	    MenuItem fileOpenProjectItem = new MenuItem(fileMenu, SWT.NONE);
	    fileOpenProjectItem.setText("Open Project");
	    fileOpenProjectItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					openProject();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    MenuItem fileNewProjectItem = new MenuItem(fileMenu, SWT.NONE);
	    fileNewProjectItem.setText("New Project");
	    fileNewProjectItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("You Selected New Project: " + ((MenuItem) e.widget).getText());
				try {
					createNewProject();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    MenuItem fileAddPatternItem = new MenuItem(fileMenu, SWT.NONE);
	    fileAddPatternItem.setText("Add Pattern Table");
	    fileAddPatternItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("You Selected " + ((MenuItem) e.widget).getText());
				try {
					createNewPatternFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    MenuItem fileAddNameTableItem = new MenuItem(fileMenu, SWT.NONE);
	    fileAddNameTableItem.setText("Add Name Table");
	    fileAddNameTableItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				createNewNameTableFile();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    MenuItem fileImportPatternItem = new MenuItem(fileMenu, SWT.NONE);
	    fileImportPatternItem.setText("Import Pattern Table");
	    fileImportPatternItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					importPatternFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		shell.layout();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1300, 1000);
		shell.setText("SWT Application");
		shell.setLayout(null);
	}
	
	
	// creates new empty project json file
	private void createNewProject() throws IOException, URISyntaxException {
		FileDialog outputFileDialog = new FileDialog(shell, SWT.SAVE);
		outputFileDialog.setFileName("new-project.json");
		String[] projectExtensions = {"json"};
		outputFileDialog.setFilterExtensions(projectExtensions);
		String pathName = outputFileDialog.open();
		projectFile = new File(pathName);
		Boolean patternCreated = new File(projectFile.getParent() + "/patterns/").mkdir();
		Boolean nametableCreated = new File(projectFile.getParent() + "/nametables/").mkdir();
		Boolean palatteCreated = new File(projectFile.getParent() + "/palettes/").mkdir();
		Project project = new Project();
		String[] split = pathName.split("/");
		String fileName = split[split.length-1];
		project.setProjectName(fileName);
		ProjectController p = new ProjectController();
		p.saveProject(project, pathName);
		currentProject = project;
		loadFileTree();
	}
	
	private void createNewPatternFile() throws IOException {
		FileDialog outputFileDialog = new FileDialog(shell, SWT.SAVE);
		outputFileDialog.setFileName("newPattern.chr");
		String[] projectExtensions = {"chr"};
		outputFileDialog.setFilterExtensions(projectExtensions);
		String pathName = outputFileDialog.open();
		String newPatternName = outputFileDialog.getFileName();
		patternFiles.add(new File(projectFile.getParent() + "/patterns/" + newPatternName));
		currentProject.addPattern(newPatternName);
		loadFileTree();
		patternTableComposite.setCurrentPatternTableIndex(patternFiles.size() - 1);
		patternTableComposite.loadFile(newPatternName);
	}
	
	private void selectPatternFile(String fileName) {
		int patternFileIndex = patternFiles.indexOf(new File(projectFile.getParent() + "/patterns/" + fileName));
		patternTableComposite.setCurrentPatternTableIndex(patternFileIndex);
		patternTableComposite.redraw();
		patternTableComposite.getCompositeMain().redraw();
	}
	
	private void importPatternFile() throws IOException{
		FileDialog inputFileDialog = new FileDialog(shell, SWT.OPEN);
		inputFileDialog.setFileName("newPattern.chr");
		String[] projectExtensions = {"chr"};
		inputFileDialog.setFilterExtensions(projectExtensions);
		String pathName = inputFileDialog.open();
		String newPatternName = inputFileDialog.getFileName();
		patternFiles.add(new File(projectFile.getParent() + "/patterns/" + newPatternName));
		currentProject.addPattern(newPatternName);
		loadFileTree();
		patternTableComposite.setCurrentPatternTableIndex(patternFiles.size() - 1);
		patternTableComposite.loadFile(pathName);
	}
	
	private void createNewNameTableFile() {
		FileDialog outputFileDialog = new FileDialog(shell, SWT.SAVE);
		outputFileDialog.setFileName("newNameTable.chr");
		String[] projectExtensions = {"chr"};
		outputFileDialog.setFilterExtensions(projectExtensions);
		String pathName = outputFileDialog.open();
		String newNameTableName = outputFileDialog.getFileName();
		nameTableFiles.add(new File(projectFile.getParent() + "/nameTables/" + newNameTableName));
		currentProject.addNameTable(newNameTableName);
		loadFileTree();
		// set variables for new nametable here as needed
	}
	private void createNewPaletteFile() {
		String newPaletteName = "testPalette" + paletteFiles.size() + ".chr";
		paletteFiles.add(new File(projectFile.getParent() + "/palettes/" + newPaletteName));
		currentProject.addPalette(newPaletteName);
		loadFileTree();
	}
	
	private void saveProject() throws IOException, URISyntaxException{
		FileDialog outputFileDialog = new FileDialog(shell, SWT.SAVE);
		outputFileDialog.setFileName(projectFile.getName());
		String[] projectExtensions = {"json"};
		outputFileDialog.setFilterExtensions(projectExtensions);
		String pathName = outputFileDialog.open();
		ProjectController p = new ProjectController();
		p.saveProject(currentProject, pathName);
		
		System.out.println("Pattern Files: " + patternFiles.size());
		// Save Pattern Table Files
		for (int i=0; i<patternFiles.size(); i++) {
			FileOutputStream fStream = new FileOutputStream(patternFiles.get(i));
			fStream.write(patternTableComposite.getNesFormattedPatternData(i));
			fStream.close();
		}
	}
	
	private void openProject() throws IOException, URISyntaxException {
		FileDialog outputFileDialog = new FileDialog(shell, SWT.OPEN);
		outputFileDialog.setFileName("");
		String[] projectExtensions = {"json"};
		outputFileDialog.setFilterExtensions(projectExtensions);
		String pathName = outputFileDialog.open();
		projectFile = new File(pathName);
		patternFiles = new ArrayList<File>();
		nameTableFiles = new ArrayList<File>();
		paletteFiles = new ArrayList<File>();
		ProjectController p = new ProjectController();
		currentProject = p.openProject(pathName);
		for (String s : currentProject.getPatterns()) {
			patternFiles.add(new File(projectFile.getParent() + "/patterns/" + s));
			patternTableComposite.setCurrentPatternTableIndex(patternFiles.size() - 1);
			patternTableComposite.loadFile(projectFile.getParent() + "/patterns/" + s);
		}
		for (String s : currentProject.getNameTables()) {
			nameTableFiles.add(new File(projectFile.getParent() + "/nametables/" + s));
		}
		for (String s : currentProject.getPalettes()) {
			paletteFiles.add(new File(projectFile.getParent() + "/palettes/" + s));
		}
		loadFileTree();
	}
	
	private Tree loadFileTree() {
		fileBrowserTree = new Tree(fileBrowserComposite, SWT.BORDER);
		fileBrowserComposite.setContent(fileBrowserTree);
		fileBrowserComposite.setMinSize(fileBrowserTree.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		fileBrowserTree.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event e) {
				System.out.println(fileBrowserTree.getSelection()[0].getText());
				if (fileBrowserTree.getSelection()[0].getParentItem().getText().equals("Patterns")) {
					System.out.println("You Selected a Pattern Table File!!!");
					selectPatternFile(fileBrowserTree.getSelection()[0].getText());
					patternTableComposite.redraw();
				}
			}
		});
		
		if (currentProject != null) {
			TreeItem projectFolder = new TreeItem(fileBrowserTree, SWT.NONE);
			projectFolder.setText(currentProject.getProjectName());
			
			TreeItem patternsFolder = new TreeItem(projectFolder, SWT.NONE);
			patternsFolder.setText("Patterns");
			for (String s : currentProject.getPatterns()) {
				TreeItem treeItem = new TreeItem(patternsFolder, SWT.NONE);
				treeItem.setText(s);
			}

			TreeItem nameTablesFolder = new TreeItem(projectFolder, SWT.NONE);
			nameTablesFolder.setText("NameTables");
			for (String s : currentProject.getNameTables()) {
				TreeItem treeItem = new TreeItem(nameTablesFolder, SWT.NONE);
				treeItem.setText(s);
			}

			TreeItem palettesFolder = new TreeItem(projectFolder, SWT.NONE);
			palettesFolder.setText("Palettes");
			for (String s : currentProject.getPalettes()) {
				TreeItem treeItem = new TreeItem(palettesFolder, SWT.NONE);
				treeItem.setText(s);
			}
		}
		if (fileBrowserTree.getItems().length > 0) {
			fileBrowserTree.getItems()[0].setExpanded(true);
			TreeItem[] items = fileBrowserTree.getItems()[0].getItems();
			for (TreeItem t : items) {
				t.setExpanded(true);
			}
		}
		return fileBrowserTree;
	}
}
