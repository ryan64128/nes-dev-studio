package com.example.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.example.PaletteDialog;

public class PatternTableComposite extends Composite {
	protected Shell parentShell;
	protected ScrolledComposite scrolledComposite;
	protected ScrolledComposite compositeMain;
	private Label lblTileNumber;
	
	// graphics data fields for pattern tables -----------------------------------------------------------------------------
	private List<byte[]> nesFormattedPatternData;
	private List<int[][][]> displayFormattedData;
	private int currentPatternTableIndex = 0;
	private String currentCharFileName = "";

	// display and drawing fields ------------------------------------------------------------------------------------------
	private int compositeWidth = 256;
	private int compositeGridWidth = compositeWidth / 8;
	private int GRID_COUNT = 8;
	private int CANVAS_WIDTH = 640;
	private int SPRITE_WIDTH = CANVAS_WIDTH / 16;
	private int gridWidth = SPRITE_WIDTH / GRID_COUNT;
	private int currentXIndex = 0;
	private int currentYIndex = 0;
	private int currentSpriteIndex = 0;
	private final int EDIT_MODE = 0;
	private final int SELECT_MODE = 1;
	private int currentMode = EDIT_MODE;
	private boolean showGrid = false;
	
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
	
	private List<File> patternFiles;

	
	public PatternTableComposite(Composite parent, int style, Shell parentShell) {
		super(parent, style);
		this.parentShell = parentShell;
		// create initial empty nes data buffer for pattern table
		nesFormattedPatternData = new ArrayList<byte[]>();
		nesFormattedPatternData.add(new byte[4096]);
		// create initial empty display ready data buffer to display pattern tables
		displayFormattedData = new ArrayList<int[][][]>();
		displayFormattedData.add(new int[256][8][8]);
		
		parentShell.setText("NES Studio - " + currentCharFileName);
		
		lblTileNumber = new Label(this, SWT.NONE);
		lblTileNumber.setBounds(795, 320, 150, 14);
		lblTileNumber.setText("Tile Number: " + currentSpriteIndex);
		
		Button btnEditMode = new Button(this, SWT.RADIO);
		btnEditMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentMode = EDIT_MODE;
			}
		});
		btnEditMode.setBounds(795, 285, 89, 15);
		btnEditMode.setText("Edit Mode");
		btnEditMode.setSelection(true);
		
		Button btnSelectmode = new Button(this, SWT.RADIO);
		btnSelectmode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentMode = SELECT_MODE;
			}
		});
		btnSelectmode.setBounds(929, 285, 116, 15);
		btnSelectmode.setText("SelectMode");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBounds(797, 201, 64, 64);
		
		Label lblCurrentColor = new Label(this, SWT.NONE);
		lblCurrentColor.setBounds(789, 181, 96, 14);
		lblCurrentColor.setText("Current Color");
		
		Button btnShowGrid = new Button(this, SWT.CHECK);
		btnShowGrid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showGrid = !showGrid;
				compositeMain.redraw();
			}
		});
		btnShowGrid.setBounds(929, 249, 93, 16);
		btnShowGrid.setText("Show Grid");
		btnShowGrid.setSelection(false);
		
		Composite[] compositePalettes = new Composite[8];
		paletteDialog = new PaletteDialog(parentShell);
		for (int n=0; n<8; n++) {
			final int compositePaletteIndex = n;
			compositePalettes[n] = new Composite(this, SWT.NONE);
			compositePalettes[n].setBounds(750 + (n/4)*150, 25 + (n%4)*40, paletteWidth, paletteHeight);
			compositePalettes[n].addPaintListener(new PaintListener() {

				@Override
				public void paintControl(PaintEvent e) {
					GC gc = e.gc;
					int colorWidth = paletteWidth / 4;
					for (int i=0; i<4; i++) {
						gc.setBackground(palettes[compositePaletteIndex][i]);
						gc.fillRectangle(i*colorWidth, 0, colorWidth, paletteHeight);
					}

				}
			});
			compositePalettes[n].addMouseListener(new MouseListener() {

				@Override
				public void mouseUp(MouseEvent arg0) {

				}

				@Override
				public void mouseDown(MouseEvent e) {
					currentPaletteIndex = e.x / (paletteWidth / 4);
					currentPalette = compositePaletteIndex;
					compositeMain.redraw();
					scrolledComposite.redraw();
					composite.setBackground(palettes[currentPalette][currentPaletteIndex]);
				}

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					currentPaletteIndex = e.x / (paletteWidth / 4);
					currentPalette = compositePaletteIndex;
					palettes[currentPalette][currentPaletteIndex] = paletteDialog.open();
					compositeMain.redraw();
					scrolledComposite.redraw();
					compositePalettes[compositePaletteIndex].redraw();
					composite.setBackground(palettes[currentPalette][currentPaletteIndex]);
				}
			});
		}
		
		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setBounds(789, 350, 256+5, 256+5);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setForeground(new Color(200,200,200));
				
	            gc.drawRectangle(0, 0, compositeWidth, compositeWidth);
	            
	            for (int i=0; i<8; i++) {
            		for (int j=0; j<8; j++) {
            			int index = displayFormattedData.get(currentPatternTableIndex)[currentSpriteIndex][i][j];
            			gc.setBackground(palettes[currentPalette][index]);
            			gc.fillRectangle(j*compositeGridWidth, i*compositeGridWidth, compositeGridWidth, compositeGridWidth);
            		}
	            }

	            // draw gridlines
	            for (int i=0; i<GRID_COUNT; i++) {
	            	gc.drawLine(i*compositeGridWidth, 0, i*compositeGridWidth, 256);
	            	if (i % 8 == 0)
	            		gc.drawLine(i*compositeGridWidth+1, 0, i*compositeGridWidth+1, 256);
	            }
	            for (int i=0; i<GRID_COUNT; i++) {
	            	gc.drawLine(0, i*compositeGridWidth, 256, i*compositeGridWidth);
	            	if (i % 8 == 0)
	            		gc.drawLine(0, i*compositeGridWidth+1, 256, i*compositeGridWidth+1);

	            }
			}
			
		});
		
		scrolledComposite.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				mouseDown = false;
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				displayFormattedData.get(currentPatternTableIndex)[currentSpriteIndex][(e.y / compositeGridWidth)%8][(e.x / compositeGridWidth)%8] = currentPaletteIndex;
				compositeMain.redraw();
				scrolledComposite.redraw();
				mouseDown = true;
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});
		scrolledComposite.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if (e.x > 0 && e.x < 256 && e.y > 0 && e.y < 256) {
					if (mouseDown) {
						displayFormattedData.get(currentPatternTableIndex)[currentSpriteIndex][(e.y / compositeGridWidth)%8][(e.x / compositeGridWidth)%8] = currentPaletteIndex;
						compositeMain.redraw();
						scrolledComposite.redraw();
					}
				}
				else {
					mouseDown = false;
				}
			}
		});

		compositeMain = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		compositeMain.setBounds(0, 0, CANVAS_WIDTH+5, CANVAS_WIDTH+5);
		compositeMain.setExpandHorizontal(true);
		compositeMain.setExpandVertical(true);
		compositeMain.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				
				// Draw Sprite chars
	            for (int n=0; n<256; n++) {
	            	for (int i=0; i<8; i++) {
	            		for (int j=0; j<8; j++) {
	            			int index = displayFormattedData.get(currentPatternTableIndex)[n][i][j];
	            			gc.setBackground(palettes[currentPalette][index]);
	            			gc.fillRectangle(j*gridWidth + (n%16)*8*gridWidth, i*gridWidth+ (n/16)*8*gridWidth, gridWidth, gridWidth);
	            		}
	            	}
	            }
	            
	            // draw outer frame
	            gc.setForeground(new Color(156,156,156));
	            gc.drawRectangle(0, 0, CANVAS_WIDTH, CANVAS_WIDTH);
	            
	            // draw gridlines
	            for (int i=0; i<GRID_COUNT*16; i++) {
	            	if (showGrid)
	            		gc.drawLine(i*gridWidth, 0, i*gridWidth, CANVAS_WIDTH);
	            	if (i % 8 == 0)
		            	gc.drawLine(i*gridWidth+1, 0, i*gridWidth+1, CANVAS_WIDTH);
	            }
	            for (int i=0; i<GRID_COUNT*16; i++) {
	            	if (showGrid)
	            		gc.drawLine(0, i*gridWidth, CANVAS_WIDTH, i*gridWidth);
	            	if (i % 8 == 0)
		            	gc.drawLine(0, i*gridWidth+1, CANVAS_WIDTH, i*gridWidth+1);

	            }
	            
	            // highlight current selected tile
	            int currentSpriteX = (currentSpriteIndex % 16) * gridWidth * 8;
	            int currentSpriteY = (currentSpriteIndex / 16) * gridWidth * 8;
    			gc.setForeground(new Color(0,200,200));
    			gc.drawRectangle(currentSpriteX, currentSpriteY, gridWidth*8, gridWidth*8);
	            
			}
		});
		
		compositeMain.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.x < CANVAS_WIDTH && e.y < CANVAS_WIDTH) {
					if (currentMode == EDIT_MODE) {
						mouseX = e.x;
						currentXIndex = mouseX / gridWidth;
						mouseY = e.y;
						currentYIndex = mouseY / gridWidth;
						int spriteIndex = currentXIndex / 8 + (currentYIndex / 8)*16;
						displayFormattedData.get(currentPatternTableIndex)[spriteIndex][currentYIndex%8][currentXIndex%8] = currentPaletteIndex;
						compositeMain.redraw();
						scrolledComposite.redraw();
					}
				}
				if (e.x < CANVAS_WIDTH && e.y < CANVAS_WIDTH) {
					if (currentMode == SELECT_MODE) {
						currentSpriteIndex = (e.x / gridWidth) / 8 + ((e.y / gridWidth) / 8)*16;
						compositeMain.redraw();
						scrolledComposite.redraw();
						lblTileNumber.setText("Tile Number: " + currentSpriteIndex);
					}
				}
				mouseDown = true;
			}

			@Override
			public void mouseUp(MouseEvent e) {
				mouseDown = false;
			}
			
		});
		compositeMain.addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if (mouseDown) {
					if (e.x < CANVAS_WIDTH && e.y < CANVAS_WIDTH) {
						if (currentMode == EDIT_MODE) {
							mouseX = e.x;
							currentXIndex = mouseX / gridWidth;
							mouseY = e.y;
							currentYIndex = mouseY / gridWidth;
							int spriteIndex = currentXIndex / 8 + (currentYIndex / 8)*16;
							displayFormattedData.get(currentPatternTableIndex)[spriteIndex][currentYIndex%8][currentXIndex%8] = currentPaletteIndex;
							compositeMain.redraw();
							scrolledComposite.redraw();
						}
					}
				}
			}
		});
	}
	
	private int[][] convertNesFormatToDisplayFormat(byte[] nesFormattedChar, int offset) {
		int[][] displayFormattedChar = new int[8][8];
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				displayFormattedChar[i][7-j] = ((nesFormattedChar[i + offset*16]   & (int)Math.pow(2, j)) >> j) + ((nesFormattedChar[(i+offset*16)+8] & (int)Math.pow(2, j)) >> j)*2;
			}
		}
		return displayFormattedChar;
	}
	
	private void convertDisplayFormatToNesFormat(int index) {
		int[][][] displayFormattedChar = displayFormattedData.get(index);
		int[][] array1 = new int[8][8];
		int[][] array2 = new int[8][8];
		for (int n=0; n<256; n++) {
			array1 = new int[8][8];
			array2 = new int[8][8];
			for (int i=0; i<8; i++) {
				for (int j=0; j<8; j++) {
					if (displayFormattedChar[n][i][j] == 0) {
						array1[i][j] = 0;
						array2[i][j] = 0;
					}
					else if (displayFormattedChar[n][i][j] == 1) {
						array1[i][j] = 1;
						array2[i][j] = 0;
					}
					else if (displayFormattedChar[n][i][j] == 2) {
						array1[i][j] = 0;
						array2[i][j] = 1;
					}
					else if (displayFormattedChar[n][i][j] == 3) {
						array1[i][j] = 1;
						array2[i][j] = 1;
					}
				}
			}
			byte[] byteArray = intArrayToByteArray(array1, array2);
			for (int i=0; i<16; i++) {
				nesFormattedPatternData.get(index)[n*16 + i] = byteArray[i];
			}
		}
	}
	
	private byte[] intArrayToByteArray(int[][] array1, int[][] array2) {
		byte[] byteArray = new byte[16];
		for (int i=0; i<8; i++) {
			byte val1 = 0x00;
			val1 |= array1[i][7] * 0b00000001;
			val1 |= array1[i][6] * 0b00000010;
			val1 |= array1[i][5] * 0b00000100;
			val1 |= array1[i][4] * 0b00001000;
			val1 |= array1[i][3] * 0b00010000;
			val1 |= array1[i][2] * 0b00100000;
			val1 |= array1[i][1] * 0b01000000;
			val1 |= array1[i][0] * 0b10000000;
			byteArray[i] = val1;
			byte val2 = 0x00;
			val2 |= array2[i][7] * 0b00000001;
			val2 |= array2[i][6] * 0b00000010;
			val2 |= array2[i][5] * 0b00000100;
			val2 |= array2[i][4] * 0b00001000;
			val2 |= array2[i][3] * 0b00010000;
			val2 |= array2[i][2] * 0b00100000;
			val2 |= array2[i][1] * 0b01000000;
			val2 |= array2[i][0] * 0b10000000;
			byteArray[i+8] = val2;
		}
		return byteArray;
	}
	
	private void printFileBytes(int index) {
		// print fileBytes array
				int i=0;
				while (i<nesFormattedPatternData.get(index).length) {
					System.out.printf("%02X", nesFormattedPatternData.get(index)[i]);
					i++;
					if (i % 4 == 0 && i != 0)
						System.out.print(" ");
					if (i % 16 == 0 && i != 0)
						System.out.println();
				}
	}
	
	private void saveFile(String fileName) throws IOException {
//		FileDialog outputFileDialog = new FileDialog(parentShell, SWT.SAVE);
//		outputFileDialog.setFileName(currentCharFileName);
//		String fName = outputFileDialog.open();
		FileOutputStream fStream = new FileOutputStream(new File(fileName));
		fStream.write(nesFormattedPatternData.get(currentPatternTableIndex));
//		currentCharFileName = outputFileDialog.getFileName();
		parentShell.setText("NES Studio - " + fileName);
	}
	
	public void loadFile(String fileName) throws IOException {
		//FileDialog fileOpenDialog = new FileDialog(parentShell);
		//String fName = fileOpenDialog.open();
		File newFile = new File(fileName);    // if file exists, open it
		if (newFile.exists()) {
			FileInputStream fStream = new FileInputStream(newFile);
			//		currentCharFileName = fileOpenDialog.getFileName();
			currentCharFileName = fileName;
//			parentShell.setText("NES Studio - " + currentCharFileName);
			// read file 16 bytes at a time into fileBytes array
			int offset = 0;
			int countRead = 1;
			nesFormattedPatternData.add(new byte[4096]);
			while (countRead > 0 && offset < nesFormattedPatternData.get(currentPatternTableIndex).length) {
				countRead = fStream.read(nesFormattedPatternData.get(currentPatternTableIndex), offset, 16);
				offset += countRead;
			}
			fStream.close();
		}
		else {		// else if file doesn't exist, fill with nesFormattedPatternData with empty data
			byte[] emptyData = new byte[4096];
			for (int i=0; i<4096; i++) {
				emptyData[i] = 0x00;
			}
			nesFormattedPatternData.add(emptyData);
		}
		displayFormattedData.add(new int[256][8][8]);
		for (int n=0; n<256; n++) {
			displayFormattedData.get(currentPatternTableIndex)[n] = convertNesFormatToDisplayFormat(nesFormattedPatternData.get(currentPatternTableIndex), n);
		}
		compositeMain.redraw();
		scrolledComposite.redraw();
	}
	
	public int getCurrentPatternTableIndex() {
		return this.currentPatternTableIndex;
	}
	
	public void setCurrentPatternTableIndex(int index) {
		this.currentPatternTableIndex = index;
		System.out.println("currentPatternTableIndex = " + currentPatternTableIndex);
	}
	
	public Composite getCompositeMain() {
		return this.compositeMain;
	}
	
	public byte[] getNesFormattedPatternData(int index) {
		// first convert int[] sprite data to byte[] nes representaion
		convertDisplayFormatToNesFormat(index);
		return nesFormattedPatternData.get(index);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
