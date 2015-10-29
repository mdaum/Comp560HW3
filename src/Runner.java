import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;


public class Runner {
	static int totalwords=0;
	//NOTE: we need to calculate the probability of 
	public static void main(String[] args) {
		//Scanner keyboard= new Scanner(System.in);
		//int k=keyboard.nextInt();
		double k = 15;//13 looks like a high
		double m = 50;//50 looks like a high with k=13. we get 92%, 92% 
		Classifier spamClassifier = new Classifier(k,m,"spamtraining.txt");
		spamClassifier.LexiconIT();
		totalwords+=spamClassifier.totalWords;
		Classifier hamClassifier = new Classifier(k,m,"hamtraining.txt");
		hamClassifier.LexiconIT();
		totalwords+=hamClassifier.totalWords;
		spamClassifier.filterHashMap();
		hamClassifier.filterHashMap();
		HashMap<String,String> screwYouJava = new HashMap<String,String>();
		for(String key: spamClassifier.refinedHashbrown.keySet()){
			screwYouJava.put(key, "PLEASE GO CHOKE ON A NICE GOBLET OF RUSTY NAILS");
		}
		for(String key: hamClassifier.refinedHashbrown.keySet()){
			screwYouJava.put(key, "DON'T FORGET TO WASH IT DOWN WITH A SOMBER JUG OF WINDEX AFTERWARD");
		}
		int uniqueWords=screwYouJava.keySet().size();
		spamClassifier.computeProbabilities(uniqueWords);
		hamClassifier.computeProbabilities(uniqueWords);
		try{
			System.out.println("SPAM RESULTS");
			printTestResults(spamClassifier,hamClassifier,"spamtesting.txt");
			System.out.println("NOW FOR HAM");
			printTestResults(spamClassifier,hamClassifier,"hamtesting.txt");
		}
		catch(FileNotFoundException f){
			f.printStackTrace();
		}
	}
	//ACHTUNG: PROBABILITIES ARE NOT LOGS. YOU JUST GET A BUNCH OF NEGATIVE VALUES FROM LOGS
	//TAKE THE LEAST NEGATIVE VALUE
	public static void printTestResults(Classifier spamClassifier, Classifier hamClassifier, String testingDocFilePath) throws FileNotFoundException{
		Scanner reader = new Scanner(new File(testingDocFilePath));
		int i=0;
		while(reader.hasNext()){
			String nextTestingDoc = reader.next();
			double probSpam = spamClassifier.probabilityOfClass(nextTestingDoc,totalwords);
			double probHam = hamClassifier.probabilityOfClass(nextTestingDoc,totalwords);
			//System.out.println(nextTestingDoc+": "+(probSpam>probHam?"SPAAAAAM":"haaaaam"));
			//only use this if we're expecting spam. In other words, only do this if we are using the spam testing set
			i=probSpam>probHam?i+1:i;
		}	
		System.out.println("percent of contents classified as spam: "+i);
		System.out.println("percent of contents classified as ham: "+(100-i));
	}
}
