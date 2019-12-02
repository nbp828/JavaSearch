package com.nbp828.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class DocumentMappingReader {
    public HashMap<String, Integer> CreateMapping(String filepath){

        HashMap<String, Integer> retMap = new HashMap<>();

        try {

            File file = new File(filepath);
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);

            String st;
            while ((st = br.readLine()) != null) {

                String[] tokens = st.split(" ", 2);
                Integer id = Integer.parseInt(tokens[0]);
                String fileName = tokens[1];
                retMap.put(fileName, id);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        return retMap;
    }
}
