package com.nbp828.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;

public class StopWordRemover {

    HashSet<String> stopWords = new HashSet<>();
    HashSet<Character> stopChars;

    public StopWordRemover(String filepath){
        try {

            File file = new File(filepath);
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);

            String st;
            while ((st = br.readLine()) != null) {
                st = st.trim();
                this.stopWords.add(st);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

        // Add chars
        Character arr[] = { '+', '/', '-', '*', '`', '\'', '\\', '\"', '!', '?', ',', '.', '\n', '\r' };
        // add ()
        this.stopChars = new HashSet<>(Arrays.asList(arr));

    }

    public String RemoveStopWords(String content){

        // remove punctuation
        StringBuilder sb = new StringBuilder();
        for (char c : content.toCharArray()){
            if (!this.stopChars.contains(c)) {
                sb.append(c);
            }
            else {
                sb.append(' ');
            }
        }

        // remove words
        String spaceString = sb.toString();
        String[] words = spaceString.split("\\s+");
        sb = new StringBuilder();
        for (String word : words){
            if (!this.stopWords.contains(word)){
                sb.append(word);
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}
