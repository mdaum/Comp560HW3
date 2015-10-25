import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


public class Runner {

	//NOTE: we need to calculate the probability of 
	public static void main(String[] args) {
		//Scanner keyboard= new Scanner(System.in);
		//int k=keyboard.nextInt();
		double k = 20;//20 looks like a high
		double m = 1;//1 is 91 with k=20. NOTE: 0 results in 0s. Much break. 
		FeatureComputer spamFeatureComputer = new FeatureComputer(k,m,"spamtraining.txt");
		spamFeatureComputer.LexiconIT();
		FeatureComputer hamFeatureComputer = new FeatureComputer(k,m,"hamtraining.txt");
		hamFeatureComputer.LexiconIT();
		try{
			printTestResults(m,spamFeatureComputer.totalWords,spamFeatureComputer.murmaider,hamFeatureComputer.murmaider,"spamtesting.txt");
		}
		catch(FileNotFoundException f){
			f.printStackTrace();
		}
		
	}
	//ACHTUNG: PROBABILITIES ARE NOT LOGS. YOU JUST GET A BUNCH OF NEGATIVE VALUES FROM LOGS
	//TAKE THE LEAST NEGATIVE VALUE
	public static double probabilityOfClass(double m, double v, HashMap<String,Double> probabilityHash, String filepath) throws FileNotFoundException{
		double probability=Math.log(.5);//yes, this IS the probability that it is either spam or ham based on what we observed from the training set
		Scanner reader = new Scanner(new File(filepath));
		while(reader.hasNext()){
			String next = reader.next();
			//System.out.println(next);
			//System.out.println("probability that "+next+" belongs to whatever class this is:"+(probabilityHash.get(next)==null?(m/((m+1)*v)):probabilityHash.get(next)));
			probability+=Math.log(probabilityHash.get(next)==null?(m/((m+1)*v)):probabilityHash.get(next));
		}
		return probability;
	}
	public static void printTestResults(double m, double v, HashMap<String,Double> spamProbabilityHash,HashMap<String,Double> hamProbabilityHash, String trainingDocFilePath) throws FileNotFoundException{
		Scanner reader = new Scanner(new File(trainingDocFilePath));
		int i=0;
		while(reader.hasNext()){
			String nextTrainingDoc = reader.next();
			//System.out.println(nextTrainingDoc+": "+probabilityOfClass(m,v,probabilityHash,nextTrainingDoc));
			double probSpam = probabilityOfClass(m,v,spamProbabilityHash,nextTrainingDoc);
			double probHam = probabilityOfClass(m,v,hamProbabilityHash,nextTrainingDoc);
			System.out.println(nextTrainingDoc+": "+(probSpam>probHam?"SPAAAAAM":"haaaaam"));
			//only use this if we're expecting spam. In other words, only do this if we are using the spam testing set
			i=probSpam>probHam?i+1:i;
		}	
		System.out.println(i);
	}
}
