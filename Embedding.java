package BPCS;

import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Embedding {

	public Embedding(Vessel im, Payload p) throws Exception {
		super();
		this.im = im;
		this.p = p;
		int payloadBlocks = p.blockCount() + (int)Math.ceil(p.blockCount()/64.0) + 1;
		if (im.getEmbeddableBlocksCount() <= payloadBlocks) {
			throw new Exception("Payload block cannot be embedded");
		}
	}

	Vessel im;
	Payload p;

	void encode() throws Exception {
		Plane[] planes = im.getPlanes();
		int position = 0;
		int metaData[][] = getMetaData();
		ArrayList<int[][]> payload = p.blocks;

		List<int[][]> conjugationBlocks = p.getConjugationMapBlocks();
		List<int[][]> finalpayload = new ArrayList<int[][]>();

		finalpayload.add(Payload.conjugateBlock(metaData));
		conjugationBlocks.forEach(b->finalpayload.add(Payload.conjugateBlock(b)));
//		finalpayload.addAll(conjugationBlocks);
		finalpayload.addAll(payload);

		for (int i = 0; i < planes.length; i++) {
			ArrayList<Block> blocks = planes[i].getBlocks();
			ArrayList<Integer> complexRegions = planes[i].getComplexRegions();
			for (int complexBlock : complexRegions) {
				Block b = blocks.get(complexBlock);
				b.embed(finalpayload.get(position++));
				if (position >= finalpayload.size()) {
					outputFile();
					return;
				}
			}
		}

	}

	

	void outputFile() throws IOException {
		int[][] finalImage = im.mergePlanes();
		for (int i = 0; i < finalImage.length; i++)
			for (int j = 0; j < finalImage[0].length; j++) {
				finalImage[i][j] = grayToBinary(finalImage[i][j]);
			}
		BufferedImage img1 = new BufferedImage(im.width, im.height, 1);
		for (int i = 0; i < im.height; i++) {
			for (int j = 0; j < im.width; j++) {
				img1.setRGB(j, i, finalImage[i][j]);
			}
		}
		ImageIO.write(img1, "png", new File("/Users/arjunagarwal/Desktop/output.png"));
		// ImageIO.write(img1, "png", new File("output/output.png"));
	}

	private int[][] getMetaData() throws Exception {
		int block[][] = new int[8][8];
		int length = p.fileBytes.length;
		int type = p.filetype.getValue();
		List<Integer> lengthBits = getBitList(length);
		List<Integer> typeBits = getBitList(type);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 8; j++)
				block[i][j] = lengthBits.get(i * 8 + j);
		}
		for (int i = 0, k = 4; i < 4; i++, k++) {
			for (int j = 4; j < 8; j++)
				block[k][j] = typeBits.get(i * 8 + j);
		}
		return block;
	}

	private List<Integer> getBitList(int value) {
		String s = String.format("%32s", Integer.toBinaryString(value)).replaceAll(" ", "0");
		return Arrays.stream(s.split("")).map(i -> (Integer.parseInt(i))).collect(Collectors.toList());
	}

	static int grayToBinary(int rgb) {
		int red = ~(topbc((rgb >> 16) & 0xff));
		int green = ~(topbc((rgb >> 8) & 0xff));
		int blue = ~(topbc(rgb & 0xff));
		return (red << 16) | (green << 8) | blue;

	}

	static int topbc(int gray) {
		int binary = 0;
		for (; gray > 0; gray = gray >> 1) {
			binary ^= gray;
		}
		binary = ~binary;
		return binary;
	}

}
