import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


public class Test {
	
	public static DictionaryInterface<String, ArrayList> hashmap = new HashedDictionary<String, ArrayList>();
	public static ArrayList<String> stopWords = new ArrayList<String>();
	public static LinkedList<Value> values = new LinkedList<Value>();
	
	
	public static void main(String[] args) throws IOException {
		
		String rootFile = "bbc";
		hashmap.setLoadFactor(0.8);  //0.5  or 0.8
		hashmap.setHashType("PAF");  //PAF or SSF
		hashmap.setCollisionHandlingType("DH"); //DH OR LP
		AddingElements adding = new AddingElements(rootFile);
		searchWords();

		
				
		}
		
	
	
	
	public static void searchWords() throws FileNotFoundException {
		 File file = new File( "search.txt");
		 Scanner scanner = new Scanner(file);
		 long avgTime = 0;
		 long minTime = 99999, maxTime = -1;
		 int step = 0;
		 while(scanner.hasNextLine()) {
			 String word = scanner.nextLine();
			 long startTime = System.nanoTime();

			Object x =  hashmap.getValue(word);
			 long endTime = System.nanoTime();
			 //print the values of searched keys
			/* if(x!=null) {
				 System.out.println("\n" +word);
				 displayList(hashmap.getValue(word));
			 }*/
			 
			 long duration = endTime - startTime;
			 if(minTime>duration)
				 minTime  = duration;
			 if(maxTime<duration)
				 maxTime = duration;
			 avgTime += duration;
			 step++;
		 }
		 avgTime /= step;
		 System.out.println("Average search time is: " +  avgTime +"  nano second");
		 System.out.println("Minimum search time is: " +  minTime +"  nano second" );
		 System.out.println("Maximum search time is: " +  maxTime +"  nano second");
		
	}
	
	
	
	
	

	
	public static void displayList(ArrayList<Value> list) {
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i).getFileName() + "-----" + list.get(i).getFrequency()+"      ");
		}
	}
	
	

	

	
}
