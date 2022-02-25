package BPCS;
import java.io.File;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class Main {
	public static void main(String args[]) {
		Image img = new Image("/Users/arjunagarwal/Desktop/moon.jpeg");
		try {
			int[][] img2 = img.readPixels();
			Payload p = new Payload("/Users/arjunagarwal/Desktop/test.png","");
			
			Embedding e = new Embedding(img,p);
			e.encode();
			int[][] imgx = img.mergePlanes();
			for(int i = 0;i< img2.length;i++) for(int j=0;j< img2[0].length;j++) {
				imgx[i][j] = grayToBinary(imgx[i][j]);
			}
			BufferedImage img1 = new BufferedImage(img.width, img.height, 5);
	        for(int i = 0; i < img.height; i++) {
	        	for(int j = 0; j < img.width; j++) {
	        		img1.setRGB(j,  i, imgx[i][j]);
	        	}
	        }
	        ImageIO.write(img1,"jpeg", new File("/Users/arjunagarwal/Desktop/moon10.jpeg"));
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	static int grayToBinary(int rgb) {
		int red = ~(topbc((rgb >> 16) & 0xff));
		int green = ~(topbc((rgb >> 8) & 0xff));
		int blue = ~(topbc(rgb & 0xff));
		return (red << 16) | (green << 8) | blue;
		
	}
	static int topbc(int gray) {
		int binary=0;
		for(;gray > 0;gray=gray>>1){
		    binary^=gray;
		}
		binary = ~ binary;
		return binary;
	}
}
 