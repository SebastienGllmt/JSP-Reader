package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BMPReader {

	public static void main(String[] args) {
		try {
			new BMPReader("test.bmp");
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error in reading file");
			e.printStackTrace();
		}
	}
	
	public BMPReader(String filepath) throws IOException{
		File f = new File(filepath);
		System.out.println(f.getAbsolutePath());
		InputStream in = new FileInputStream(f);
		int c;
		while((c = in.read()) != -1){
			System.out.println(c);
		}
	}

}
