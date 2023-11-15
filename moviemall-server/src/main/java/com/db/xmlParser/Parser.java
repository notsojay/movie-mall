package com.db.xmlParser;

import com.db.xmlParser.workers.CastParseRunnable;
import com.db.xmlParser.workers.MovieParserRunnable;
import com.db.xmlParser.workers.StarParseRunnable;
import com.models.MovieEntity;
import com.models.StarEntity;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.utils.FileUtils.clearDirectory;

public class Parser extends DefaultHandler {
    private final Map<String, StarEntity> sharedStarMap;
    private final Map<String, MovieEntity> sharedMovieMap;

    public Parser() {
        sharedStarMap = new ConcurrentHashMap<>();
        sharedMovieMap = new ConcurrentHashMap<>();
    }

    public void start(String moviesFilePath, String actorsFilePath, String castsFilePath) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {

            CountDownLatch latch = new CountDownLatch(2);
            executorService.execute(new MovieParserRunnable(moviesFilePath, sharedMovieMap, latch));
            executorService.execute(new StarParseRunnable(actorsFilePath, sharedStarMap, latch));
            awaitLatch(latch);

            latch = new CountDownLatch(1);
            executorService.execute(new CastParseRunnable(castsFilePath, sharedMovieMap, sharedStarMap, latch));
            awaitLatch(latch);

            executorService.shutdown();
        }
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Parser <path-to-xml-file> <path-to-xml-file> <path-to-xml-file>");
            return;
        }
        Parser parser = new Parser();
        clearDirectory("result_output/xml_parser");
        parser.start(args[0], args[1], args[2]);
    }
}