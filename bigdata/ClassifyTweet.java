import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClassifyTweet {

    // Alphabet size (# of letters + 1 for space) 
    static final int ALPHABET_SIZE = 26 + 1; 
    static final int CATEGORY_COUNT = 8;
    
    static TrieNode root;  

    static void insert(String key, double[] category) { 

        int level; 
        int length = key.length(); 
        int index; 
       
        TrieNode pCrawl = root; 

        for (level = 0; level < length; level++) {

            if (key.charAt(level) == ' '){
                index = 26;
            }else{
                index = key.charAt(level) - 'a'; 
            }

            if (pCrawl.children[index] == null) 
                pCrawl.children[index] = new TrieNode(); 
       
            pCrawl = pCrawl.children[index]; 
        } 

        pCrawl.category = category;
    } 

    static double[] search(String key) { 

        int level; 
        int length = key.length(); 
        int index; 
        TrieNode pCrawl = root; 
       
        for (level = 0; level < length; level++) { 

            if (key.charAt(level) == ' '){
                index = 26;
            }else{
                index = key.charAt(level) - 'a'; 
            }

            if (pCrawl.children[index] == null) 
                return new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}; 
       
            pCrawl = pCrawl.children[index]; 
        } 
        
        if (pCrawl != null)
            return pCrawl.category;

        return new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    } 

    static void insertFromFile(String filename) throws FileNotFoundException {
        
        ArrayList<String> inputArray = new ArrayList<String>();
        Scanner fileReader = new Scanner(new File(filename));

        while (fileReader.hasNextLine()){
            inputArray.add(fileReader.nextLine());
        }

        for(int i = 0; i < inputArray.size(); i ++){

            double[] category = new double[CATEGORY_COUNT];
            String[] line = inputArray.get(i).split(",");
            
            for (int j = 0; j < CATEGORY_COUNT; j++){
                category[j] = Double.parseDouble(line[j+1]);
            }

            String test = "me with the last bottle of bottled water in florida apparently florida";
            ArrayList<String> tList = new ArrayList<String>();
            for(String text:test.split(" ")) {
                tList.add(text);
            }

            insert(line[0], category);
        }

        fileReader.close();
    }

    static int[] classifyTweet(ArrayList<String> keys) {

        double[] weightSingle = new double[CATEGORY_COUNT];
        double[] weightDouble = new double[CATEGORY_COUNT];
        double[] weightTriple = new double[CATEGORY_COUNT];
        double[] result = new double[CATEGORY_COUNT];

        for (int i = 0; i < keys.size(); i++){

            weightSingle = search(keys.get(i));

            if ((i + 1) < keys.size())
                weightDouble = search(keys.get(i) + " " + keys.get(i + 1));
            else
                weightDouble = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

            if ((i + 2) < keys.size())
                weightTriple = search(keys.get(i) + " " + keys.get(i + 1) + " " + keys.get(i + 2));
            else
                weightTriple = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

            for (int j = 0; j < CATEGORY_COUNT; j++){
                result[j] += weightSingle[j];
                result[j] += weightDouble[j];
                result[j] += weightTriple[j];
            }
        }
        System.out.println(Arrays.toString(result));
        return processResult(result);
    }

    static int[] processResult(double[] results){

        int category = 1;
        int disaster = 0;

        //exclude last space
        for (int i = 1; i < CATEGORY_COUNT - 1; i++){
            if (results[i] > results[category - 1]){
                category = i + 1;
            }
        }

        disaster = results[CATEGORY_COUNT - 1] >= 0 ? 1 : 0;

        return new int[] {category, disaster};
    }

    static void classifyFile(String filename) throws FileNotFoundException{

        ArrayList<String> inputArray = new ArrayList<String>();
        ArrayList<String> tweetArray = new ArrayList<String>();
        Scanner fileReader = new Scanner(new File(filename));

        while (fileReader.hasNextLine()){
            inputArray.add(fileReader.nextLine());
        }

        //Could seperate by clauses here...
        
        for (String tweet: inputArray){
            tweetArray = new Tweet(tweet).getWords();
            int[] results = classifyTweet(tweetArray);
            System.out.println(tweet + ", " + results[0] + ", " + results[1]);
        }

        fileReader.close();
        
    }

    public static void main(String args[]) {
        
        root = new TrieNode();

        try {
            insertFromFile("../keyWeights.csv");
            classifyFile("../testInput.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}