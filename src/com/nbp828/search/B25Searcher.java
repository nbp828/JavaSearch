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
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

public class B25Searcher {

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

            results = this.doPagingSearch(searcher, query, 100);

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
                                      int hitsPerPage) throws IOException {

        // Collect enough docs to show 1 pages
        TopDocs results = searcher.search(query, hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;
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
                retResults[index] = result;
                index++;
            }
        }

        return retResults;
    }
}
