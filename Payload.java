package BPCS;
import java.io.IOException;
import java.math.BigInteger;

import java.nio.file.*;
import java.util.*;

public class Payload {
	String fileName;
	byte[] fileBytes;
	ArrayList<int[][]> blocks;
	public static final double K_MAX = 112.0; 
	ArrayList<Integer>conjugateMap = new ArrayList<Integer>();
	int fileSize;
	MimeType filetype;
	static MimeType[] ALLOWED_MIMETYPES = {MimeType.TXT, MimeType.PNG, MimeType.JPEG, MimeType.PDF};
	
	Payload(String file) throws Exception {
		this.fileName = file;
		this.checkFileType();
		this.readFileBytes();
	}
	void checkFileType() throws Exception {
		String fileType = this.fileName.split("\\.")[1];
		for(MimeType mimetype: ALLOWED_MIMETYPES){
			if(fileType.equals(mimetype.getExtension())) {
				this.filetype = mimetype;
				return;
			}
		}
		throw new Exception("Unsupported Payload Type Provided.");
	}
	int blockCount() {
		return this.blocks.size();
	}
	void readFileBytes() throws IOException {
		byte[] fileContent;
		fileContent = Files.readAllBytes(Paths.get(fileName));
		this.fileBytes = fileContent;
		this.fileSize = this.fileBytes.length;
		this.setupPayloadBlocks();
	}
	List<int[][]> getConjugationMapBlocks() {
		List<int[][]> blocks = new ArrayList<int[][]>();
		double count = Math.floor(conjugateMap.size() / 64.0);
		for (int i = 0; i < count; i++) {
			int[][] block = new int[8][8];
			for (int j = 0; j < 8; j++)
				for (int k = 0; k < 8; k++)
					block[j][k] = conjugateMap.get((int) i * 64 + 8 * j + k);
			blocks.add(block);
		}
		int remaining = conjugateMap.size() % 64;
		if (remaining > 0) {
			int[][] block = new int[8][8];
			int rows = (int) Math.floor(remaining / 8.0);
			int cols = remaining % 8;
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < 8; j++) {
					block[i][j] = conjugateMap.get((int) count * 64 + 8 * i + j);
				}
			for (int i = 0; i < cols; i++)
				block[rows][i] = conjugateMap.get(64 * (int) count + rows * 8 + i);
			blocks.add(block);
		}
		return blocks;
	}
	void setupPayloadBlocks() {
		blocks = new ArrayList<int[][]>();
		for (int i = 0; i < Math.ceil((fileBytes.length/8.0)) ;i++){
			int[][] block = new int[8][];
			int size = (i == Math.ceil(fileBytes.length/8.0) - 1) && (fileBytes.length%8.0 != 0) ? fileBytes.length % 8 : 8;
			for (int j = 0; j < size ; j++) {
				block[j] = this.convertToBinary(fileBytes[i* 8 + j]);
			}

			for(int k = size; k < 8;k++) {
				block[k] = new int[] {1,1,1,1,1,1,1,1};
			}


			if (this.getKValue(block)/K_MAX <= 0.3) {
				block = this.conjugateBlock(block);
				this.conjugateMap.add(1);
			}
			else {
				this.conjugateMap.add(0);
			}
			blocks.add(block);
		}
	}
	static int[][] conjugateBlock(int[][] block) {
		int i = 0;
		for (int j=0;j<8;j++) for (int k=0;k<8;k++) {
			block[j][k] = block[j][k] ^ i;
			i = (i+1) %2;
		}
		return block;
	}
	int getKValue(int[][] block) {
		int changes = 0;
		for(int r = 0; r< 8; r++) for(int c= 0;c < 7;c++) if(block[r][c] != block[r][c+1]) changes++;
		for(int c = 0; c < 8 ; c++) for(int r= 0;r < 7;r++) if(block[r][c] != block[r+1][c]) changes++;
		return changes;
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
