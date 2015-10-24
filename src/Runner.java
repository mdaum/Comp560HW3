import java.util.Scanner;


public class Runner {

	public static void main(String[] args) {
		Scanner keyboard= new Scanner(System.in);
		int k=keyboard.nextInt();
		FeatureComputer spamFeatureComputer = new FeatureComputer(k,"hamtraining.txt");
		spamFeatureComputer.LexiconIT();
	}

}
