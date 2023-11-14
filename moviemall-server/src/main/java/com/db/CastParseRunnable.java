package com.db;

import com.models.MovieEntity;
import com.models.StarEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.db.DatabaseManager.execDbUpdate;
import static com.db.DatabaseManager.getDirectDatabaseConnection;

public class CastParseRunnable implements Runnable {
    private String xmlFilePath;
    private Map<String, StarEntity> sharedActorMap;
    private Map<String, MovieEntity> sharedMovieMap;
    private int starsInMoviesCount;
    private CountDownLatch latch;

    private String INSER_INTO_STARS_IN_MOVIES = """
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
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(xmlFilePath, new DefaultHandler() {
                private Connection connection;
                private StringBuilder elementValue;
                private String currentMovieKey;
                private String currActorKey;

                {
                    connection = getDirectDatabaseConnection(
                            "jdbc:mysql://localhost:3306/moviedb",
                            "cs122b",
                            "pwd_is_team_NO_1");
                    currentMovieKey = null;
                    currActorKey = null;
                    elementValue = new StringBuilder();
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementValue.setLength(0);
                    switch (qName.toLowerCase()) {
                        case "m":
                            try {
                                insertMovieActorRelationsToDb();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            currentMovieKey = null;
                            currActorKey = null;
                            break;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    elementValue.append(ch, start, length);
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    String value = elementValue.toString().trim();
                    switch (qName.toLowerCase()) {
                        case "f" -> currentMovieKey = value;
                        case "a" -> currActorKey = value;
                    }
                    elementValue.setLength(0);
                }

                private void insertMovieActorRelationsToDb() throws SQLException {
                    String movieID = sharedMovieMap.get(currentMovieKey).getMovieId();
                    String starID = sharedActorMap.get(currActorKey).getStarID();
                    if (movieID == null || starID == null) return;

                    AtomicInteger updateCount = new AtomicInteger();
                    execDbUpdate(
                            connection,
                            INSER_INTO_STARS_IN_MOVIES,
                            updateCount::addAndGet,
                            starID,
                            movieID
                    );
                    if (updateCount.get() == 1) ++starsInMoviesCount;
                }

            });
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    }
}
