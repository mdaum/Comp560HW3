package ngram;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class Runner {
	static int totalwords=0;
	//NOTE: we need to calculate the probability of 
	public static void main(String[] args) {
		Scanner keyboard= new Scanner(System.in);
		int gram=Integer.parseInt(args[0]);
		double k = Integer.parseInt(args[1]);//20 looks like a high
		double m = Integer.parseInt(args[2]);//1 is 91 with k=20. NOTE: 0 results in 0s. Much break. 
		Classifier spamClassifier = new Classifier(k,m,"spamtraining.txt",gram);
		spamClassifier.LexiconIT();
		totalwords+=spamClassifier.totalWords;
		Classifier hamClassifier = new Classifier(k,m,"hamtraining.txt",gram);
		hamClassifier.LexiconIT();
		totalwords+=hamClassifier.totalWords;
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
			printTestResults(spamClassifier,hamClassifier,"spamtesting.txt");
			System.out.println("NOW FOR HAM");
			printTestResults(spamClassifier,hamClassifier,"hamtesting.txt");
		}
		catch(FileNotFoundException f){
			f.printStackTrace();
		}
		
	}
	public static void printTestResults(Classifier spamClassifier, Classifier hamClassifier, String testingDocFilePath) throws FileNotFoundException{
		Scanner reader = new Scanner(new File(testingDocFilePath));
		int i=0;
		while(reader.hasNext()){
			String nextTestingDoc = reader.next();
			double probSpam = spamClassifier.probabilityOfClass(nextTestingDoc,totalwords);
			double probHam = hamClassifier.probabilityOfClass(nextTestingDoc,totalwords);
			i=probSpam>probHam?i+1:i;
		}	
		System.out.println("percent of contents classified as spam: "+i);
		System.out.println("percent of contents classified as ham: "+(100-i));
	}
}
