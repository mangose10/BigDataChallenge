// if the calculated weight of the category from 1-7 threshold is less then 1/5 and catorgory 8 is any number rn
// Ignore all zeros

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class WeightCalculator
{
    String output = "";

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
            output += key + ',' + Arrays.toString(root.category) + '\n';
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
                    traverse(root.children[i], key + Character.toString('a' + i));
                }
            }
        }
    }

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