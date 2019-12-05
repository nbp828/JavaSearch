package com.nbp828.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class B25Searcher {

    private HashMap<String, HashMap<String, Double[]>> supMap;

    public B25Searcher(String supfilepath)
    {
        supMap = new HashMap<>();

        try {

            File file = new File(supfilepath);
            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);

            String st;
            while ((st = br.readLine()) != null) {

                String[] tokens = st.split(",", 4);
                String qid = tokens[0];
                String did = tokens[1];
                Double sim = Double.parseDouble(tokens[2]);
                Double rel = Double.parseDouble(tokens[3]);


                if (!supMap.containsKey(qid))
                {
                    supMap.put(qid, new HashMap<>());
                }

                HashMap<String, Double[]> valueMap = supMap.get(qid);

                if (!valueMap.containsKey(did))
                {
                    valueMap.put(did, new Double[2]);
                }

                Double[] scores = valueMap.get(did);
                scores[0] = sim;
                scores[1] = rel;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
    }

    public Result[] Search(String indexPath, String queryString, String queryId){
        Result[] results = null;

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new BM25Similarity());
            Analyzer analyzer = new StandardAnalyzer();
            String field = "contents";
            QueryParser parser = new QueryParser(field, analyzer);

            queryString = queryString.trim();
            if (queryString.length() == 0) {
                return null;
            }

            Query query = parser.parse(queryString);
            System.out.println("Searching for: " + query.toString(field));

            results = this.doPagingSearch(searcher, query, 100, queryId);

            for (Result result : results) {
                result.queryId = queryId;
            }
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        return results;
    }

    private Result[] doPagingSearch(IndexSearcher searcher, Query query,
                                      int hitsPerPage, String queryId) throws IOException {

        // Ranking for Academic
//        RankingScoreMapper rankingMapper = new RankingScoreMapper();
//        HashMap<String, Float> rankmap = rankingMapper.GetRankScoreMap(
//                "/Users/neilpatel/Project/SearchEngine/SearchLucene/AcademicRankMap.txt");

        // Supervised Learning


        // Collect enough docs to show 1 pages
        TopDocs results = searcher.search(query, hitsPerPage * 2);
        ScoreDoc[] hits = results.scoreDocs;
        Result[] tempResults = new Result[200];
        Result[] retResults = new Result[100];
        int index = 0;
        if (hits.length > 0) {
            for (ScoreDoc hit : hits)
            {
                Result result = new Result();
                result.score = hit.score;
                Document doc = searcher.doc(hit.doc);
                String path = doc.get("path");
                String[] dirs = path.split("/");
                result.docId = dirs[dirs.length -1];
                tempResults[index] = result;
                //retResults[index] = result;
//                if (rankmap.containsKey(result.docId))
//                {
//                    result.score += rankmap.get(result.docId);
//                }
//                else
//                {
//                    System.out.println("There is no ranking for: " + result.docId);
//                }

                index++;
            }
        }

        // If doc_id is in training
        for (Result r : tempResults)
        {
            if (supMap.containsKey(queryId))
            {
                if (supMap.get(queryId).containsKey(r.docId))
                {
                    double sim = supMap.get(queryId).get(r.docId)[0];
                    double rel = supMap.get(queryId).get(r.docId)[1];
                    r.score += Math.pow(10.0,(sim + Math.log(rel)/10.0));
                }
            }
        }


        // Add the top 100
        Arrays.sort(tempResults, new SortByScore());
        for (int i = 0; i < 100; i++)
        {
            retResults[i] = tempResults[i];
        }

        return retResults;
    }
}

class SortByScore implements Comparator<Result>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Result a, Result b)
    {
        if (a.score < b.score)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
