package com.terraformfileanalyzer;

import java.util.HashMap;

public class App {

    public static void main(String[] args) throws Exception {
        //This program assumes that the follong commands was already executed:
        //terraform plan -out plan_example.bin
        TFParser parser=new TFParser();
        String planJson = parser.ToJson("plan_example.json", "show");
        HashMap<String, Change> hmFindings = parser.GetPlannedChanges(planJson);
        //Then the folliwong command must be executed: terraform apply plan_example.bin
        String stateJson = parser.ToJson("terraform.tfstate", "show");
        parser.PopulateWithIDs(stateJson, hmFindings);        
    }



}