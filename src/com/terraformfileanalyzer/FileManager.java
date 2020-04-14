package com.terraformfileanalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.Gson;

public class FileManager {

    public void Write(HashMap<String, Change> hmChanges, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("'changes':[");
        int count = 0;

        for (Map.Entry<String, Change> entry : hmChanges.entrySet()) {
            sb.append("{");

            Change objChange = entry.getValue();
            sb.append("'action':'" + objChange.getAction() + "',");
            sb.append("'address':'" + objChange.getAddress() + "',");
            sb.append("'id':'" + objChange.getId() + "',");
            sb.append("'type':'" + objChange.getType() + "'");

            count++;
            if (hmChanges.size() == count) {
                sb.append("}");
            } else {
                sb.append(",}");
            }
        }

        sb.append("]");
        sb.append("}");

        try {
            File file = new File(fileName);           
            file.delete();

            FileWriter myWriter = new FileWriter(fileName, false);
            myWriter.write(sb.toString().replace("'", "\""));
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public HashMap<String, Change> Read(String fileName) {
        StringBuilder sb = new StringBuilder();

        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                sb.append(myReader.nextLine());
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String jsonContent = sb.toString();
        Gson g = new Gson();
        ChangeFile changeFile = g.fromJson(jsonContent, ChangeFile.class);
        List<Change> list = changeFile.getChanges();

        HashMap<String, Change> hmChanges = new HashMap<String, Change>();
        for (int i = 0; i < list.size(); i++) {            
            Change newObj = new Change();
            Change curObj = list.get(i);
            newObj.setAction(curObj.getAction());
            newObj.setAddress(curObj.getAddress());
            newObj.setId(curObj.getId());
            newObj.setType(curObj.getType());
            hmChanges.put(curObj.getAddress(), newObj);
        }
        return hmChanges;
    }

}
