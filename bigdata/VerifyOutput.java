import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * VerifyOutput
 */
public class VerifyOutput {

    // seperate keys
    static ArrayList<int[]> getOut(String filename) throws IOException {

        ArrayList<int[]> tweetList = new ArrayList<int[]>();
        ArrayList<String> inputArray = new ArrayList<String>();
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));

        String curLine = "";
        while ((curLine = fileReader.readLine()) != null) {
            // System.out.println(curLine);
            if (!curLine.equals(",,") && !curLine.equals("")) {
                inputArray.add(curLine.replaceAll("’", "'")); // .replaceAll("“", "\"").replaceAll("", "\"")
            }
        }

        for (int i = 0; i < inputArray.size(); i++) {

            int[] line = new int[2];
            
            line[0] = Integer.parseInt(
                    inputArray.get(i).substring(inputArray.get(i).length() - 4, inputArray.get(i).length() - 3));
            line[1] = Integer.parseInt(inputArray.get(i).substring(inputArray.get(i).length() - 1));

            tweetList.add(line);
        }

        fileReader.close();

        return tweetList;
    }

    static ArrayList<int[]> getKeys(String filename) throws IOException {

        ArrayList<int[]> tweetList = new ArrayList<int[]>();
        ArrayList<String> inputArray = new ArrayList<String>();
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));

        String curLine = "";
        while ((curLine = fileReader.readLine()) != null) {
            // System.out.println(curLine);
            if (!curLine.equals(",,") && !curLine.equals("")) {
                inputArray.add(curLine.replaceAll("’", "'")); // .replaceAll("“", "\"").replaceAll("", "\"")
            }
        }

        for (int i = 0; i < inputArray.size(); i++) {

            int[] line = new int[3];
            line[0] = Integer.parseInt(inputArray.get(i).substring(0, 1));
            boolean cat2 = inputArray.get(i).substring(2, inputArray.get(i).length() - 2).equals("");
            if (!cat2){
                line[1] = Integer.parseInt(inputArray.get(i).substring(2, inputArray.get(i).length() - 2));
            }else{
                line[1] = -1;
            }
            line[2] = Integer.parseInt(inputArray.get(i).substring(inputArray.get(i).length() - 1));

            tweetList.add(line);
        }

        fileReader.close();

        return tweetList;
    }

    // compare keys
    static void compare(ArrayList<int[]> output, ArrayList<int[]> correct, String outputFile)
            throws IOException {

        String out = "";
        int cCounter = 0;
        int dCounter = 0;
        int total = output.size();

        ArrayList<String> inputArray = new ArrayList<String>();
        BufferedReader fileReader = new BufferedReader(new InputStreamReader (new FileInputStream(outputFile), "UTF8"));

        String curLine  = fileReader.readLine();
        while ((curLine  = fileReader.readLine()) != null){
            
            if (!curLine.equals(",,") &&  !curLine.equals("")){
                inputArray.add(curLine.replaceAll("’", "'")); //.replaceAll("“", "\"").replaceAll("", "\"")
            }
        }
        fileReader.close();

        for (int i = 0; i < total; i++) {
            out += inputArray.get(i) + "," + correct.get(i)[0] + ",";
            out += correct.get(i)[1] == -1 ? "" : correct.get(i)[1];
            out += "," + correct.get(i)[2];
            System.out.println(i +" : " + output.get(i)[0] + "," + output.get(i)[1] + " vs " +correct.get(i)[0] +"/" + correct.get(i)[1] + "," + correct.get(i)[2]);
            if ((output.get(i)[0] == correct.get(i)[0]) || (output.get(i)[0] == correct.get(i)[1])) {
                cCounter++;
                out += ",1";
            }else{
                out += ",0";
            }
            if (output.get(i)[1] == correct.get(i)[2]) {
                dCounter++;
                out += ",1";
            }else{
                out += ",0";
            }
            out += "\n";
        }
        
        out += ",,,,,," + cCounter + "," + dCounter + "\n";
        out += ",,,,,," + ((float)cCounter/total) + "," + ((float)dCounter/total) + "\n";
        out += ",,,,,," + ((float)(cCounter + dCounter)/(total*2)) + "\n";

        writeFile(out);
    }

    static void writeFile(String out) {

        try {
            File file = new File("../correctedOutput.csv");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter outputFile = new FileWriter(file);

            outputFile.write(out);

            outputFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        String outputName = "../output.csv";
        String correctName = "../newVals.csv";
        ArrayList<int[]> output = new ArrayList<int[]>();
        ArrayList<int[]> correct = new ArrayList<int[]>();

        try {
            output = getOut(outputName);
            correct = getKeys(correctName);
            compare(output, correct, outputName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}