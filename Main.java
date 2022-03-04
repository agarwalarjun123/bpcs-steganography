package BPCS;
import java.util.Scanner;

public class Main {
	public static void main(String[] args)  {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter 1 for embedding and 2 for extraction");
		int option = Integer.parseInt(sc.nextLine());
		if(option == 1) {
			System.out.println("Enter complete path of Vessel Image");
			String vesselPath = sc.nextLine();
			Vessel vessel;
			try {
				vessel = new Vessel(vesselPath);
				System.out.println("Enter complete path of Payload File");
				String payloadPath = sc.nextLine();
				Payload payload = new Payload(payloadPath);
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
//			ex.extract(embedded);
		}
		sc.close();
	}
	
}
 