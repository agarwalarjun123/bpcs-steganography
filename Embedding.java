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
		ArrayList<int [][]> payload = p.blocks;
		System.out.println(payload.size());
		for(Plane plane: planes) {
			ArrayList<Block> blocks = plane.getBlocks();
			for(int complexBlock : plane.getComplexRegions()) {
				Block b = blocks.get(complexBlock);
				b.embed(payload.get(position++));
				if (position >= payload.size()) {
					return;
				}
			}
		}
		if(position < payload.size()) {
			System.out.println("Payload could not be completely embedded");
		}
	}
	
}
