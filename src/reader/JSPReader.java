package reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSPReader {

	private int imageCountNum=0;
	private int[] imageCountBytes = new int[2];
	private List<int[]> imageHeader = new ArrayList<int[]>();
	private List<int[]> imageContent = new ArrayList<int[]>();
	
	public void readJSP(File f) throws IOException{
		InputStream in = new FileInputStream(f);
		
		for(int i=0; i<2; i++){
			imageCountBytes[i] = in.read();
		}
		imageCountNum = getImageCount();
		
		for(int i=0; i<imageCountNum; i++){
			int[] imageHeaderArray = new int[16];
			for(int j=0; j<16; j++){
				imageHeaderArray[j] = in.read();
			}
			imageHeader.add(imageHeaderArray);
		}
		
		for(int i=0; i<imageCountNum; i++){
			int[] imageArray = new int[getSize(i)];
			for(int j=0; j<getSize(i); j++){
				imageArray[j] = in.read();
			}
			imageContent.add(imageArray);
		}
	}
	
	public void writeJSP(File f) throws IOException{
		OutputStream out = new FileOutputStream(f);
		for(int i : imageCountBytes){
			out.write(i);
		}

		for(int[] i : imageHeader){
			for(int j : i){
				out.write(j);
			}
		}
		
		for(int[] i : imageContent){
			for(int j : i){
				out.write(j);
			}
		}
	}
	
	public int[] getHeader(int picID){
		return imageHeader.get(picID);
	}
	public void setHeader(int[] array, int picID){
		imageHeader.set(picID, array);
	}
	public void swapHeader(int pic1, int pic2){
		Collections.swap(imageHeader, pic1, pic2);
	}
	public void insertHeader(int[] array, int picID){
		imageHeader.add(picID, array);
	}
	public void deleteHeader(int picID){
		imageHeader.remove(picID);
	}
	public int[] getContent(int picID){
		return imageContent.get(picID);
	}
	public void setContent(int[] array, int picID){
		imageContent.set(picID, array);
	}
	public void swapContent(int pic1, int pic2){
		Collections.swap(imageContent, pic1, pic2);
	}
	public void insertContent(int[] array, int picID){
		imageContent.add(picID, array);
	}
	public void deleteContent(int picID){
		imageContent.remove(picID);
	}
	public int getImageCount(){
		return getValue(imageCountBytes, 0, 2);
	}
	public void setImageCount(int value){
		byte[] bytes = shortToBytes(value);
		for(int i=0; i<2; i++){
			imageCountBytes[i] = bytes[i];
		}
	}
	public int getWidth(int picID){
		return getValue(imageHeader.get(picID), 0, 2);
	}
	public void setWidth(int value, int picID){
		setValue(value, picID, 0, 2);
	}
	public int getHeight(int picID){ 
		return getValue(imageHeader.get(picID), 2, 2);
	}
	public void setHeight(int value, int picID){
		setValue(value, picID, 2, 2);
	}
	public int getXOrigine(int picID){
		return getValue(imageHeader.get(picID), 4, 2);
	}
	public void setXOrigine(int value, int picID){
		setValue(value, picID, 4, 2);
	}
	public int getYOrigine(int picID){
		return getValue(imageHeader.get(picID), 6, 2);
	}
	public void setYOrigine(int value, int picID){
		setValue(value, picID, 6, 2);
	}
	public int getSize(int picID){
		return getValue(imageHeader.get(picID), 8, 4);
	}
	public void setSize(int value, int picID){
		setValue(value, picID, 8, 4);
	}
	
	private int getValue(int[] array, int startIndex, int length){
		int value=0;
		for(int i=0, mult=1; i<length; i++, mult*=256){
			value += array[startIndex+i]*mult;
		}
		return value;
	}
	private void setValue(int value, int picID, int startIndex, int length){
		byte[] values = new byte[length];
		if(length == 2){
			values = shortToBytes(value);
		}else if(length ==4){
			values = intToBytes(value);
		}else{
			System.err.println("Invalid length in setValue");
		}
		for(int i=0; i<length; i++){
			imageHeader.get(picID)[startIndex+i] = values[i];
		}
	}
	
	private byte[] shortToBytes(int value){
		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		byte[] bytes = buf.putShort((short)value).array();
		return bytes;
	}
	private byte[] intToBytes(int value) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		byte[] bytes = buf.putInt(value).array();
		return bytes;
	}
}
