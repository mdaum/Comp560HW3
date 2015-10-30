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
		double k = 8;//13 looks like a high
		double m = 50;//50 looks like a high with k=13. we get 92%, 92% 
		Classifier spamClassifier = new Classifier(k,m,"spamtraining.txt");
		spamClassifier.LexiconIT();
		totalwords+=spamClassifier.totalWords;
		Classifier hamClassifier = new Classifier(k,m,"hamtraining.txt");
		hamClassifier.LexiconIT();
		totalwords+=hamClassifier.totalWords;
		//spamClassifier.filterHashMap();
		//hamClassifier.filterHashMap();
		HashMap<String,Integer> primordialLexicon = new HashMap<String,Integer>();
		for(String key : spamClassifier.hashbrown.keySet()){
			primordialLexicon.put(key,spamClassifier.hashbrown.get(key));
		}
		for(String key : hamClassifier.hashbrown.keySet()){
			primordialLexicon.put(key,primordialLexicon.get(key)==null?hamClassifier.hashbrown.get(key):primordialLexicon.get(key)+hamClassifier.hashbrown.get(key));
		}
		HashMap<String,Integer> lexicon = new HashMap<String,Integer>();
		for(String key:primordialLexicon.keySet()){
			if(primordialLexicon.get(key)>k)
				lexicon.put(key,primordialLexicon.get(key));
		}
		int numLexiconWords=lexicon.keySet().size();
		spamClassifier.computeProbabilities(lexicon,numLexiconWords);
		hamClassifier.computeProbabilities(lexicon,numLexiconWords);
		try{
			System.out.println("SPAM RESULTS");
			printTestResults(spamClassifier,hamClassifier,"spamtesting.txt");
			System.out.println("HAM RESULTS");
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
