
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class WeightMaker { 
      
    // Alphabet size (# of letters + 1 for space) 
    static final int ALPHABET_SIZE = 26 + 1; 
    static final int CATEGORY_COUNT = 8;


    //Class to store Tweet its category and whether its a diasaster
    public static class WeightedTweet {

        ArrayList<String> keys;
        int category;
        int disaster;
    
        WeightedTweet(ArrayList<String> tweet, int category, int disaster){
            this.keys = tweet;
            this.category = category;
            this.disaster = disaster;
        }
    };

    static TrieNode root;  

    /**
     * Inserts tweet with category and disaster weights. If disaster is 0 (not a disaster) we'll
     * switch it to -1 so that the weight is properly balanced (negative weight == no disaster)
     * 
     * @param key the word(s) to add to the Trie
     * @param category the category of the given tweet
     * @param disaster whether or not its a disaster
     */
    static void insertWithWeight(String key, int category, int disaster) { 

        int level; 
        int length = key.length(); 
        int index; 
       
        TrieNode pCrawl = root; 
       
        if (disaster == 0)
            disaster = -1;

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

        pCrawl.isWord = true;
        pCrawl.category[category - 1] ++;
        pCrawl.category[CATEGORY_COUNT - 1] += disaster;

        //System.out.println(key);
    } 

    /**
     * Searches for the key parameter and returns a double[] 0-6 are the category and 7 is disaster
     * in order to support consecutive words, the space character will be stored as a child
     * 
     * @param key word(s) to search
     * @return double[] 0-6 are the category and 7 is disaster
     */
    static double[] weightedSearch(String key) { 

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

    /**
     * Reads a csv file by using the "," delimiter. Creates an ArrayList of WeightedTweet to be inserted.
     * 
     * 
     * @param filename name of the file to read
     * @return ArrayList of WeightedTweet made from file values
     * @throws FileNotFoundException
     */
    static ArrayList<WeightedTweet> readWeightedFile(String filename) throws FileNotFoundException {

        ArrayList<WeightedTweet> tweetList = new ArrayList<WeightedTweet>();
        ArrayList<String> inputArray = new ArrayList<String>();
        Scanner fileReader = new Scanner(new File(filename));

        while (fileReader.hasNextLine()){
            inputArray.add(fileReader.nextLine());
        }

        for(int i = 0; i < inputArray.size(); i++){

            String[] line = inputArray.get(i).split(",");

            WeightedTweet curTweet = new WeightedTweet(new Tweet(line[0]).getWords(),
                                                       Integer.parseInt(line[1]),
                                                       Integer.parseInt(line[2]));
                                                       
            tweetList.add(curTweet);
        }

        fileReader.close();

        return tweetList;
    }
       
    // Driver 
    public static void main(String args[]) { 
        try{
            Tweet test = new Tweet("aljsghjhs #theldjfhlakj hlaevf");
            System.out.println(test.getWords());
        }
        catch(Exception e){
            System.out.println("\nno dict\n");
            return;
        }
    } 
}