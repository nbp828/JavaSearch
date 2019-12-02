package com.nbp828.search;

import java.io.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        // Read the doc folder
        // Academic Docs = "/Users/neilpatel/Project/SearchEngine/SearchLucene/Docs/"
        // Create an index
        String indexPath = "../" + "Academic_Index";
        //Indexer indexer = new Indexer();
        //indexer.createIndex(indexPath, "/Users/neilpatel/Project/SearchEngine/SearchLucene/Docs/");

        // Read the query file
        String queryPath = "/Users/neilpatel/Project/SearchEngine/SearchEngineData/Assignment_initial_datasets/General_data/testqueries2.txt";
        QueryReader queryReader = new QueryReader();
        HashMap<String, String> queryMap = queryReader.Read(queryPath);

        String generalDocPath = "/Users/neilpatel/Project/SearchEngine/Search_engine_competition/document_id_mapping_gen_data.txt";
        DocumentMappingReader docMapper = new DocumentMappingReader();
        HashMap<String, Integer> docGenMap = docMapper.CreateMapping(generalDocPath);

        // Search all queries with index
        B25Searcher searcher = new B25Searcher();
        Result[][] resultArr = new Result[queryMap.size()][100];

        int index = 0;
        for (String s : queryMap.keySet())
        {
            Result[] values = searcher.Search(indexPath, queryMap.get(s), s);
//            for (Result value : values){
//                Integer item = docGenMap.get(value.docId);
//                String itemString = item.toString();
//                value.docId = itemString;
//            }
            resultArr[index] = values;
            index++;
        }

        // Write to result
        // <queryid>\t<docid>\t<score>
        // /Users/neilpatel/Project/SearchEngine/Search_engine_competition/results/Academic_domain_results.txt
        String resultPath = "../" + "results/General_domain_results.txt";

        File file = new File(resultPath);
        try
        {
            if (file.exists())
            {
                file.delete();
            }
            boolean created = file.createNewFile();
            System.out.println("New file created? " + created);

            WriteResultsToFile(resultArr, resultPath);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }


    }

    private static void WriteResultsToFile(Result[][] resultsArr, String fileName)
            throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (Result[] results : resultsArr){
            try
            {
                for (Result result : results)
                {
                    if (result == null)
                    {
                        System.out.println("No search result");
                        continue;
                    }

                    String resultString = result.queryId + '\t' + result.docId + '\t' + result.score + "\r\n";
                    writer.append(resultString);
                    System.out.println(resultString);
                }
            }
            catch (IOException e)
            {
                System.out.println(e.toString());
            }
        }

        writer.close();
    }

    private static HashMap<Integer, String> CreateMapping(String filepath)
    {
        // /Users/neilpatel/Project/SearchEngine/Search_engine_competition/document_id_mapping_gen_data.txt

        return null;
    }
}
