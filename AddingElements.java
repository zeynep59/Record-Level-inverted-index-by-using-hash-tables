import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;


public class AddingElements {
	
	public static ArrayList<String> stopWords = new ArrayList<String>();
	
	
	
	
	public AddingElements(String rootFile) throws IOException {
		addWords(rootFile);
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void addWords(String rootFile) throws IOException {
		StopWords();
		
		LinkedList<?> words = new LinkedList<Object>();
		 java.util.List<File> filesInFolder = Files.walk(Paths.get(rootFile))
		            .filter(Files::isRegularFile)
		            .map(Path::toFile)
		            .collect(Collectors.toList());
		 
		 
		 int num = 0;
		 long duration = 0;
		 long startTime = System.nanoTime();
		for(int i=0; i<filesInFolder.size(); i++) {//filesInFolder.size()
				Scanner scanner = new Scanner(filesInFolder.get(i));
				String txtName = filesInFolder.get(i).toString().substring(4);
				while(scanner.hasNext()) {
					String[] tokens = scanner.nextLine().toLowerCase(Locale.ENGLISH).split(DELIMITERS);
					
					for(int j=0; j<tokens.length; j++) {
					if(!stopWords.contains(tokens[j])) {
					   
						if(Test.hashmap.getValue(tokens[j])==null) {
							ArrayList<Value> values = new ArrayList<Value>();
							Value newValue = new Value(txtName, 1);
							values.add(newValue);
							Test.hashmap.put(tokens[j], values);
							num++;
						}
						else { 
								updateValue(Test.hashmap.getValue(tokens[j]), txtName);
								
							}
					}}
					} long endTime = System.nanoTime();//System.currentTimeMillis();
					 duration = endTime - startTime;	
		}
		System.out.println("number of words that added: " + num);
		 double seconds = (double)duration / 1_000_000_000.0;
		 
		 System.out.println("Indexing Time:  " + seconds);
		 System.out.println("Collision number : " + (Test.hashmap.getCollisionNumber()));
		
		 
	}
	
		
	
	
	public static void StopWords() throws IOException {
		
		 File fileName = new File("stop_words_en.txt");
		 BufferedReader br= new BufferedReader(new FileReader(fileName));
		 String word;
		 
		 //determine the stop words
		 while ((word = br.readLine()) != null ) {
			 if(!stopWords.contains(word))
				 stopWords.add(word);
		 }
		
}

	public static boolean updateValue(ArrayList<Value> list, String fileName) {
		boolean contain = false;
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getFileName().equals(fileName)) {
				contain = true;
				list.get(i).increaseFrequency();
				break;
			}
				
		}
		if(!contain) {
			Value newValue  = new Value(fileName, 1);
			list.add(newValue);
		}
		return contain;
	}
	
	String DELIMITERS = "[-+=" +

		        " " +        //space

		        "\r\n " +    //carriage return line fit

				"1234567890" + //numbers

				"’'\"" +       // apostrophe

				"(){}<>\\[\\]" + // brackets

				":" +        // colon

				"," +        // comma

				"‒–—―" +     // dashes

				"…" +        // ellipsis

				"!" +        // exclamation mark

				"." +        // full stop/period

				"«»" +       // guillemets

				"-‐" +       // hyphen

				"?" +        // question mark

				"‘’“”" +     // quotation marks

				";" +        // semicolon

				"/" +        // slash/stroke

				"⁄" +        // solidus

				"␠" +        // space?   

				"·" +        // interpunct

				"&" +        // ampersand

				"@" +        // at sign

				"*" +        // asterisk

				"\\" +       // backslash

				"•" +        // bullet

				"^" +        // caret

				"¤¢$€£¥₩₪" + // currency

				"†‡" +       // dagger

				"°" +        // degree

				"¡" +        // inverted exclamation point

				"¿" +        // inverted question mark

				"¬" +        // negation

				"#" +        // number sign (hashtag)

				"№" +        // numero sign ()

				"%‰‱" +      // percent and related signs

				"¶" +        // pilcrow

				"′" +        // prime

				"§" +        // section sign

				"~" +        // tilde/swung dash

				"¨" +        // umlaut/diaeresis

				"_" +        // underscore/understrike

				"|¦" +       // vertical/pipe/broken bar

				"⁂" +        // asterism

				"☞" +        // index/fist

				"∴" +        // therefore sign

				"‽" +        // interrobang

				"※" +          // reference mark

		        "]";

}

