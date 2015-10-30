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
		System.out.println("what gram?");
		int gram=keyboard.nextInt();
		double k = 8;//20 looks like a high
		double m = 45;//1 is 91 with k=20. NOTE: 0 results in 0s. Much break. 
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
		/*for(String key : spamClassifier.murmaider.keySet()){
			int count=spamClassifier.hashbrown.get(key);
			System.out.println(key+"   "+count);
		}
		System.out.println("AND NOW FOR HAM");
		for(String key : hamClassifier.murmaider.keySet()){
			int count=hamClassifier.hashbrown.get(key);
			System.out.println(key+"   "+count);
		}*/
		
		/*int max=0;
		String keyMax="";
		for(String key:spamClassifier.refinedHashbrown.keySet()){
			if(spamClassifier.refinedHashbrown.get(key)>max){
				max=spamClassifier.refinedHashbrown.get(key);
				keyMax=key;
			}
		}
		System.out.println(keyMax+" "+max);*/
	}
	//ACHTUNG: PROBABILITIES ARE NOT LOGS. YOU JUST GET A BUNCH OF NEGATIVE VALUES FROM LOGS
	//TAKE THE LEAST NEGATIVE VALUE
	public static void printTestResults(Classifier spamClassifier, Classifier hamClassifier, String testingDocFilePath) throws FileNotFoundException{
		Scanner reader = new Scanner(new File(testingDocFilePath));
		int i=0;
		while(reader.hasNext()){
			String nextTestingDoc = reader.next();
			//System.out.println(nextTrainingDoc+": "+probabilityOfClass(m,v,probabilityHash,nextTrainingDoc));
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
