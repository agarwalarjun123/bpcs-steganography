package BPCS;

public class Block {
	int[][] block = new int[8][8];
	int r,c;
	Block(int r, int c, int[][] img){
		this.r = r;
		this.c = c;
		for(int i = r; i< r+8; i++) for(int j = c; j< c+8; j++) block[i-r][j-c] = img[i][j];
	}
	int getKValue() {
		int changes = 0;
				
		for(int r = 0; r< 8; r++) for(int c= 0;c < 7;c++) if(block[r][c] != block[r][c+1]) changes++;
		for(int c = 0; c < 8 ; c++) for(int r= 0;r < 7;r++) if(block[r][c] != block[r+1][c]) changes++;
		return changes;
	}
	int getBlockBit(int i, int j) {
		return block[i][j];
	}
	
	void embed(int data[][]) {
		for(int i = 0; i< this.block.length; i++) for(int j = 0; j< this.block[0].length; j++) {
			block[i][j] = data[i][j];
		}
	}
	
}
