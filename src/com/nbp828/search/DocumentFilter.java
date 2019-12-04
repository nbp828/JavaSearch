package com.nbp828.search;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class DocumentFilter {

    public void RemoveStopWords(String stopWordFilePath, String inputDirPath, String outputDirPath)
    {
        File outDir = new File(outputDirPath);
        if (outDir.exists())
        {
            String[]entries = outDir.list();
            for(String s: entries){
                File currentFile = new File(outDir.getPath(),s);
                currentFile.delete();
            }

            if(outDir.delete()) {
                outDir.mkdir();
            }
            else {
                throw new UnsupportedOperationException("Failed to create a folder");
            }
        }
        else
        {
            outDir.mkdir();
        }

        StopWordRemover remover = new StopWordRemover(stopWordFilePath);
        File inFolder = new File(inputDirPath);

        String[] files = inFolder.list();
        try
        {
            for (String filepath : files) {

                File file = new File(inFolder.getPath() + '/' + filepath);
                String entireFileText = new Scanner(file)
                        .useDelimiter("\\A").next();
                String inString = entireFileText.toLowerCase();
                String outString = remover.RemoveStopWords(inString);
                String outFilePath = outputDirPath + file.getName();
                Files.write(Paths.get(outFilePath), outString.getBytes());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }

    }

}
