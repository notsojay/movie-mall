package com.db.xmlParser.workers;

import com.models.MovieEntity;
import com.models.StarEntity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.db.DatabaseManager.execDbUpdate;
import static com.db.DatabaseManager.getDirectDatabaseConnection;

public class CastParseRunnable implements Runnable {
    private final String xmlFilePath;
    private final Map<String, StarEntity> sharedActorMap;
    private final Map<String, MovieEntity> sharedMovieMap;
    private int starsInMoviesCount;
    private final CountDownLatch latch;
    private final String INSERT_INTO_STARS_IN_MOVIES = """
            INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?);
            """;

    public CastParseRunnable(String xmlFilePath, Map<String, MovieEntity> sharedMovieMap, Map<String, StarEntity> sharedActorMap, CountDownLatch latch) {
        this.xmlFilePath = xmlFilePath;
        this.sharedMovieMap = sharedMovieMap;
        this.sharedActorMap = sharedActorMap;
        this.latch = latch;
        this.starsInMoviesCount = 0;
    }

    @Override
    public void run() {
        try (Connection connection = getDirectDatabaseConnection(
                "jdbc:mysql://localhost:3306/moviedb",
                "cs122b",
                "pwd_is_team_NO_1");
             InputStream inputStream = new FileInputStream(xmlFilePath);
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1)) {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(new InputSource(reader), new DefaultHandler() {
                private StringBuilder elementValue;
                private String currentMovieKey;
                private String currActorKey;

                {
                    currentMovieKey = null;
                    currActorKey = null;
                    elementValue = new StringBuilder();
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementValue.setLength(0);
                    switch (qName.toLowerCase()) {
                        case "m" -> {
                            currentMovieKey = null;
                            currActorKey = null;
                        }
                        case "filmc" -> currentMovieKey = null;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    elementValue.append(ch, start, length);
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    String value = elementValue.toString().trim();
                    try {
                        switch (qName.toLowerCase()) {
                            case "m" -> insertMovieActorRelationsToDb();
                            case "f" -> currentMovieKey = value;
                            case "a" -> currActorKey = value;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Error processing element " + qName + ": " + e.getMessage());
                    } finally {
                        elementValue.setLength(0);
                    }
                }

                private void insertMovieActorRelationsToDb() throws SQLException {
                    if (currentMovieKey == null || currActorKey == null) return;

                    String movieID = Optional.ofNullable(sharedMovieMap.get(currentMovieKey))
                            .map(MovieEntity::getMovieId)
                            .orElse(null);

                    String starID = Optional.ofNullable(sharedActorMap.get(currActorKey))
                            .map(StarEntity::getStarID)
                            .orElse(null);

                    if (movieID == null || starID == null) {
                        return;
                    }

                    AtomicInteger updateCount = new AtomicInteger();
                    execDbUpdate(
                            connection,
                            INSERT_INTO_STARS_IN_MOVIES,
                            updateCount::addAndGet,
                            starID,
                            movieID
                    );
                    if (updateCount.get() == 1) ++starsInMoviesCount;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Insert " + starsInMoviesCount + " stars_in_movies.");
            latch.countDown();
        }
    }
}