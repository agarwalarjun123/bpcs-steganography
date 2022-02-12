package BPCS;
import java.io.File;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Main {
	public static void main(String args[]) {
		
		Image img = new Image("/Users/arjunagarwal/Downloads/Chrysanthemum.jpeg");
		try {
			int[][] img2 = img.readPixels();
			Payload p = new Payload();
			Embedding e = new Embedding(img,p);
			e.encode();
			int[][] imgx = img.mergePlanes();
			for(int i = 0;i< imgx.length;i++) for(int j=0;j< imgx[0].length;j++) {
				imgx[i][j] = grayToBinary(imgx[i][j]);
			}
			BufferedImage img1 = new BufferedImage(img.width, img.height, 5);

	        for(int i = 0; i < img.height; i++) {
	        	for(int j = 0; j < img.width; j++) {
	        		img1.setRGB(img.width -1 - j, img.height -1 - i, imgx[i][j]);
	        	}
	        }
	        for(int i =0; i< img.height; i++) {
	        	for (int j = 0; j< img.width; j++) {
	        		if(img2[i][j] != imgx[i][j]) {
	        			System.out.println("i -" + i + " j -" + j);
	        		}
	        	}
	        }

	        ImageIO.write(img1, "jpeg", new File("/Users/arjunagarwal/Desktop/Chrysanthemum11211212.jpeg"));
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	static int grayToBinary(int gray) {
		int binary=0;
		for(;gray > 0;gray=gray>>1){
		    binary^=gray;
		}
		binary = ~ binary;
		return binary;
	}
}
 