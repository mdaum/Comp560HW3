import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Classifier {
	int numDistinctWords=0,totalWords=0,numClassFiles;
	double k,m;
	String filepath;
	HashMap<String, Integer> hashbrown= new HashMap<String, Integer>();
	HashMap<String,Integer> refinedHashbrown= new HashMap<String,Integer>();
	HashMap<String, Double> murmaider = new HashMap<String, Double>();
	boolean windows;

	public Classifier(double k, double m, String filepath) {
		this.m = m;
		this.k = k;
		this.filepath = filepath;
		this.windows=System.getProperty("os.name").toLowerCase().contains("win");
	}

	public void LexiconIT() {
		try {
			Scanner reader = new Scanner(new File(filepath));
			//int i=0;
			while(reader.hasNextLine()){
				String nextFilePath=reader.nextLine();
				//System.out.println("File "+i);
				if(nextFilePath.length()>3){//check to see if it's just an empty new line character
					numClassFiles++;
					//all logic for while loop in here
					if(windows)nextFilePath.replaceAll("\\\\","/");
					Scanner innerRead=new Scanner(new File(nextFilePath));
					while(innerRead.hasNext()){
						totalWords++;
						String itemToHash=innerRead.next();
						if(itemToHash.contains("\n"))itemToHash=itemToHash.substring(0, itemToHash.length()-1);//sanitized!
						hashbrown.put(itemToHash, hashbrown.get(itemToHash)==null?1:hashbrown.get(itemToHash)+1);
					}
					innerRead.close();
				}
				//stupid EOF
				//i++;
			}
			//sSystem.out.println("done hashing everything");
			//System.out.println(hashbrown.containsKey("the"));
			//System.out.println(hashbrown.get("buy"));
			numDistinctWords=hashbrown.keySet().size();
			filterHashMap();
			computeProbabilities();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	public void filterHashMap(){
		refinedHashbrown = new HashMap<String,Integer>();
		for(String key : hashbrown.keySet()){
			if(hashbrown.get(key)>k)
				refinedHashbrown.put(key, hashbrown.get(key));
		}
	}
	public void computeProbabilities(){
		for(String key : refinedHashbrown.keySet()){
			murmaider.put(key, ((double)(refinedHashbrown.get(key)+m))/((double)(totalWords*(m+1))));
		}
	}
	public double probabilityOfClass(String filepath) throws FileNotFoundException{
		double probability=Math.log(.5);//yes, this IS the probability that it is either spam or ham based on what we observed from the training set
		Scanner reader = new Scanner(new File(filepath));
		while(reader.hasNext()){
			String next = reader.next();
			//System.out.println(next);
			//System.out.println("probability that "+next+" belongs to whatever class this is:"+(probabilityHash.get(next)==null?(m/((m+1)*v)):probabilityHash.get(next)));
			probability+=Math.log(murmaider.get(next)==null?(m/((m+1)*(double)totalWords)):murmaider.get(next));
		}
		return probability;
	}
	
}
