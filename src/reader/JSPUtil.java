package reader;

import graphics.ColorTable;
import graphics.WriteAnimatedGif;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Sebastien
 * TODO
 * Flipping images horizontally/vertically (?)
 * Resize image
 * Add BMPs
 * Export JSP to BMP
 */

public class JSPUtil {

	private JSPReader jsp;
	File colorPath;

	/**
	 * Create a new JSP Util
	 * @param input - The JSP to input
	 * @param ctPath - The path of the colour palette to use
	 */
	public JSPUtil(File input, String ctPath) {
		loadUtil(input, new File(ctPath));
	}
	
	/**
	 * Create a new JSP Util
	 * @param input - The JSP to input
	 * @param ctPath - The path of the colour palette to use
	 */
	public JSPUtil(String input, String ctPath) {
		loadUtil(new File(input), new File(ctPath));
	}
	
	/**
	 * Create a new JSP Util
	 * @param input - The JSP to input
	 * @param ctPath - The path of the colour palette to use
	 */
	public JSPUtil(File input, File ctPath) {
		loadUtil(input, ctPath);
	}
	
	/**
	 * Create a new JSP Util
	 * @param input - The JSP to input
	 * @param ctPath - The path of the colour palette to use
	 */
	public JSPUtil(String input, File ctPath) {
		loadUtil(new File(input), ctPath);
	}
	
	/**
	 * Sets up a JSP Util
	 * @param input - The JSP to input
	 * @param ctPath - The path of the colour palette to use
	 */
	private void loadUtil(File input, File ctPath){
		jsp = new JSPReader();
		colorPath = ctPath;
		try {
			jsp.readJSP(input);
		} catch (IOException e) {
			System.out.println("Reading error on " + input.getAbsolutePath());
			e.printStackTrace();
		}
	}

	/**			Public methods			**/
	
	/**
	 * Writes the edited JSP to a given file
	 * @param output - the file to output to
	 */
	public void writeJSP(String output) {
		writeJSP(new File(output));
	}
	
	/**
	 * Writes the edited JSP to a given file
	 * @param output - the file to output to
	 */
	public void writeJSP(File output) {
		try {
			jsp.writeJSP(output);
		} catch (IOException e) {
			System.out.println("Writing error on " + output);
		}
	}

	/**
	 * Creates a BufferedImage out of a JSP
	 * @param picID - The index of the image to convert
	 * @return
	 */
	public BufferedImage createImage(int picID){
		return createImage(jsp.getWidth(picID), jsp.getHeight(picID), picID);
	}
	
	/**
	 * Creates a BufferedImages out of a JSP
	 * @param maxWidth - The max width of the image
	 * @param maxHeight - The max height of the image
	 * @param picID - The index of the image to convert
	 * @return the BufferedImage of the image
	 */
	public BufferedImage createImage(int maxWidth, int maxHeight, int picID) {
		int width = jsp.getWidth(picID);
		BufferedImage bi = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
		g2d.fillRect(0, 0, maxWidth, maxHeight);

		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		ColorTable ct = new ColorTable(colorPath);		
		int color = -1;
		while ((c = scan.byteStream()) != -1) {
			int pos = scan.getRealPosition();
			int x = pos % width;
			int y = pos / width;
			
			if(color != c){
				color = c;
				g2d.setColor(ct.getColorFromTable(c));
			}
			g2d.fillRect(x, y, 1, 1);
		}
		
		return bi;
	}
	
