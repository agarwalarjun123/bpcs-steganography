package BPCS;

import java.util.ArrayList;

public class Embedding {
	public Embedding(Image im, Payload p) {
		super();
		this.im = im;
		this.p = p;
	}
	Image im;
	Payload p;
	
	void encode() {
		Plane[] planes = im.getPlanes();
		int position = 0;
		ArrayList<int [][]> payload = generatePayload();

		for(Plane p: planes) {
			ArrayList<Block> blocks = p.getBlocks();
			for(int complexBlock : p.getComplexRegions()) {
				if(position >= payload.size()) {
					break;
				}
				Block b = blocks.get(complexBlock);
				b.embed(payload.get(position++));
			}
		}
	}
	ArrayList<int [][]> generatePayload(){
		String s = "You should submit a report (maximum 6-pages, not including references or appendices, with\n"
				+ "additional images, data etc, included as appendices) and accompanying video.You should submit a report (maximum 6-pages, not including references or appendices, with\\n\"\n"
				+ "				+ \"additional images, data etc, included as appendices) and accompanying video.";
		byte[] byteArray = s.getBytes();
		ArrayList<int[][]> blocks = new ArrayList<int[][]>();
		for (int i = 0; i < Math.ceil((byteArray.length/8.0)) ;i++){
			int[][] block = new int[8][];
			for (int j = 0; j < 8 ; j++) {
				block[j] = this.convertToBinaryArray(byteArray[i]);
			}
			blocks.add(block);
		}
		
		return blocks;
	}
	int[][] conjugateblock(int[][] block){
		int flag = 0;
		for(int i =0;i<8;i++)for(int j=0;j<8;j++) {
			block[i][j] = block[i][j] ^ flag;
			flag = (flag + 1) % 2;
		}
		return block;
	}
	int[] convertToBinaryArray(int i) {
		int n = i;
		int counter = 0;
		int[] bits = new int[8];
		while(n != 0) {
			bits[counter++] = n % 2;
			n /= 2;
		}
		return bits;
	}
	
}
