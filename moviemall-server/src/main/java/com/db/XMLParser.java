package com.db;

import com.models.MovieEntity;
import com.models.StarEntity;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XMLParser extends DefaultHandler {
    private Map<String, StarEntity> sharedActorMap;
    private Map<String, MovieEntity> sharedMovieMap;
    private final CountDownLatch latch = new CountDownLatch(3);

    public XMLParser() {
        sharedActorMap = Collections.synchronizedMap(new HashMap<>());
        sharedMovieMap = Collections.synchronizedMap(new HashMap<>());
    }

    public void start(String moviesFilePath, String castsFilePath, String actorsFilePath) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {

            executorService.execute(new MovieParserRunnable(moviesFilePath, sharedMovieMap, latch));
            executorService.execute(new ActorParseRunnable(actorsFilePath, sharedActorMap, latch));
            executorService.execute(new CastParseRunnable(castsFilePath, sharedMovieMap, sharedActorMap, latch));

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            executorService.shutdown();
        }
    }


    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java XMLParser <path-to-xml-file> <path-to-xml-file> <path-to-xml-file>");
            return;
        }
        XMLParser parser = new XMLParser();
        parser.start(args[0], args[1], args[2]);
    }
}

//
//    @Override
//    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        elementValue.setLength(0); // Clear the StringBuilder for new element value
//        switch (qName.toLowerCase()) {
//            case "directorfilms":
//                currentDirector = new Director();
//                break;
//            case "film":
//                currentFilm = new Film();
//                break;
//            // No need to create instances for other elements
//            // They will be processed in endElement method based on flags
//        }
//    }
//
//    @Override
//    public void characters(char[] ch, int start, int length) {
//        elementValue.append(ch, start, length); // Collect the characters for this element
//    }
//
//    @Override
//    public void endElement(String uri, String localName, String qName) {
//        String value = elementValue.toString().trim();
//        switch (qName.toLowerCase()) {
//            case "directorfilms":
//                directors.add(currentDirector);
//                break;
//            case "dirid":
//                currentDirector.setDirId(value);
//                break;
//            case "dirstart":
//                currentDirector.setDirStart(value);
//                break;
//            case "dirname":
//                currentDirector.setDirName(value);
//                break;
//            case "coverage":
//                currentDirector.setCoverage(value);
//                break;
//            case "film":
//                currentDirector.addFilm(currentFilm);
//                break;
//            case "fid":
//                currentFilm.setFid(value);
//                break;
//            case "t":
//                currentFilm.setTitle(value);
//                break;
//            case "year":
//                currentFilm.setYear(value);
//                break;
//            // Add cases for other elements as necessary
//            // Example for nested elements:
//            case "dirs":
//                // Here you can add logic to handle nested elements
//                // E.g., if there are multiple directors for a film, handle them here
//                break;
//            // Continue with other tags
//        }
//    }

