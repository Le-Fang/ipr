package edu.ucla.cs.grafter.instrument;

import edu.ucla.cs.grafter.instrument.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class InstruThread extends Thread {
    String filePath;
    int linenumber;
    String patch;
    String testName;
    String destination;
    String moduleName;
    String methodName;
    String directoryPath;
    int i;
    boolean result;

    public boolean getResult() {
        return this.result;
    }

    public InstruThread(String filePath, int linenumber, String patch, String testName, String destination,
            String moduleName, String methodName, String directoryPath, int i) {
        this.filePath = filePath;
        this.linenumber = linenumber;
        this.patch = patch;
        this.testName = testName;
        this.destination = destination;
        this.moduleName = moduleName;
        this.methodName = methodName;
        this.directoryPath = directoryPath;
        this.i = i;
    }

    public void instruTrace() {
        Scanner sc;
        try {
            sc = new Scanner(new File("F:\\CSVDemo.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        sc.useDelimiter(","); // sets the delimiter pattern
        while (sc.hasNext()) {
            String[] list = sc.nextLine().split(",");
        }
    }

    public void run() {

        // read execution traces from a txt file, these execution traces do not include
        // our target file
        ArrayList<String> traces = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File("/Users/eddiii/Desktop/courses/ipr/IPRTraces.txt"));
            while (sc.hasNext()) {
                // System.out.println(sc.nextLine());
                traces.add(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IPRTraecs txt file unreadable");
        }

        // for each of the trace info, we want to insert print statements
        for (String each : traces) {
            String path = each.split(",")[0];
            int lineN = Integer.valueOf(each.split(",")[1]);
            path = path.replaceFirst(directoryPath, directoryPath + "IPR/" + Integer.toString(i));
            CloneInstrument.preprocessTrace(path, lineN);
        }

        this.result = CloneInstrument.showDiff(filePath, linenumber, patch, testName, destination, moduleName,
                methodName);

        // pass our return values back to CloneInstrument
        CloneInstrument.success.set(i, result);

        // copy our test results to a more obvious location
        try {
            org.apache.commons.io.FileUtils.copyFile(new File(
                    filePath.substring(0, filePath.lastIndexOf("/") + 1) + "iprOutput.txt"),
                    new File(directoryPath + "IPR/" + "iprOutput" + Integer.toString(i) + ".csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
