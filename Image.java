package BPCS;

import java.io.File;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Image {
	private String fileName;
	private int[][] pixels;
	int height;
	private int [][] graycode;
	Plane[] planes;
	int width;
	
	
	Image(String fileName) {
		this.fileName = fileName;
	}
	int[][] readPixels() throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(this.fileName));
		this.height = bufferedImage.getHeight();
		this.width = bufferedImage.getWidth();
		int[][] pixels = new int[this.height][this.width];
		for (int i = 0; i < this.height; i++) {
			for (int j =0; j< this.width; j++)
				pixels[i][j] = bufferedImage.getRGB(j,i);
		}
		this.pixels = pixels;
		this.convertToGrayCode();
		this.setupPlanes();
		return pixels;
	}
	int[][] convertToGrayCode() {
		int[][] graycode = new int[height][width];
		for(int i = 0; i< this.height; i++) {
			for(int j = 0; j < this.width; j++) {
				graycode[i][j] = (this.pixels[i][j] >> 1) ^ this.pixels[i][j];
			}
			
		}
		this.graycode = graycode;
		return graycode;
	}
	int[][] getGrayCode() {
		return this.graycode;
	}
	int getCodeAt(int x,int y,int i) {
		return (this.graycode[x][y] >> (i)) & 0x000001;
	}
	Plane[] setupPlanes() {
		Plane[] planes = new Plane[24]; 
		for(int i = 0; i< 24; i++) {
			planes[i] = new Plane(i,height, width);
			for(int j = 0; j< height; j++) {
				for(int k = 0; k< width; k++) {
					planes[i].setBit(j, k, this.getCodeAt(j, k, i));
				}
			}
			planes[i].setupBlocks();
		}
		this.planes = planes;
		return planes;
	}
	Plane[] getPlanes() {
		return this.planes;
	}
	int[][] mergePlanes(){
		int[][] finalImage = new int[height][width];
		ArrayList<int[][]> mergedPlanes = new ArrayList<int[][]>(); 
		for(int i =0 ;i<24;i++) {
			mergedPlanes.add(this.planes[i].mergeBlocks());
		}
		for(int i = 0;i<height;i++) {
			for(int j = 0;j< width;j++) {
				String bits = "";
				for(int k =0;k < mergedPlanes.size();k++ ) {
					bits = Integer.toString(mergedPlanes.get(k)[i][j]) + bits;
				}
				finalImage[i][j] = Integer.parseInt(bits, 2);
				
			}
		}
		return finalImage;
	}
}
