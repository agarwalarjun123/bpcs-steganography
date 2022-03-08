package BPCS;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Vessel {
	private String fileName;
	private int[][] pixels;
	int height;
	private int [][] graycode;
	Plane[] planes;
	int width;
	long size;
	int embeddableBlocks = 0;
	MimeType filetype;
	static MimeType[] ALLOWED_MIMETYPES = {MimeType.PNG, MimeType.BMP};
	
	
	
	Vessel(String fileName) throws Exception {
		this.fileName = fileName;
		this.checkFileType();
		this.readPixels();
	}
	void checkFileType() throws Exception {
		String fileType = this.fileName.split("\\.")[1];
		for(MimeType mimetype: ALLOWED_MIMETYPES){
			if(fileType.equals(mimetype.getExtension())) {
				this.filetype = mimetype;
				return;
			}
		}
		throw new Exception("Unsupported Vessel Type Provided.");
	}
	int getEmbeddableBlocksCount() {
		return this.embeddableBlocks;
	}
	int[][] readPixels() throws IOException {
		File file = new File(this.fileName);
		this.size = file.length(); 
		BufferedImage bufferedImage = ImageIO.read(file);
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
				int red = (this.pixels[i][j] >> 16) & 0xff;
				int green = (this.pixels[i][j] >> 8) & 0xff;
				int blue = (this.pixels[i][j]) & 0xff;
				graycode[i][j] = (((red >> 1) ^ red) << 16) | (((green >> 1) ^ green) << 8) | ((blue >> 1) ^ blue);
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
			this.embeddableBlocks += planes[i].complexBlockCount();
		}
		this.planes = planes;
		return planes;
	}
	Plane[] getPlanes() {
		return this.planes;
	}
	int[][] mergePlanes(){
		int[][] finalImage = new int[height][width];
		for (int i = 0;i< height; i++) for (int j = 0;j< width; j++) finalImage[i][j] = pixels[i][j];
		
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
