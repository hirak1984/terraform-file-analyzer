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
        } catch (Exception ex) {

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

    public String ToJson(String fileName, String flag) {
        Runtime rt = Runtime.getRuntime();
        List<String> arrParams = new ArrayList<String>();
        arrParams.add("terraform");
        arrParams.add(flag);
        arrParams.add("-json");
        if (fileName != null) {
            arrParams.add(fileName);
        }

        Process proc;
        try {
            proc = rt.exec(arrParams.toArray(new String[0]));
        } catch (IOException e1) {
            return null;
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String temp;
        try {
            do {
                temp = stdInput.readLine();
                if (temp == null)
                    break;
                sb.append(temp);
            } while (true);
        } catch (IOException e) {
        }

        return sb.toString();
    }

    public HashMap<String, Change> GetPlannedChanges(String fileContent) {
        JSONObject json = GetJsonObj(fileContent);
        String createAction = "create";
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
                        hmFindings.put(changeEntry.getAddress(),
                                new Change(changeEntry.getAddress(), changeEntry.getType(), changeEntry.getAction()));
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
                                for (int m = 0; m < actionsArr.size(); m++) {
                                    String tmpAction = (String) actionsArr.get(m);
                                    if (tmpAction.compareTo(createAction) == 0) {
                                        changeEntry.setAction(createAction);
                                        continue;
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
