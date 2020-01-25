// total = sum of first 7 in the array
// 8th is not counted in the count
// divide by each one by the total including the 8th 
// update the values of each indicies of the array with the new calculated weight
// if the calculated weight of the category from 1-7 threshold is less then 1/5 and catorgory 8 is any number rn
// Individual total depending on how many times that word occured.
// Keep track of the string 
// Ignore all zeros
// Write to a file
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class WeightCalculator
{
     
     /**
      * Takes in already filled trie and passes it through a traversal function
      * that is going to add the existing children to a stack.
      * @param root
      */
     public WeightCalculator(TrieNode root)
     {
          traverse(root);
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

          if(root.IsWord)
          {
               root.category = calculateWeight(root.category);
               writeFile(key, root.category);
               
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

     private void writeFile(String key, double[] weights)
     {
          try
          {
               File file = new File("../keyWeights.csv");

               if(!file.exists())
               {
                    file.createNewFile();
               }

               FileWriter outputFile = new FileWriter(file);

               outputFile.write(key + "," + Arrays.toString(weights));
          }
          catch(Exception e)
          {
               e.printStackTrace();
          }
     }
}