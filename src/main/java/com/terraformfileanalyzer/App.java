package com.terraformfileanalyzer;

import java.io.File;
import java.util.HashMap;

public class App {

    public static void main(String[] args) throws Exception {
        TFParser parser = new TFParser();
        FileManager objFile = new FileManager();

        String fileProvided = "plan_example.json";
        if (args.length > 0) {
            fileProvided = args[0];
        }

        String summaryFilePath = "summary.json";
        if (args.length > 1) {
            summaryFilePath = args[1];
        }

        System.out.println("TF File Analyzer");
        System.out.println("Using source [" + fileProvided + "] and target [" + summaryFilePath + "] ...");
        File file = new File(fileProvided);
        if (!file.exists()) {
            System.out.println("Source [" + fileProvided + "] does not exist.");
            return;
        }

        try {
            if (!fileProvided.toLowerCase().endsWith(".tfstate")) {
                String planJson = parser.ToJson(fileProvided, "show");
                HashMap<String, Change> hmFindings = parser.GetPlannedChanges(planJson);
                objFile.Write(hmFindings, summaryFilePath);

            } else {
                HashMap<String, Change> hmFindings = objFile.Read(summaryFilePath);
                String stateJson = parser.ToJson(fileProvided, "show");
                parser.PopulateWithIDs(stateJson, hmFindings);
                objFile.Write(hmFindings, summaryFilePath);
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

}