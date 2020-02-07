import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClassifyTweet {

    // Alphabet size (# of letters + 1 for space)
    static final int ALPHABET_SIZE = 26 + 1;
    static final int CATEGORY_COUNT = 8;
    private static String output = "";

    static TrieNode root;

    static void insert(String key, double[] category) {

        int level;
        int length = key.length();
        int index;

        TrieNode pCrawl = root;

        for (level = 0; level < length; level++) {

            if (key.charAt(level) == ' ') {
                index = 26;
            } else {
                index = key.charAt(level) - 'a';
            }

            if ((index + 'a') > 123)
                System.out.println(index + 'a');

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

            if (key.charAt(level) == ' ') {
                index = 26;
            } else {
                index = key.charAt(level) - 'a';
            }

            if (pCrawl.children[index] == null)
                return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

            pCrawl = pCrawl.children[index];
        }

        if (pCrawl != null)
            return pCrawl.category;

        return new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    }

    static void insertFromFile(String filename) throws FileNotFoundException {

        ArrayList<String> inputArray = new ArrayList<String>();
        Scanner fileReader = new Scanner(new File(filename));

        while (fileReader.hasNextLine()) {
            inputArray.add(fileReader.nextLine());
        }

        for (int i = 0; i < inputArray.size(); i++) {

            double[] category = new double[CATEGORY_COUNT];
            String[] line = inputArray.get(i).split(",");

            for (int j = 0; j < CATEGORY_COUNT; j++) {
                category[j] = Double.parseDouble(line[j + 1]);
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

        for (int i = 0; i < keys.size(); i++) {
            // System.out.println(keys.get(i));
            weightSingle = search(keys.get(i));

            if ((i + 1) < keys.size())
                weightDouble = search(keys.get(i) + " " + keys.get(i + 1));
            else
                weightDouble = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

            if ((i + 2) < keys.size())
                weightTriple = search(keys.get(i) + " " + keys.get(i + 1) + " " + keys.get(i + 2));
            else
                weightTriple = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

            for (int j = 0; j < CATEGORY_COUNT; j++) {
                result[j] += weightSingle[j];
                result[j] += weightDouble[j];
                result[j] += weightTriple[j];
            }
        }
        // System.out.println(Arrays.toString(result));
        return processResult(result);
    }

    static int[] processResult(double[] results) {

        int category = 1;
        int disaster = 0;

        // exclude last space
        for (int i = 0; i < CATEGORY_COUNT - 1; i++) {
            if (results[i] > results[category - 1]) {
                category = i + 1;
            }
        }

        disaster = results[CATEGORY_COUNT - 1] >= 0 ? 1 : 0;
        System.out.println(category + ", " + disaster);
        System.out.println(Arrays.toString(results));
        return new int[] { category, disaster };
    }

    static void classifyFile(String filename) throws IOException {

        ArrayList<String> inputArray = new ArrayList<String>();
        ArrayList<String> tweetArray = new ArrayList<String>();
        BufferedReader fileReader = new BufferedReader(new InputStreamReader (new FileInputStream(filename), "UTF8"));

        String curLine  = fileReader.readLine();
        while ((curLine  = fileReader.readLine()) != null){
            System.out.println(curLine);
            if (!curLine.equals(",,") &&  !curLine.equals("")){
                inputArray.add(curLine.replaceAll("’", "'")); //.replaceAll("“", "\"").replaceAll("", "\"")
            }
        }

        //Could seperate by clauses here...
        
        for (String tweet: inputArray){
            System.out.println(tweet);
            tweetArray = new Tweet(tweet).getWords();
            int[] results = classifyTweet(tweetArray);
            output += tweet + ", " + results[0] + ", " + results[1] + "\n";
            System.out.println(tweet);
        }

        writeFile(output);

        fileReader.close();
    }

    public static void writeFile(String key)
    {
        try
        {
            File file = new File("../output.csv");

            if(!file.exists())
            {
                file.createNewFile();
            }

            FileWriter outputFile = new FileWriter(file);

            outputFile.write(key);

            outputFile.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        
        root = new TrieNode();

        try {
            insertFromFile("../keyWeights.csv");
            classifyFile("../testWithNew.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}