import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class FeatureComputer {
	int k;
	String filepath;
	HashMap<String, Integer> hashbrown= new HashMap<String, Integer>();
	boolean windows;

	public FeatureComputer(int k, String filepath) {
		this.k = k;
		this.filepath = filepath;
		this.windows=System.getProperty("os.name").toLowerCase().contains("win");
	}

	public void LexiconIT() {
		try {
			Scanner reader = new Scanner(new File(filepath));
			int i=0;
			while(reader.hasNextLine()){
				String nextFilePath=reader.nextLine();
				System.out.println("File "+i);
				if(nextFilePath.length()>3){//all logic for while loop in here
					if(windows)nextFilePath.replaceAll("\\\\","/");
					Scanner innerRead=new Scanner(new File(nextFilePath));
					while(innerRead.hasNext()){
						String tohashMaybe=innerRead.next();
						if(tohashMaybe.equals("buy"))System.out.println("We got it! YUH!MILKMAN");
						if(tohashMaybe.contains("\n"))tohashMaybe=tohashMaybe.substring(0, tohashMaybe.length()-1);//sanitized!
						hashbrown.put(tohashMaybe, hashbrown.get(tohashMaybe)==null?1:hashbrown.get(tohashMaybe)+1);
					}
					innerRead.close();
				}
				//stupid EOF
				i++;
			}
			System.out.println("done hashing everything");
			System.out.println(hashbrown.containsKey("the"));
			System.out.println(hashbrown.get("buy"));
			
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
