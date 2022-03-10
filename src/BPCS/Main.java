package BPCS;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws Exception {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter 1 for embedding and 2 for extraction");
		int option = Integer.parseInt(sc.nextLine());
		if(option == 1) {
			System.out.println("Enter complete path of Vessel Image");
			String vesselPath = sc.nextLine();
			Vessel vessel;
			try {
				vessel = new Vessel(vesselPath);
				System.out.println("Vessel Embeddable block count: " + vessel.getEmbeddableBlocksCount());
				System.out.println("Vessel Capacity: " + vessel.getEmbeddableBlocksCount() * 8 + " bytes");
				System.out.println("Enter complete path of Payload File");
				String payloadPath = sc.nextLine();
				Payload payload = new Payload(payloadPath);
				System.out.println("Payload Size: " + payload.fileSize);
				System.out.println("Payload Blocks Count: " + payload.blockCount());
				System.out.println("Payload Conjugate Blocks Count: " + payload.conjugateBlocksCount());
				System.out.println("Payload Metadata blocks: 1");
				Embedding e = new Embedding(vessel,payload);
				e.encode();
			}
			catch(Exception e) {
				e.printStackTrace();
				sc.close();
				return;
			}
		}
			
		else if(option == 2) {
			System.out.println("Enter complete path of Embedded Image");
			String embedded = sc.nextLine();
			Extraction ex = new Extraction();
			ex.extract(embedded);
		}
		sc.close();
	}
	
}
 