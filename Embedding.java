package BPCS;

import java.util.*;
import java.util.stream.Collectors;

public class Embedding {
	public static Map<Integer, List> testArray = new HashMap();
	public List<Integer> arraylist = new ArrayList();
	public Embedding(Image im, Payload p) {
		super();
		this.im = im;
		this.p = p;
	}
	Image im;
	Payload p;
	
	void encode() throws Exception {
		Plane[] planes = im.getPlanes();
		int position = 0;
		ArrayList<int [][]> payload = p.blocks;
		System.out.println(payload.size());
		for(int i = 0;i< planes.length; i++) {
			ArrayList<Block> blocks = planes[i].getBlocks();
			Block metaBlock = blocks.get(0);
			int metaData[][] = getMetaData();
			metaBlock.embed(metaData);
			ArrayList<Integer> complexRegions = planes[i].getComplexRegions();
			for(int complexBlock : complexRegions.subList(1,complexRegions.size())) {
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

	private int[][] getMetaData() throws Exception {
		int block[][] = new int[8][8];
		int length = p.fileBytes.length;
		int type = -1;
		for(Integer k : MimeType.typeCodes.keySet()){
			if(Arrays.asList(MimeType.typeCodes.get(k)).contains(p.fileName.split("\\.")[1]))
				type = k;
			if(type == -1)
				throw new Exception("Cannot extract the payload");
		}
		List<Integer> lengthBits = getBitList(length);
		List<Integer> typeBits = getBitList(type);
		for(int i=0; i<4; i++){
			for(int j=0; j<8; j++)
				block[i][j] = lengthBits.get(i*8+j);
		}
		for(int i=0,k=4; i<4; i++,k++){
			for(int j=4; j<8; j++)
				block[k][j] = typeBits.get(i*8+j);
		}
		return block;
	}

	private List<Integer> getBitList(int value) {
		String s = String.format("%32s", Integer.toBinaryString(value)).replaceAll(" ", "0");
		return  Arrays.stream(s.split("")).map(i->(Integer.parseInt(i))).collect(Collectors.toList());
	}

}
