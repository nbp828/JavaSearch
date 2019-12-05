package com.nbp828.search;

import java.io.*;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        // Read the doc folder
        // Docs = "/Users/neilpatel/Project/SearchEngine/SearchLucene/Docs/";

        // Filter Docs
        DocumentFilter filter = new DocumentFilter();
        String stopWordFile = "/Users/neilpatel/Project/SearchEngine/SearchEngineData/Assignment_initial_datasets/" +
                "General_data/lemur-stopwords.txt";
        String inDir = "/Users/neilpatel/Project/SearchEngine/SearchLucene/AcademicDocs/";
        String outDir = "/Users/neilpatel/Project/SearchEngine/AcademicDocs/";

        //!filter.RemoveStopWords(stopWordFile, inDir, outDir);

        // Create an index
        String indexPath = "../" + "General_Index";
        Indexer indexer = new Indexer();
        //!indexer.createIndex(indexPath, outDir);


        // Read the query file
        //String queryPath = "/Users/neilpatel/Project/SearchEngine/SearchEngineData/Assignment_initial_datasets/" +
        //        "Academic_papers/test_queries.txt"; // here
        String queryPath = "/Users/neilpatel/Project/SearchEngine/SearchEngineData/Assignment_initial_datasets/" +
                "General_data/testqueries2.txt"; // here

        QueryReader queryReader = new QueryReader();
        HashMap<String, String> queryMap = queryReader.Read(queryPath);

        String generalDocPath = "/Users/neilpatel/Project/SearchEngine/Search_engine_competition/" +
                "document_id_mapping_gen_data.txt"; // here
        DocumentMappingReader docMapper = new DocumentMappingReader();
        HashMap<String, Integer> docGenMap = docMapper.CreateMapping(generalDocPath);

        // Search all queries with index
        B25Searcher searcher = new B25Searcher("/Users/neilpatel/Project/SearchEngine/" +
                "SearchEngineData/Assignment_initial_datasets/General_data/trainTestMap.txt");
        Result[][] resultArr = new Result[queryMap.size()][docGenMap.size()];

        int index = 0;
        for (String s : queryMap.keySet())
        {
            Result[] values = searcher.Search(indexPath, queryMap.get(s), s);
//            for (Result value : values){
//                value.score += rankmap.get(value.docId);
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
}