	/**
	 * Prints a JSP frame-by-frame
	 * @param dir - the directory to print to
	 */
	public void printAll(String dir){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			printImage(dir, i);
		}
	}
	
	/**
	 * Prints an image as a PNG
	 * @param dir - the directory to print to
	 * @param picID - the index of the image to print
	 */
	public void printImage(String dir, int picID) {
		BufferedImage bi = createImage(picID);
		String outputFile = dir + picID + ".png";
		File file = new File(outputFile);
		try {
			ImageIO.write(bi, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shortcut to print an image as a PNG if the BufferedImage was already calculated
	 * @param bi - the BufferedImage to print
	 * @param dir - the directory to print to
	 * @param picID - the index of the image to print
	 */
	public void printImage(BufferedImage bi, String dir, int picID) {
		String outputFile = dir + picID + ".png";
		File file = new File(outputFile);
		try {
			ImageIO.write(bi, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a gif to a given directory
	 * @param gifDir - The directory
	 */
	public void writeGif(String gifDir){
		writeGif(gifDir, null, false);
	}
	
	/**
	 * Writes a gif and corresponding images to a directory
	 * @param gifDir - Directory for gif
	 * @param imgDir - Directory for png
	 */
	public void writeGif(String gifDir, String imgDir){
		writeGif(gifDir, imgDir, true);
	}
	
	/**
	 * Writes a gif and corresponding images to a root directory + extensions
	 * @param dir - The root directory
	 * @param gifName - Extension for gif
	 * @param imgName - Extension for png
	 * @param PNG - Whether or not to print png
	 */
	public void writeGif(String dir, String gifName, String imgName, boolean PNG){
		writeGif(dir + gifName, dir + imgName, PNG);
	}
	
	/**
	 * Writes a gif and corresponding images to a root directory + extensions
	 * @param dir - The root directory
	 * @param gifName - Extension for gif
	 * @param imgName - Extension for png
	 */
	public void writeGif(String dir, String gifName, String imgName){
		writeGif(dir + gifName, dir + imgName, true);
	}
	
	/**
	 * Writes an animated GIF of a JSP
	 * @param dir - the directory to print to
	 * @param PNG - whether or not PNG versions should be printed at the same time
	 */
	public void writeGif(String gifDir, String imgDir, boolean PNG) {
		try {
			int imageCount = jsp.getImageCount();
			BufferedImage[] frames = new BufferedImage[imageCount];
			String[] delayTimes = new String[imageCount];

			
			int maxWidth = 0;
			int maxHeight = 0;
			for(int i=0; i<imageCount; i++){
				int width = jsp.getWidth(i);
				int height = jsp.getHeight(i);
				if(width > maxWidth){
					maxWidth = width;
				}
				if(height > maxHeight){
					maxHeight = height;
				}
			}
			for (int i = 0; i < imageCount; i++) {
				BufferedImage bi = createImage(maxWidth, maxHeight, i);
				frames[i] = bi;
				delayTimes[i] = "12";
				if(PNG){
					printImage(bi, imgDir+".png", i);
				}
			}

			File f = new File(gifDir+".gif");

			WriteAnimatedGif.saveAnimate(f, frames, delayTimes);
		} catch (Exception e) {
			JFrame frame = null;
			JOptionPane.showMessageDialog(frame, "No image was found for this monster.","JSP image error",JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Gets the amount of images in a JSP
	 */
	public int getImageCount(){
		return jsp.getImageCount();
	}

	/*			Get JSP Reader Info			*/
	
	/**
	 * Gets the JSPReader. This is used to append/insert JSPs
	 */
	public JSPReader getReader(){
		return jsp;
	}
	
	public int getSize(int picID){
		return jsp.getSize(picID);
	}
	public int getSize(){
		int size = 0;
		for(int i=0; i<jsp.getImageCount();i++){
			size += jsp.getSize(i);
		}
		return size;
	}
	
	public int getHeight(int picID){
		return jsp.getHeight(picID);
	}
	public int getWidth(int picID){
		return jsp.getWidth(picID);
	}
	public int getXOrigine(int picID){
		return jsp.getXOrigine(picID);
	}
	public int getYOrigine(int picID){
		return jsp.getYOrigine(picID);
	}
	
	/*			Insert images			*/
	
	/**
	 * Inserts a JSP at the end of the current one
	 * @param newJSP - the JSPReader of the JSP to append
	 */
	public void appendAllJSP(JSPReader newJSP){
		int index = jsp.getImageCount();
		insertAllJSP(newJSP, index);
	}
	/**
	 * Inserts a JSP at a given index of the current one
	 * @param newJSP - the new JSP to add
	 * @param index - the position at which it should be added
	 */
	public void insertAllJSP(JSPReader newJSP, int index){
		int imageCount = newJSP.getImageCount();
		for(int i=0; i<imageCount; i++){
			insertFrame(newJSP, i, index+i);
		}
	}
	
	/**
	 * Inserts a single image from one JSP to another
	 * @param newJSP - the JSP to pull the image from
	 * @param fromIndex - the index of the image to pull from
	 * @param toIndex - the index to insert the image at
	 */
	public void insertFrame(JSPReader newJSP, int fromIndex, int toIndex){
		jsp.insertHeader(newJSP.getHeader(fromIndex), toIndex);
		jsp.insertContent(newJSP.getContent(fromIndex), toIndex);
		jsp.setImageCount(jsp.getImageCount()+1);
	}
	
	/**
	 * Adds a single frame from a JSP to the end of another
	 * @param newJSP - the JSP to pull the image from
	 * @param fromIndex - the index of the image to pull from
	 */
	public void appendFrame(JSPReader newJSP, int fromIndex){
		int imageCount = jsp.getImageCount();
		insertFrame(newJSP, fromIndex, imageCount);
	}
	
	/**
	 * Copy one frame of a JSP to another position
	 * @param fromIndex - the index of the image to copy
	 * @param toIndex - the index to copy the image to
	 */
	public void copyImage(int fromIndex, int toIndex){
		insertFrame(jsp, fromIndex, toIndex);
	}
	
	/*			Delete images			*/
	
	/**
	 * Deletes the last JSP in a file
	 */
	public void deleteImage(){
		int index = jsp.getImageCount()-1;
		deleteImage(index);
	}
	/**
	 * Deletes a given image in a JSP
	 * @param picID - the index of the image to be deleted
	 */
	public void deleteImage(int picID){
		jsp.deleteContent(picID);
		jsp.deleteHeader(picID);
		jsp.setImageCount(jsp.getImageCount()-1);
	}

	/*			Re-colour methods			*/
	
	/**
	 * Recolours from one range to another
	 * @param colFrom - the range to check for
	 * @param colTo - the range to change to
	 * @param picID - the index of the image to re-colour
	 */
	public void recolor(int colFrom, int colTo, int picID) {
		allColorsTo(new int[] { colFrom }, new int[] {colTo}, picID);
	}
	public void recolor(int colFrom, int colTo) {
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			allColorsTo(new int[] { colFrom }, new int[] {colTo}, i);
		}
	}

	/**
	 * Recolours an array of ranges to another
	 * @param colFrom - all the colours to re-colour
	 * @param colTo - the colours to change them to
	 * @param picID - the index of the image to re-colour
	 */
	public void recolorAll(int[] colFrom, int[] colTo, int picID) {
		allColorsTo(colFrom, colTo, picID);
	}
	public void recolorAll(int[] colFrom, int[] colTo) {
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			allColorsTo(colFrom, colTo, i);
		}
	}

	/*			Shift methods			*/
	
	/**
	 * Shifts a single colour in range
	 * @param color - the colour to shift
	 * @param direction - the direction and amount of ranges to shift by
	 * @param picID - the index of the image to shift
	 */
	public void shiftSingle(int color, int direction, int picID){
		shiftRange(color, color, direction, picID);
	}
	public void shiftSingle(int color, int direction){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			shiftSingle(color, direction, i);
		}
	}
	
	/**
	 * Shifts a single colour range
	 * @param range - the range to shift
	 * @param direction - the direction and amount of ranges to shift by
	 */
	public void shiftColor(int range, int direction, int picID){
		shiftRange(range*32, (range+1)*32, direction, picID);
	}
	public void shiftColor(int range, int direction){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			shiftRange(range*32, (range+1)*32, direction, i);
		}
	}
	
	/**
	 * Shifts a specified colour range
	 * @param min - the minimum colour to shift
	 * @param max - the maximum colour to shift
	 * @param direction - the direction and amount of ranges to shift by
	 * @param picID - the index of the image to shift
	 */
	public void shiftRange(int min, int max, int direction, int picID){
		shiftBy(min, max, direction, picID);
	}
	/**
	 * Shifts a specified colour range
	 * @param min - the minimum colour to shift
	 * @param max - the maximum colour to shift
	 * @param direction - the direction and amount of ranges to shift by
	 */
	public void shiftRange(int min, int max, int direction){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			shiftRange(min, max, direction, i);
		}
	}
	
	/**
	 * Shifts everything in a JSP
	 * @param direction - the direction and amount of ranges to shift by
	 * @param picID - the index of the image to shift
	 */
	public void shift(int direction, int picID){
		shiftBy(0, 255, direction, picID);
	}
	/**
	 * Shifts everything in a JSP
	 * @param direction - the direction and amount of ranges to shift by
	 */
	public void shift(int direction){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			shiftBy(0, 255, direction, i);
		}
	}

	/*			Paint range methods			*/
	
	/**
	 * Changes one colour to another
	 * @param colFrom - the colour to change
	 * @param colTo - the colour to change it to
	 * @param picID - the index of the image to paint
	 */
	public void paintColor(int colFrom, int colTo, int picID){
		paintRange(colFrom, colFrom, colTo, picID);
	}
	/**
	 * Changes one colour to another
	 * @param colFrom - the colour to change
	 * @param colTo - the colour to change it to
	 */
	public void paintColor(int colFrom, int colTo){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			paintRange(colFrom, colFrom, colTo, i);
		}
	}
	
	/**
	 * Changes colours in a range to a given colour
	 * @param min - the minimum colour to change
	 * @param max - the maximum colour to change
	 * @param colTo - the colour to change them to
	 * @param picID - the index of the picture to paint
	 */
	public void paintRange(int min, int max, int colTo, int picID){
		paintColorRange(min, max, colTo, picID);
	}
	/**
	 * Changes colours in a range to a given colour
	 * @param min - the minimum colour to change
	 * @param max - the maximum colour to change
	 * @param colTo - the colour to change them to
	 */
	public void paintRange(int min, int max, int colTo){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			paintColorRange(min, max, colTo, i);
		}
	}

	/*			Swap colours			*/
	
	/**
	 * Swaps two colours
	 * @param col1 - the first colour to swap
	 * @param col2 - the second colour to swap
	 * @param picID - the index of the image to swap these in
	 */
	public void swapColor(int col1, int col2, int picID){
		swapTwoColors(col1, col2, false, picID);
	}
	/**
	 * Swaps two colours
	 * @param col1 - the first colour to swap
	 * @param col2 - the second colour to swap
	 */
	public void swapColor(int col1, int col2){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			swapColor(col1, col2, i);
		}
	}
	
	/**
	 * Swaps two ranges of colours
	 * @param range1 - the first range to swap
	 * @param range2 - the second range to swap
	 * @param picID - the index of the image to swap these in
	 */
	public void swapRange(int range1, int range2, int picID){
		swapTwoColors(range1, range2, true, picID);
	}
	/**
	 * Swaps two ranges of colours
	 * @param range1 - the first range to swap
	 * @param range2 - the second range to swap
	 */
	public void swapRange(int range1, int range2){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			swapRange(range1, range2, i);
		}
	}
	
	/*			Swap shade			*/
	
	/**
	 * Swaps two shades
	 * @param shade1 - the first shade to swap
	 * @param shade2 - the second shade to swap
	 * @param picID - the index of the image to swap these in
	 */
	public void swapShades(int shade1, int shade2, int picID){
		swapTwoShades(shade1, shade2, picID);
	}
	/**
	 * Swaps two shades
	 * @param shade1 - the first shade to swap
	 * @param shade2 - the second shade to swap
	 */
	public void swapShades(int shade1, int shade2){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			swapShades(shade1, shade2, i);
		}
	}
	
	/*			Light shade			*/
	
	/**
	 * Changes the brightness of an image
	 * @param brightness - the brightness to change it by
	 * @param picID - the index of the image to apply the light to
	 */
	public void light(int brightness, int picID){
		brightnessTo(brightness, picID);
	}
	/**
	 * Changes the brightness of an image
	 * @param brightness - the brightness to change it by
	 */
	public void light(int brightness){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			brightnessTo(brightness, i);
		}
	}
	
	/**
	 * Changes the brightness of an image
	 * @param oldShade - the shade to change
	 * @param newShade - the shade to change to
	 * @param isOffset - whether or not this is a new shade or an offset of the old one
	 * @param picID - the index of the image to apply the light to
	 */
	public void lightShade(int oldShade, int newShade, boolean isOffset, int picID){
		lightRange(oldShade, oldShade, newShade, isOffset, picID);
	}
	/**
	 * Changes the brightness of an image
	 * @param oldShade - the shade to change
	 * @param newShade - the shade to change to
	 * @param isOffset - whether or not this is a new shade or an offset of the old one
	 */
	public void lightShade(int oldShade, int newShade, boolean isOffset){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			lightRange(oldShade, oldShade, newShade, isOffset, i);
		}
	}
	
	/**
	 * Changes the shading on a range of shades
	 * @param min - the minimum shade
	 * @param max - the maximum shade
	 * @param newShade - the shade to apply
	 * @param isOffset - whether or not this is a new shade or an offset of the old one
	 * @param picID - the index of the image to apply the shade to
	 */
	public void lightRange(int min, int max, int newShade, boolean isOffset, int picID){
		lightShadeRange(min, max, newShade, isOffset, picID);
	}
	/**
	 * Changes the shading on a range of shades
	 * @param min - the minimum shade
	 * @param max - the maximum shade
	 * @param newShade - the shade to apply
	 * @param isOffset - whether or not this is a new shade or an offset of the old one
	 */
	public void lightRange(int min, int max, int newShade, boolean isOffset){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			lightShadeRange(min, max, newShade, isOffset, i);
		}
	}

	/*			Colour shade methods			*/
	
	/**
	 * Changes the colour of a given shade
	 * @param shade - the shade to paint
	 * @param colTo - the colour to change the shade to
	 * @param picID - the index of the image to paint
	 */
	public void paintShade(int shade, int colTo, int picID){
		colorShade(shade, shade, colTo, picID);
	}
	/**
	 * Changes the colour of a given shade
	 * @param shade - the shade to paint
	 * @param colTo - the colour to change the shade to
	 */
	public void paintShade(int shade, int colTo){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			colorShade(shade, shade, colTo, i);
		}
		
	}
	
	/**
	 * Change the colour of a range of shades
	 * @param min - the minimum shade
	 * @param max - the maximum shade
	 * @param colTo - the colour to change the shade to
	 * @param picID - the index of the image to paint
	 */
	public void colorShade(int min, int max, int colTo, int picID){
		changeShadeColor(min, max, colTo, picID);
	}
	/**
	 * Change the colour of a range of shades
	 * @param min - the minimum shade
	 * @param max - the maximum shade
	 * @param colTo - the colour to change the shade to
	 */
	public void colorShade(int min, int max, int colTo){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			changeShadeColor(min, max, colTo, i);
		}
	}

	/*			Shade colour methods			*/
	
	/**
	 * Changes the shade of a colour
	 * @param colour - the colour to light
	 * @param brightness - the shade to apply
	 * @param picID - the index of the image to light
	 */
	public void lightColor(int color, int brightness, int picID){
		lightColorBounds(color, color, brightness, picID);
	}
	/**
	 * Changes the shade of a colour
	 * @param colour - the colour to light
	 * @param brightness - the shade to apply
	 */
	public void lightColor(int color, int brightness){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			lightColorBounds(color, color, brightness, i);
		}
	}
	
	/**
	 * Changes the shade of a range of colours
	 * @param range - the range to light
	 * @param brightness - the shade to apply
	 * @param picID - the index of the image to light
	 */
	public void lightColorRange(int range, int brightness, int picID){
		lightColorBounds(range*32, (range+1)*32, brightness, picID);
	}
	/**
	 * Changes the shade of a range of colours
	 * @param range - the range to light
	 * @param brightness - the shade to apply
	 */
	public void lightColorRange(int range, int brightness){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			lightColorBounds(range*32, (range+1)*32, brightness, i);
		}
	}
	
	/**
	 * Changes the shade of a range of colours
	 * @param min - the minimum colour to light
	 * @param max - the max colour to light
	 * @param brightness - the shade to apply
	 * @param picID - the index of the image to light
	 */
	public void lightColorBounds(int min, int max, int brightness, int picID){
		changeColorShade(min, max, brightness, picID);
	}
	/**
	 * Changes the shade of a range of colours
	 * @param min - the minimum colour to light
	 * @param max - the max colour to light
	 * @param brightness - the shade to apply
	 */
	public void lightColorBounds(int min, int max, int brightness){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			changeColorShade(min, max, brightness, i);
		}
	}
	
	/*			Hide methods			*/
	
	/**
	 * hides a range of colours
	 * @param range - the range to hide
	 * @picID - the index of the image to hide
	 */
	public void hideColorRange(int range, int picID){
		renderTransparent(range*32, (range+1)*32, false, picID);
	}
	/**
	 * hides a range of colours
	 * @param range - the range to hide
	 */
	public void hideColorRange(int range){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			hideColorRange(range, i);
		}
	}
	
	/**
	 * Hides colours by bounds
	 * @param min - the minimum colour to hide
	 * @param max - the max colour to hide
	 * @param picID - the index of the image to hide
	 */
	public void hideColor(int min, int max, int picID){
		renderTransparent(min, max, false, picID);
	}
	/**
	 * Hides colours by bounds
	 * @param min - the minimum colour to hide
	 * @param max - the max colour to hide
	 */
	public void hideColor(int min, int max){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			hideColor(min, max, i);
		}
	}
	
	/**
	 * Hides colours by shade
	 * @param min - the minimum shade
	 * @param max - the max shade
	 * @param picID - the index of the image to hide
	 */
	public void hideShade(int min, int max, int picID){
		renderTransparent(min, max, true, picID);
	}
	/**
	 * Hides colours by shade
	 * @param min - the minimum shade
	 * @param max - the max shade
	 */
	public void hideShade(int min, int max){
		int imageCount = jsp.getImageCount();
		for(int i=0; i<imageCount; i++){
			hideShade(min, max, i);
		}
	}
	
	/*			Invert colour methods			**/
	
	/**
	 * Inverts all colours in an image
	 * @param picID - The index of the image to invert
	 */
	public void invert(int picID){
		invertColorRange(0, 255, picID);
	}
	
	/**
	 * Inverts all colours
	 */
	public void invert(){
		for(int i=0; i<jsp.getImageCount(); i++){
			invert(i);
		}
	}
	
	/**
	 * Inverts colours in a certain range in an image
	 * @param min - The minimum colour
	 * @param max - The maximum colour
	 * @param picID - The index of the image to invert
	 */
	public void invertColors(int min, int max, int picID){
		invertColorRange(min, max, picID);
	}
	/**
	 * Inverts colours in a certain range
	 * @param min - The minimum colour
	 * @param max - The maximum colour
	 */
	public void invertColors(int min, int max){
		for(int i=0; i<jsp.getImageCount(); i++){
			invertColors(min, max, i);
		}
	}
	
	/**
	 * Inverts a colour range in an image
	 * @param range - The range to invert
	 * @param picID - The index of the image to invert
	 */
	public void invertRange(int range, int picID){
		invertColorRange((range*32), (range+1)*32, picID);
	}
	/**
	 * Inverts a colour range
	 * @param range - The range to invert
	 */
	public void invertRange(int range){
		for(int i=0; i<jsp.getImageCount(); i++){
			invertRange(range, i);
		}
	}
	/**
	 * Inverts a range of shades in an image
	 * @param min - The minimum shade
	 * @param max - The maximum shade
	 * @param picID - The index of the image to invert
	 */
	public void invertShade(int min, int max, int picID){
		invertShadeRange(min, max, picID);
	}
	
	/**
	 * Inverts a range of shades
	 * @param min - The minimum shade
	 * @param max - The maximum shade
	 */
	public void invertShade(int min, int max){
		for(int i=0; i<jsp.getImageCount(); i++){
			invertShade(min, max, i);
		}
	}
	
	/**
	 * Inverts all shades in an image
	 * @param picID - The index of the image to invert
	 */
	public void invertAllShades(int picID){
		invertShadeRange(0, 31, picID);
	}
	/**
	 * Inverts all shades
	 */
	public void invertAllShades(){
		for(int i=0; i<jsp.getImageCount(); i++){
			invertAllShades(i);
		}
	}
	
	/*			Random Colour methods			**/
	
	/**
	 * Makes the image have random shades
	 * @param picID - The index of the image
	 */
	public void randomShade(int picID){
		turnsShadeRandom(0, 31, 0, 31, picID);
	}
	/**
	 * Makes the JSP have random shades
	 */
	public void randomShade(){
		for(int i=0; i<jsp.getImageCount(); i++){
			randomShade(i);
		}
	}
	/**
	 * Makes the range of shades have a random shade
	 * @param minShadeFrom - Min shade to convert
	 * @param maxShadeFrom - Max shade to convert
	 * @param minShadeTo - Min shade random
	 * @param maxShadeTo - Max shade random
	 * @param picID - The index of the image
	 */
	public void randomShade(int minShadeFrom, int maxShadeFrom, int minShadeTo, int maxShadeTo, int picID){
		turnsShadeRandom(minShadeFrom, maxShadeFrom, minShadeTo, maxShadeTo, picID);
	}
	/**
	 * Makes the range of shades have a random shade
	 * @param minShadeFrom - Min shade to convert
	 * @param maxShadeFrom - Max shade to convert
	 * @param minShadeTo - Min shade random
	 * @param maxShadeTo - Max shade random
	 */
	public void randomShade(int minShadeFrom, int maxShadeFrom, int minShadeTo, int maxShadeTo){
		for(int i=0; i<jsp.getImageCount(); i++){
			turnsShadeRandom(minShadeFrom, maxShadeFrom, minShadeTo, maxShadeTo, i);
		}
	}
	
	/**
	 * Gives the image a random range
	 * @param picID
	 */
	public void randomRange(int picID){
		turnsRangeRandom(0, 7, 0, 7, picID);
	}
	/**
	 * Gives the JSP a random range
	 */
	public void randomRange(){
		for(int i=0; i<jsp.getImageCount(); i++){
			randomRange(i);
		}
	}
	/**
	 * Makes a range of colours in an image turn into a random range
	 * @param minRangeFrom - Min range to convert
	 * @param maxRangeFrom - Max range to convert
	 * @param minRangeTo - Min range random
	 * @param maxRangeTo - Max range random
	 * @param picID - The index of the image
	 */
	public void randomRange(int minRangeFrom, int maxRangeFrom, int minRangeTo, int maxRangeTo, int picID){
		turnsRangeRandom(minRangeFrom, maxRangeFrom, minRangeTo, maxRangeTo, picID);
	}
	/**
	 * Makes a range of colours in a JSP turn into a random range
	 * @param minRangeFrom - Min range to convert
	 * @param maxRangeFrom - Max range to convert
	 * @param minRangeTo - Min range random
	 * @param maxRangeTo - Max range random
	 */
	public void randomRange(int minRangeFrom, int maxRangeFrom, int minRangeTo, int maxRangeTo){
		for(int i=0; i<jsp.getImageCount(); i++){
			randomRange(minRangeFrom, maxRangeFrom, minRangeTo, maxRangeTo, i);
		}
	}
	
	/**
	 * Gives an image random colours
	 * @param picID - The index of the image
	 */
	public void randomColors(int picID){
		turnsColorsRandom(0, 255, 0, 255, picID);
	}
	/**
	 * Makes a JSP have random colours
	 */
	public void randomColors(){
		for(int i=0; i<jsp.getImageCount(); i++){
			randomColors(i);
		}
	}
	/**
	 * Makes an image convert a colour range to random colours
	 * @param minColFrom - Min colour to convert
	 * @param maxColFrom - Max colour to convert
	 * @param minColTo - Min colour random
	 * @param maxColTo - Max colour random
	 * @param picID - The index of the image
	 */
	public void randomColors(int minColFrom, int maxColFrom, int minColTo, int maxColTo, int picID){
		turnsColorsRandom(minColFrom, maxColFrom, minColTo, maxColTo, picID);
	}
	/**
	 * Makes a JSP convert a colour range to random colours
	 * @param minColFrom - Min colour to convert
	 * @param maxColFrom - Max colour to convert
	 * @param minColTo - Min colour random
	 * @param maxColTo - Max colour random
	 */
	public void randomColors(int minColFrom, int maxColFrom, int minColTo, int maxColTo){
		for(int i=0; i<jsp.getImageCount(); i++){
			turnsColorsRandom(minColFrom, maxColFrom, minColTo, maxColTo, i);
		}
	}

	/**			Private methods			**/
	
	/**
	 * Renders transparent colours/shades by bounds
	 * @param minCol - the minimum colour/shade
	 * @param maxCol - the maximum colour/shade
	 * @param isShade - whether the arguments are shades or not
	 * @param picID - the index of the image to render transparent
	 */
	private void renderTransparent(int minCol, int maxCol, boolean isShade, int picID){
		int[] contentArray = jsp.getContent(picID);
		List<Integer> imageContent = new ArrayList<Integer>();
		for(int i : contentArray){
			imageContent.add(i);
		}
		jsp.getContent(picID);
		int offset=0;
		JSPScanner scan = new JSPScanner(contentArray);
		while (scan.hasNext()) {
			if(!scan.hasNextByte()){
				scan.skipHead();
			}
			int length = scan.getLength();
			int headPosition = scan.getIndex() + offset;
			int count=0;
			int painted=0;
			while(scan.hasNextByte()){
				count++;
				int value = scan.nextByte();
				if(isShade){ //if shade instead of colour
					value %= 32;
				}
				if(value >= minCol && value <= maxCol){
					if(painted == 0){
						imageContent.remove(headPosition);
						offset--;
					}else{
						imageContent.set(headPosition, painted);
						painted=0;
					}
					imageContent.remove(scan.getIndex()+offset); //removes the colour
					imageContent.add(scan.getIndex()+offset, 129); //adds 1 blank pixel
					if(length - count > 0){
						offset++;
						imageContent.add(scan.getIndex()+offset, (length - count));
						headPosition = scan.getIndex() + offset;
					}
				}else{
					painted++;
				}
			}
		}
		jsp.setSize(imageContent.size(), picID);
		
		contentArray = new int[imageContent.size()];
		int contentSize = imageContent.size();
		for(int i=0; i<contentSize; i++){
			contentArray[i] = imageContent.get(i);
		}
		jsp.setContent(contentArray, picID);
	}
	
	/**
	 * Changes the shade of a colour by bounds
	 * @param minCol - the minimum colour
	 * @param maxCol - the maximum colour
	 * @param brightness - the brightness to apply
	 * @param picID - the index of the image to change the colour's shade
	 */
	private void changeColorShade(int minCol, int maxCol, int brightness, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			if(c >= minCol && c <= maxCol){
				imageContent[scan.getIndex()] = applyLight(c, brightness);
			}
		}
		jsp.setContent(imageContent, picID);
	}

	/**
	 * Changes the colour of a shade
	 * @param minBright - minimum brightness
	 * @param maxBright - max brightness
	 * @param colTo - the range to change to
	 * @param picID - the index of the image to colour the shade of
	 */
	private void changeShadeColor(int minBright, int maxBright, int colTo, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			int bright = c%32;
			if(bright >= minBright && bright <= maxBright){
				imageContent[scan.getIndex()] = transposeRange(c, colTo);
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Changes the light of a given shade range
	 * @param minBright - the minimum brightness
	 * @param maxBright - the maximum brightness
	 * @param newBright - the brightness to apply
	 * @param isOffset - whether or not the brightness to apply is an offset or a value
	 * @param picID - the index of the image to light the shade of
	 */
	private void lightShadeRange(int minBright, int maxBright, int newBright, boolean isOffset, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			int bright = c%32;
			if(bright >= minBright && bright <= maxBright){
				if(!isOffset)
				{
					c -= bright;
				}
				imageContent[scan.getIndex()] = applyLight(c, newBright);
			}

		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Changes the colour of a set of colours
	 * @param minCol - the minimum colour
	 * @param maxCol - the maximum colour
	 * @param colTo - the colour to change to
	 * @param picID - the index of the image to paint the colour of
	 */
	private void paintColorRange(int minCol, int maxCol, int colTo, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			if(c >= minCol && c <= maxCol){
				imageContent[scan.getIndex()] = colTo;
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Inverts a colour between a certain range
	 * @param min - The minimum colour
	 * @param max - The maximum colour
	 * @param picID - The index of the image to invert colours on
	 */
	private void invertColorRange(int min, int max, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			if(c >= min && c <= max){
				imageContent[scan.getIndex()] = 255-c;
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Inverts a shade between a certain range
	 * @param min - The minimum shade
	 * @param max - The maximum shade
	 * @param picID - The index of the image to invert shades on
	 */
	private void invertShadeRange(int min, int max, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			int shade = c%32;
			if(shade >= min && shade <= max){
				c -= shade;
				imageContent[scan.getIndex()] = applyLight(c, 31-shade);
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Swaps two colours
	 * @param col1 - the first colour
	 * @param col2 - the second colour
	 * @param isRange - whether or not these colours are ranges or fixed values
	 * @param picID - the index of the image to swap colours on
	 */
	private void swapTwoColors(int col1, int col2, boolean isRange, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			if(c == col1){
				if(isRange){
					imageContent[scan.getIndex()] = transposeRange(c, (col2/32));
				}else{
					imageContent[scan.getIndex()] = col2;
				}
			}else if(c == col2){
				if(isRange){
					imageContent[scan.getIndex()] = transposeRange(c, (col1/32));
				}else{
					imageContent[scan.getIndex()] = col1;
				}
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Swaps two shades
	 * @param shadeOne - the first shade
	 * @param shadeTwo - the second shade
	 * @param picID - the index of the image to swap shades on
	 */
	private void swapTwoShades(int shadeOne, int shadeTwo, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			int shade = c%32;
			if(shade == shadeOne){
				imageContent[scan.getIndex()] = c - shade + shadeTwo;
			}else if(shade == shadeTwo){
				imageContent[scan.getIndex()] = c - shade + shadeOne;
			}
		}
		jsp.setContent(imageContent, picID);
	}

	/**
	 * Swaps two images
	 * @param pic1 - the first image index
	 * @param pic2 - the second image index
	 */
	public void swapIndex(int pic1, int pic2){
		jsp.swapContent(pic1, pic2);
		jsp.swapHeader(pic1, pic2);
	}

	/**
	 * Shifts colours by a given range
	 * @param min - the minimum colour to shift
	 * @param max - the maximum colour to shift
	 * @param direction - the direction and amount of ranges to shift by
	 * @param picID - the index of the image to shift
	 */
	private void shiftBy(int min, int max, int direction, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			if(c >= min && c <= max){
				imageContent[scan.getIndex()] = shiftOver(c, direction);
			}
		}
		jsp.setContent(imageContent, picID);
	}

	/**
	 * Changes the brightness
	 * @param brightness - the brightness to apply
	 * @param picID - the index of the image to apply brightness to
	 */
	private void brightnessTo(int brightness, int picID){
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1) {
			imageContent[scan.getIndex()] = applyLight(c, brightness);
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**
	 * Changes a list of colours to another
	 * @param colFrom - list of colours to change
	 * @param colTo - list of colours to change to
	 * @param picID - the index of the image to change the colours on
	 */
	private void allColorsTo(int[] colFrom, int[] colTo, int picID) {
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		while ((c = scan.byteStream()) != -1){
			int range = c/32;
			for(int color : colFrom){
				if (range == color) {
					imageContent[scan.getIndex()] = transposeRange(c, color);
					break;
				}
			}
		}
		jsp.setContent(imageContent, picID);
	}

	private void turnsShadeRandom(int minShadeFrom, int maxShadeFrom, int minShadeTo, int maxShadeTo, int picID){
		if(minShadeFrom >= maxShadeFrom || minShadeTo >= maxShadeTo){
			return;
		}
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		Random r = new Random();
		while ((c = scan.byteStream()) != -1){
			int shade = c%32;
			if(shade >= minShadeFrom && shade<= maxShadeFrom){
				int newRange = r.nextInt(maxShadeTo-minShadeTo+1) + minShadeTo;
				imageContent[scan.getIndex()] = applyLight(c, newRange);
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	private void turnsRangeRandom(int minRangeFrom, int maxRangeFrom, int minRangeTo, int maxRangeTo, int picID){
		if(minRangeFrom >= maxRangeFrom || minRangeTo >= maxRangeTo){
			return;
		}
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		Random r = new Random();
		while ((c = scan.byteStream()) != -1){
			int range = c/32;
			if(range >= minRangeFrom && range<= maxRangeFrom){
				int newShade = r.nextInt(maxRangeTo-minRangeTo+1) + minRangeTo;
				imageContent[scan.getIndex()] = newShade;
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	private void turnsColorsRandom(int minColFrom, int maxColFrom, int minColTo, int maxColTo, int picID){
		if(minColFrom >= maxColFrom || minColTo >= maxColTo){
			return;
		}
		int[] imageContent = jsp.getContent(picID);
		JSPScanner scan = new JSPScanner(imageContent);
		int c;
		Random r = new Random();
		while ((c = scan.byteStream()) != -1){
			if(c >= minColFrom && c<= maxColFrom){
				imageContent[scan.getIndex()] = r.nextInt(maxColTo-minColTo+1) + minColTo;
			}
		}
		jsp.setContent(imageContent, picID);
	}
	
	/**			Intermediate methods			**/

	/**
	 * Transposes a value to a given range
	 * @param value - the value to shift
	 * @param newRange - the new range the value will have
	 * @param return - returns the colour
	 */
	private int transposeRange(int value, int newRange) {
		int result = ((value % 32) + (newRange * 32));
		while(result > 255){
			result -= 255;
		}
		while(result < 0){
			result += 255;
		}
		return result;
	}
	
	/**
	 * Applies light to a colour
	 * @param value - the colour to apply light to
	 * @param brightness - the strength of the light to apply
	 * @return - the new value with the light applied
	 */
	private int applyLight(int value, int brightness){
		int shade = value%32;
		value -= shade;
		int sum = shade+brightness;
		if(sum > 31){
			sum = 31;
		}
		if(sum < 0){
			sum = 0;
		}
		value += sum;
		return value;
	}
	
	/**
	 * Shifts a value by a given range
	 * @param value - the value to shift
	 * @param direction - the direction/amount to shift by
	 * @return - the shifted value
	 */
	private int shiftOver(int value, int direction) {
		int range = value/32;
		int shade = value%32;
		range += direction;
		while(range > 7){
			range -= 7;
		}
		while(range < 0){
			range += 7;
		}
		value = (range*32) + shade;
		return value;
	}
}
