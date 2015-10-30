package ngram;
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
	int gram;

	public Classifier(double k, double m, String filepath,int gram) {
		this.m = m;
		this.k = k;
		this.gram=gram;
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
					String file="";
					while(innerRead.hasNextLine()){
						file+=innerRead.nextLine()+"\n";
					}
					/*String itemToHash=innerRead.next();
					if(itemToHash.contains("\n"))itemToHash=itemToHash.substring(0, itemToHash.length()-1);//sanitized!
					hashbrown.put(itemToHash, hashbrown.get(itemToHash)==null?1:hashbrown.get(itemToHash)+1);*/
					file=file.replaceAll("\n"," ");
					String tokens[]=file.split(" ");
					
					for(int i=0;i<tokens.length-gram+1;i++){
						//here append next n(gram) tokens, starting from i, to a string which we will hash (string is space-delimited)
						String toHash = "";
						for(int j=0;j<gram;j++){
							if(j!=gram-1)toHash+=tokens[i+j]+" ";
							else toHash+=tokens[i+j];
						}
						hashbrown.put(toHash, hashbrown.get(toHash)==null?1:hashbrown.get(toHash)+1);
						totalWords++;
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
	public void computeProbabilities(int goo){
		for(String key : refinedHashbrown.keySet()){
			murmaider.put(key, ((double)(refinedHashbrown.get(key)+m))/((double)(goo*m)+(double)totalWords));
		}
	}
	public double probabilityOfClass(String filepath,int goo) throws FileNotFoundException{
		double probability=Math.log(.5);//yes, this IS the probability that it is either spam or ham based on what we observed from the training set
		Scanner reader = new Scanner(new File(filepath));
		String file="";
		while(reader.hasNextLine()){
			file+=reader.nextLine()+"\n";
		}
		/*String itemToHash=innerRead.next();
		if(itemToHash.contains("\n"))itemToHash=itemToHash.substring(0, itemToHash.length()-1);//sanitized!
		hashbrown.put(itemToHash, hashbrown.get(itemToHash)==null?1:hashbrown.get(itemToHash)+1);*/
		file=file.replaceAll("\n"," ");
		String tokens[]=file.split(" ");
		
		for(int i=0;i<tokens.length-gram+1;i++){
			//here append next n(gram) tokens, starting from i, to a string which we will hash (string is space-delimited)
			String toHash = "";
			for(int j=0;j<gram;j++){
				if(j!=gram-1)toHash+=tokens[i+j]+" ";
				else toHash+=tokens[i+j];
			}
			String next=toHash;
			probability+=Math.log(murmaider.get(next)==null?(m/((double)totalWords+(double)(goo)*m)):murmaider.get(next));
		}
			//System.out.println(next);
			//System.out.println("probability that "+next+" belongs to whatever class this is:"+(probabilityHash.get(next)==null?(m/((m+1)*v)):probabilityHash.get(next)));
		
		return probability;
	}
	
}
