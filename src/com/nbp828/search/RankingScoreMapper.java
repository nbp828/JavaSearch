package com.nbp828.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class RankingScoreMapper {
    public HashMap<String, Float> GetRankScoreMap(String filepath)
    {
        HashMap<String, Float> retMap = new HashMap<>();

        try {

            File file = new File(filepath);
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);

            String st;
            while ((st = br.readLine()) != null) {

                String[] tokens = st.split(",", 2);
                String id = tokens[0];
                float content = Float.parseFloat(tokens[1]);
                content += 1.1f;
                float score = (float)Math.log10(content) / 3.0f;
                retMap.put(id, score);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        return retMap;
    }
}
