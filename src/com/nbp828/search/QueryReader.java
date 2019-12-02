package com.nbp828.search;

import java.io.*;
import java.util.HashMap;

public class QueryReader {
    public HashMap<String, String> Read(String filepath){
        HashMap<String, String> retMap = new HashMap<>();

        try {

            File file = new File(filepath);
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);

            String st;
            while ((st = br.readLine()) != null) {

                String[] tokens = st.split(",", 2);
                String id = tokens[0];
                String content = tokens[1];
                retMap.put(id, content);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        return retMap;
    }

}
