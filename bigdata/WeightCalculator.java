// if the calculated weight of the category from 1-7 threshold is less then 1/5 and catorgory 8 is any number rn
// Ignore all zeros

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class WeightCalculator
{
    String output = "";
    final static double THRESHOLD = 0.20;

    /**
     * Takes in already filled trie and passes it through a traversal function
     * that is going to add the existing children to a stack.
     * @param root
     */
    public WeightCalculator(TrieNode root)
    {
         traverse(root, "");
    }

    /**
     * Traverse through the Trie and add an existing children to the stack and 
     * continue to travse through until u reach a null terminating child
     * @param root
     */
    private void traverse(TrieNode root, String key)
    {
        if(root == null)
        {
            return;
        }         

        if(root.isWord)
        {
            System.out.println(key);
            root.category = calculateWeight(root.category);
            String line = Arrays.toString(root.category);            
            boolean isValid = checkThreshold(root.category);

            if(isValid)
            {
                output += key + ',' + line.substring(1, line.length() - 1).replace(" ", "") + '\n';
            }
        }
          
        int length = root.children.length;

        for(int i = 0; i < length; i++)
        {
            if(root.children[i] == null)
            {
                continue;
            }
            else
            {
                if(i == 26)
                {
                    traverse(root.children[i], key + " ");
                }
                else
                {
                    traverse(root.children[i], key + Character.toString((char)('a' + i)));
                }
            }
        }
    }


    /**
     * Iterates throught he double array checking the weight of each category.
     * If all the weights are equal to zero we will ignore that array, and not calculate
     * anything for that array. As well if it is below the threshold we will also ignore
     * that array from the trie. 
     * 
     * Calculating the weight by getting the sum of all the weights except disaster, then
     * divide each weight by the sum of weights. Including the disaster category
     * @param weight
     * @return updated array with the correct weight counts
     */
    private double[] calculateWeight(double[] weight)
    {
        double total = 0.0;

        int length = weight.length;

        for(int i = 0; i < length-1; i++)
        {
            total += weight[i];
        }
    
        for(int i = 0; i < length; i++)
        {
            weight[i] = weight[i] / total;
        }

        return weight;
    }

    private boolean checkThreshold(double[] weight)
    {
        int count = 0;
        int length = weight.length;
        
        for(int i = 0; i < length; i++)
        {
            if(weight[i] <= THRESHOLD)
            {
                count++;
            }
        }

        if(count == length)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Takes in the string that is being read, and writes that strings
     * array into the a file called keyWeights. If the file does exist
     * it will just re-write to the file, When it the file doesn't exist 
     * it will create a new one and write to it.
     * @param key
     */
    public void writeFile(String key)
    {
        try
        {
            File file = new File("../keyWeights.csv");

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
}