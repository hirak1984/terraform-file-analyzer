package com.terraformfileanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TFParser {

    private JSONObject GetJsonObj(String fileContent) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(fileContent);

        } catch (Exception e) {
            System.out.println("[TFParser.TFParser] An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return json;
    }

    public void PopulateWithIDs(String fileContent, HashMap<String, Change> hmFindings) {
        JSONObject json = GetJsonObj(fileContent);
        JSONArray resources = (JSONArray) ((JSONObject) ((JSONObject) json.get("values")).get("root_module"))
                .get("resources");
        Iterator outsIt = resources.iterator();

        while (outsIt.hasNext()) {
            JSONObject resourceType = (JSONObject) outsIt.next();
            JSONObject resourceAttsArr = (JSONObject) resourceType.get("values");
            String address = resourceType.get("address").toString();

            Iterator attsIt = resourceAttsArr.entrySet().iterator();
            while (attsIt.hasNext()) {
                Entry resourceMetadata = (Entry) attsIt.next();
                if (resourceMetadata.getKey().toString().compareTo("id") == 0) {
                    String instanceId = resourceMetadata.getValue().toString();
                    if (hmFindings.containsKey(address)) {
                        Change tmpObj = hmFindings.get(address);
                        tmpObj.setId(instanceId);
                        hmFindings.put(address, tmpObj);
                        break;
                    }
                }
            }

        }

    }

    //Function to invoke the terraform CLI
    public String ToJson(String fileName, String flag, String terraformExecFile) throws IOException 
    {

        //TODO: add terraform init when needed 
        //Process initProc = Runtime.getRuntime().exec(new String[] {terraformExecFile, "init"});
        //BufferedReader initInput = new BufferedReader(new InputStreamReader(initProc.getInputStream()));

        Process proc = Runtime.getRuntime().exec(new String[] {terraformExecFile, flag, "-json", fileName});
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        
        BufferedReader error = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String line = null;
        System.out.println("Error:");
        while ((line = error.readLine()) != null) {
            System.out.println(line);
        }        

        StringBuilder sb = new StringBuilder();
        String temp;

        do {
            temp = stdInput.readLine();
            if (temp == null)
                break;
            sb.append(temp);
        } while (true);
        return sb.toString();
    }

    public HashMap<String, Change> GetPlannedChanges(String fileContent) throws Exception {
        JSONObject json = GetJsonObj(fileContent);
        JSONArray changesArr = (JSONArray) json.get("resource_changes");
        HashMap<String, Change> hmFindings = new HashMap<String, Change>();

        for (int i = 0; i < changesArr.size(); i++) {
            HashMap rootHm = (HashMap) (JSONObject) changesArr.get(i);

            if (rootHm != null) {
                Iterator iterator = rootHm.entrySet().iterator();
                Change changeEntry = null;

                while (iterator.hasNext()) {
                    Entry tmpMetadataObj = (Entry) iterator.next();
                    String metaDataKey = tmpMetadataObj.getKey().toString();

                    if (metaDataKey.compareTo("address") == 0) {
                        changeEntry = new Change();
                        changeEntry.setAddress(tmpMetadataObj.getValue().toString());
                        continue;
                    }

                    if (metaDataKey.compareTo("type") == 0) {
                        String metaDataValue = tmpMetadataObj.getValue().toString();
                        changeEntry.setType(metaDataValue);
                        // We have all info now, ready to store
                        hmFindings.put(changeEntry.getAddress(), new Change(changeEntry.getAddress(),
                                changeEntry.getType(), changeEntry.getAction(), changeEntry.getId()));
                        changeEntry = null;
                        continue;
                    }

                    if (metaDataKey.compareTo("change") == 0) {
                        HashMap actionObj = (HashMap) tmpMetadataObj.getValue();
                        Iterator actionIt = actionObj.entrySet().iterator();

                        while (actionIt.hasNext()) {
                            Entry actionTmp = (Entry) actionIt.next();

                            if (actionTmp.getKey().toString().compareTo("actions") == 0) {
                                JSONArray actionsArr = (JSONArray) actionTmp.getValue();
                                if (actionsArr.size() == 1) {
                                    String tmpAction = (String) actionsArr.get(0);
                                    changeEntry.setAction(tmpAction);
                                } else {
                                    throw new Exception("Multiple changes (actions) are not supported");
                                }
                            } else {
                                if (actionTmp.getKey().toString().compareTo("before") == 0) {
                                    JSONObject beforeIdObj = (JSONObject) actionTmp.getValue();
                                    if (beforeIdObj != null) {
                                        changeEntry.setId(beforeIdObj.get("id").toString());
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }

        return hmFindings;
    }

}
