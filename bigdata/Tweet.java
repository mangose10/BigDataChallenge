import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class Tweet 
{
    //raw tweet text
    private String text;
    //arraylist of all words from tweet
    private ArrayList<String> tweetWords;
    static final int ALPHABET_SIZE = 26; 
    static private TrieNode dictRoot;
    static private TreeMap<String,ArrayList<String>> contractions;
    
    public Tweet(String text) throws FileNotFoundException
    {
        this.text = text;
        tweetWords = new ArrayList<String>();
        dictRoot = new TrieNode();
        dictRoot.loadDictionary();
        contractions = new TreeMap<String,ArrayList<String>>();
        loadContractions();
        processTweet();
    }
    
    public ArrayList<String> getWords()
    {
        return tweetWords;
    }
    
    //private class for storing dictionary with fast lookups
    static class TrieNode { 
        TrieNode[] children = new TrieNode[ALPHABET_SIZE]; 
        boolean isWord;

        TrieNode(){ 
            isWord = false;
            for (int i = 0; i < ALPHABET_SIZE; i++) { 
                children[i] = null; 
            }
        } 

        //loads a dictionary from dictionary.txt which need to be in the same folder
        void loadDictionary() throws FileNotFoundException
        {
            FileReader inFile = null;
            inFile = new FileReader("dictionary.txt");
            Scanner in = new Scanner(inFile);
            int numWords = Integer.parseInt(in.nextLine());
            int letter = 0;
            String currWord;
            TrieNode dictNode = dictRoot;
            for(int word = 0; word < numWords; word++)
            {
                dictNode = dictRoot;
                currWord = in.nextLine();
                for(letter = 0; letter <currWord.length(); letter++)
                {
                    if(dictNode.children[currWord.charAt(letter) - 'a'] == null)
                    {
                        dictNode.children[currWord.charAt(letter) - 'a'] = new TrieNode();
                    }
                    dictNode = dictNode.children[currWord.charAt(letter) - 'a'];
                }
                dictNode.isWord = true;
            }
            in.close();
        }
    }; 

    //loads a lookup table for contractions from contractions.txt which needs to be in the same folder
    private void loadContractions() throws FileNotFoundException
    {
        FileReader inFile = null;
        inFile = new FileReader("contractions.txt");
        Scanner in = new Scanner(inFile);
        int numCnts = Integer.parseInt(in.nextLine());
        ArrayList<String> words = new ArrayList<String>();

        for(int cnt = 0; cnt < numCnts; cnt++)
        {
            words = new ArrayList<String>(Arrays.asList(in.nextLine().split(" ")));
            contractions.put(words.get(0), new ArrayList<String>(words.subList(1, words.size())));
        }

        in.close();
    }
    
    //
    private void processTweet()
    {
        String currWord = "";
        char currCh = text.charAt(0);
        
        //loop through the tweet character by character, determine where words
        //are and put them in the tweetWords ArrayList
        for(int tweetPos = 0; tweetPos < text.length(); tweetPos++)
        {
            currCh = text.charAt(tweetPos);
            //if we see a letter, add it to the word we're building
            if(Character.isAlphabetic(currCh))
            {
                currWord += Character.toString(Character.toLowerCase(currCh));
            }
            //if we see a #, finish the word we're building, then call a helper
            //funciton for dealing with hashtags.
            if(currCh == '#')
            {
                if(!currWord.isEmpty())
                {
                    tweetWords.add(currWord);
                }
                currWord = "";
                tweetPos = processTag(tweetPos);
            }
            //if we see a ', call a helper function for dealing with contractions
            if(currCh == '\'')
            {
                tweetPos = processContraction(tweetPos, currWord);
                currWord = "";
            }
            //if we see a whitespace, finish the word we're building
            if(Character.isWhitespace(currCh))
            {
                if(!currWord.isEmpty())
                {
                    tweetWords.add(currWord);
                }
                currWord = "";
            }
        }
        tweetWords.add(currWord);
    }
    
    //helper function for processing hashtags
    //input: character position in the tweet of the start of the hashtag 
    //function: splits hashtag into seperate words and adds them to tweetWords.
    //          if other special character sequences are detected at the end of
    //          of a hashtag, the appropriate helper function is called to deal 
    //          with it
    //output: character position in the tweet of the end of the hashtag and other
    //          special character sequences following
    private int processTag(int tweetPos)
    {
        String tag = "";
        char currCh = text.charAt(tweetPos);
        //if theres a bunch of #'s in a row, ignore them
        while(currCh == '#')
        {
            tweetPos++;
            currCh = text.charAt(tweetPos);
        }
        //continue looping through tweet from the given position until the end
        //the hashtag is detected, then split it into words
        for(; tweetPos < text.length(); tweetPos++)
        {
            currCh = text.charAt(tweetPos);
            if(Character.isAlphabetic(currCh))
            {
                tag += Character.toString(Character.toLowerCase(currCh));
            }
            if(currCh == '#')
            {
                tweetWords.addAll(findWords(tag));
                return processTag(tweetPos);
            }
            if(currCh == '\'')
            {
                tweetWords.addAll(findWords(tag));
                return processContraction(tweetPos, "");
            }
            if(Character.isWhitespace(currCh))
            {
                tweetWords.addAll(findWords(tag));
                return tweetPos;
            }
        }
        tweetWords.addAll(findWords(tag));
        return tweetPos;
    }
    
    //helper function for processing contractions
    //input: character position in the tweet of the last character read
    //      String containing letters at the start of the contraction
    //function: splits contraction into seperate words and adds them to tweetWords.
    //          if other special character sequences are detected at the end of
    //          of a contraction, the appropriate helper function is called to 
    //          deal with it
    //output: character position in the tweet of the end of the contraction and
    //          other special character sequences following
    private int processContraction(int tweetPos, String currWord)
    {
        char currCh = text.charAt(tweetPos);
        //continue looping through tweet from the given position until the end
        //the contraction is detected, then uncontract it
        for(; tweetPos < text.length(); tweetPos++)
        {
            currCh = text.charAt(tweetPos);
            if(Character.isAlphabetic(currCh))
            {
                currWord += Character.toString(Character.toLowerCase(currCh));
            }
            if(currCh == '#')
            {
                tweetWords.addAll(uncontract(currWord));
                return processTag(tweetPos);
            }
            if(Character.isWhitespace(currCh))
            {
                tweetWords.addAll(uncontract(currWord));
                return tweetPos;
            }
        }
        tweetWords.addAll(uncontract(currWord));
        return tweetPos;
    }

    //function for splitting a string of unseperated words
    //input: string of unseperated words
    //output: ArrayList of word in the string that were recognized
    private ArrayList<String> findWords(String wordsTogether)
    {
        ArrayList<String> words = new ArrayList<String>();
        String currWord = "";
        char currLetter;
        TrieNode dictPos = dictRoot;
        int goodLetter = 0;
        int goodLength = 0;
        int length = 0;

        for(int letter = 0; letter < wordsTogether.length(); letter++)
        {
            currLetter = wordsTogether.charAt(letter);
            currWord += currLetter;
            dictPos = dictPos.children[currLetter - 'a'];
            length++;
            if(dictPos == null)
            {
                if(goodLength > 0)
                {
                    words.add(currWord.substring(0, goodLength));
                    letter = goodLetter;
                }
                length = 0;
                goodLength = 0;
                currWord = "";
                dictPos = dictRoot;
            }
            else
            if(dictPos.isWord)
            {
                goodLetter = letter;
                goodLength = length;
            }
        }
        if(goodLength > 0)
        {
            words.add(currWord.substring(0, goodLength));
        }
        return words;
    }

    ArrayList<String> uncontract(String contraction)
    {
        if(contractions.containsKey(contraction))
        {
            return contractions.get(contraction);
        }
        ArrayList<String> word = new ArrayList<String>();
        word.add(contraction);
        return word;
    }
}