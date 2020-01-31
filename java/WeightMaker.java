import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import bigdata.Tweet;


public class WeightMaker { 
      
    // Alphabet size (# of letters + 1 for space) 
    static final int ALPHABET_SIZE = 26 + 1; 
    static final int CATEGORY_COUNT = 8;
      
    // trie node 
    static class TrieNode { 

        TrieNode[] children = new TrieNode[ALPHABET_SIZE]; 
        double[] category = new double[CATEGORY_COUNT];         /* used to store category weights and the disaster weight*/
          
        TrieNode(){ 
            for (int i = 0; i < ALPHABET_SIZE; i++) { 
                children[i] = null; 
            }
        } 
    }; 

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
     * Reads a csv file by using the "," delimiter. Creates an ArrayList of WeightedTweet to be inserted
     * 
     * @param filename name of the file to read
     * @return ArrayList of WeightedTweet made from file values
     * @throws FileNotFoundException
     */
    static ArrayList<WeightedTweet> readWeightedFile(String filename) throws FileNotFoundException {

        ArrayList<WeightedTweet> tweetList = new ArrayList<WeightedTweet>();
        ArrayList<String> bufferArray = new ArrayList<String>();
        Scanner fileReader = new Scanner(new File(filename));
        fileReader.useDelimiter(",");

        while (fileReader.hasNext()){
            
            bufferArray.add(fileReader.next());
        }

        //when inserting a weighted tweet bufferArray.size() should always be a multiple of 3
        for(int i = 0; i < bufferArray.size()/3; i++){

            String test = "me with the last bottle of bottled water in florida apparently florida";
            ArrayList<String> tList = new ArrayList<String>();
            for(String text:test.split(" ")) {
                tList.add(text);
            }

            //For every i % 3 == 0, the last character is an "invisible" symbol that could be \n but to
            //make sure I'll just remove it all together, the expected value for a i % 3 == 0 is 0 or 1

            //WeightedTweet curTweet = new WeightedTweet(new Tweet(bufferArray.get(i)).tweetWords,
            WeightedTweet curTweet = new WeightedTweet(tList,
                                                       Integer.parseInt(bufferArray.get(i + 1)),
                                                       bufferArray.get(i + 2).charAt(0) - '0');
                                                       
            tweetList.add(curTweet);
        }

        fileReader.close();

        return tweetList;
    }
       
    // Driver 
    public static void main(String args[]) { 
        Tweet test;
        try{
        test = new Tweet("aljsghjhs #aksjdhdkas");
        }
        catch(Exception e){
            System.out.println("no dict");
            return;
        }
        System.out.println(test.getWords());
        /*
        root = new TrieNode(); 
        ArrayList<WeightedTweet> tweets = new ArrayList<WeightedTweet>();
        try {
            tweets = readWeightedFile("../input.csv");
        } catch (FileNotFoundException event) {
            event.printStackTrace();
        }

        for (WeightedTweet tweet: tweets){
            for (String key: tweet.keys){
                insertWithWeight(key, tweet.category, tweet.disaster);
            }

            for (int i = 0; i < tweet.keys.size() - 1; i++){
                String twoWord = tweet.keys.get(i) + " " + tweet.keys.get(i + 1);
                insertWithWeight(twoWord, tweet.category, tweet.disaster);
            }

            for (int i = 0; i < tweet.keys.size() - 2; i++){
                String twoWord = tweet.keys.get(i) + " " + tweet.keys.get(i + 1) + " " + tweet.keys.get(i + 2);
                insertWithWeight(twoWord, tweet.category, tweet.disaster);
            }
        }

        //For testing purposes we're gonna add "last" as 1, 0
        insertWithWeight("last", 1, 0);

        // Search for different keys 
        System.out.println(Arrays.toString(weightedSearch("these")));
        System.out.println(Arrays.toString(weightedSearch("bottle")));
        System.out.println(Arrays.toString(weightedSearch("florida")));
        System.out.println(Arrays.toString(weightedSearch("last")));
        System.out.println(Arrays.toString(weightedSearch("me with")));
        System.out.println(Arrays.toString(weightedSearch("last bottle of")));
        */

    } 
} 
// This code is contributed by Sumit Ghosh 