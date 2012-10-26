package com;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/* 12 rows of 16 colours */
public class ColorTable {
	private String PALETTE_PATH = "colorPalette.png";
	private File path;
	private BufferedImage colorImage = null;

	public ColorTable(File dir){
		path = new File(dir.getAbsoluteFile() + "\\" + PALETTE_PATH);
		readPalette();
	}
	public ColorTable(String dir){
		path = new File(dir + "\\" + PALETTE_PATH);
		readPalette();
	}
	
	private void readPalette(){
		try {
			colorImage = ImageIO.read(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Color getMiddleColor(int color) {
		int index = color * 32 + 16;
		return getColorFromTable(index);
	}

	public Color getColorFromTable(int index) {
		int columns = 16;
		int col = index % columns;
		int row = index / columns;
		int c = colorImage.getRGB(col, row);
		int red = (c & 0x00ff0000) >> 16;
		int green = (c & 0x0000ff00) >> 8;
		int blue = (c & 0x000000ff);
		return new Color(red, green, blue);

	}

	public Color[] getAllColors() {
		Color[] colors = new Color[8];
		for (int i = 0; i < 8; i++) {
			colors[i] = this.getMiddleColor(i);
		}
		return colors;
	}

}
