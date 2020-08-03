import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * JsonToCsv
 */
public class JsonToCsv {

    // read file
    static void getJSON(String filename) throws IOException {


        String input = "tweet,category,disaster\n";
        BufferedReader fileReader = new BufferedReader(new InputStreamReader (new FileInputStream(filename), "UTF8"));

        String curLine  = "";
        while ((curLine  = fileReader.readLine()) != null){
            
            if (curLine.contains("\"text\":") && curLine.substring(8, 14).contains("text")){
                
                if (curLine.contains("RT @")){
                    curLine = curLine.replace("\"text\":", "");
                }
                curLine = curLine.substring(curLine.indexOf(':', 0) + 2,curLine.length()-2).replace("\"", "").replace(",", ",");

                
                if (!input.contains(curLine)){
                    input += "\"" + curLine + "\"\n"; 
                }
            }
        }
        System.out.println("here");
        

        System.out.println(input);
        fileReader.close();

        writeFile(input, filename);
    }

    //write file
    static void writeFile(String out, String name) {

        try {
            File file = new File("../input.csv");

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

    public static void main(String[] args) throws IOException {
        getJSON("../twitterapi/result.json");
    }
}