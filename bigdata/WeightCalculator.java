// if the calculated weight of the category from 1-7 threshold is less then 1/5 and catorgory 8 is any number rn
// Ignore all zeros

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

public class WeightCalculator
{
     String output = "";

     double AVERAGE = 0.0;

     double THRESHOLD = 0.2;

     private double[] TOTAL;

     /**
     * Takes in already filled trie and passes it through a traversal function
     * that is going to add the existing children to a stack.
     * @param root
     */
     public WeightCalculator(TrieNode root)
     {  
          TOTAL = root.category;
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

               thresholdMaker(root.category);
            
               boolean isValid = checkThreshold(root.category);

               if(isValid)
               {
                    output += key + ',' + Arrays.toString(root.category) + '\n';
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

          int length = weight.length;

          //System.out.println(Arrays.toString(weight));
          //System.out.println(Arrays.toString(TOTAL));

          for(int i = 0; i < length; i++)
          {
              //System.out.println(weight[i] + " : " + i);
               weight[i] = weight[i] / TOTAL[i];
          }

          return weight;
     }

    /**
     * Checks the array to see if any of the weights are below
     * the given threshold. If all the values of the array are 
     * below the given threshold then ignore that weight 
     * @param weight
     * @return
     */
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

     private void thresholdMaker(double[] probability)
     {
          Double[] buffer = new Double[probability.length];

          for(int i = 0; i < buffer.length; i++)
          {
              buffer[i] = probability[i];
          }

          Double max = Collections.max(Arrays.asList(buffer));

          double average = 0.0;

          for(int i = 0; i < probability.length; i++)
          {
               average += probability[i];
          }

          THRESHOLD = max - average;
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