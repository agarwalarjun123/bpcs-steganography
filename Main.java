package BPCS;
import java.io.File;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Main {
	public static void main(String[] args) {

		Image img = new Image("/home/sharon/Downloads/Chrysanthemum.png");
		try {
			int[][] img2 = img.readPixels();
			Payload p = new Payload("/home/sharon/Downloads/test.jpeg", "");
			Embedding e = new Embedding(img,p);
			e.encode();
			int[][] imgx = img.mergePlanes();
			for(int i = 0;i< img2.length;i++) for(int j=0;j< img2[0].length;j++) {
				imgx[i][j] = grayToBinary(imgx[i][j]);
			}
			BufferedImage img1 = new BufferedImage(img.width, img.height, 1);
	        for(int i = 0; i < img.height; i++) {
	        	for(int j = 0; j < img.width; j++) {
	        		img1.setRGB(j,  i, imgx[i][j]);
	        	}
	        }
			ImageIO.write(img1,"png", new File("output/encoded.png"));

			Extraction ex = new Extraction();
			ex.extract("output/encoded.png");
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
 