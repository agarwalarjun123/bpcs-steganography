package BPCS;
import java.io.IOException;
import java.math.BigInteger;

import java.nio.file.*;
import java.util.ArrayList;

public class Payload {
	String fileName;
	byte[] fileBytes;
	ArrayList<int[][]> blocks;
	Payload(String file, String text) throws IOException {
		this.fileName = file;
		this.readFileBytes();
	}
	void readFileBytes() throws IOException {
		byte[] fileContent;
		fileContent = Files.readAllBytes(Paths.get(fileName));
		this.fileBytes = fileContent;
		this.setupPayloadBlocks();
	}
	void setupPayloadBlocks() {
		blocks = new ArrayList<int[][]>();
		for (int i = 0; i < Math.ceil((fileBytes.length/8.0)) ;i++){
			int[][] block = new int[8][];
			int size = i == Math.ceil(fileBytes.length/8.0) - 1 ? fileBytes.length % 8 : 8;
			for (int j = 0; j < size ; j++) {
				block[j] = this.convertToBinary(fileBytes[i* 8 + j]);
			}
			for(int k = size; k < 8;k++) {
				block[k] = new int[] {1,1,1,1,1,1,1,1};
			}
			blocks.add(block);
			
		}
		System.out.println("ddd");
	}
	int[][] conjugateBlock(int[][] block) {
		int i = 0;
		for (int j=0;j<8;j++) for (int k=0;k<8;k++) {
			block[j][k] = block[j][k] ^ i;
			i = (i+1) %2;
		}
		return block;
	}
	int[] convertToBinary(byte i) {
		int n;
		if (i < 0) {
			 n = i & 0xff;
		}
		else n = i;
		BigInteger bg = new BigInteger(String.valueOf(n), 10);
		String value = bg.toString(2);
		int[] bits = new int[] {0,0,0,0,0,0,0,0};
		for (int index = 0; index < value.length() ;index++) {
			bits[index + (8 - value.length())] = ((int) value.charAt(index)) - 48;
		}
		
		return bits;
	}
	
}
