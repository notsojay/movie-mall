package com.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.models.MovieEntity;
import org.json.JSONObject;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.db.DatabaseManager.*;

public class MovieParserRunnable implements Runnable {
    private String xmlFilePath;
    private Map<String, MovieEntity> sharedMovieMap;
    private CountDownLatch latch;
    private int insertedMoviesCount;
    private int duplicateMoviesCount;
    private int invalidMoviesCount;
    private String INSER_INTO_GENRES_IN_MOVIES = """
            INSERT INTO genres_in_movies (genreID, movieId) VALUES (?, ?);
            """;

    public MovieParserRunnable(String xmlFilePath, Map<String, MovieEntity> sharedMovieMap, CountDownLatch latch) {
        this.xmlFilePath = xmlFilePath;
        this.sharedMovieMap = sharedMovieMap;
        this.latch = latch;
        this.insertedMoviesCount = 0;
        this.duplicateMoviesCount = 0;
        this.invalidMoviesCount = 0;
    }

    @Override
    public void run() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(xmlFilePath, new DefaultHandler() {
                private Connection connection;
                private StringBuilder elementValue;
                private MovieEntity currentMovie;
                private String currentDirectorName;
                private Set<String> currentMovieGenres;
                private String currentFid;

                {
                    elementValue = new StringBuilder();
                    currentMovie = new MovieEntity();
                    currentMovieGenres = new HashSet<>();
                    currentDirectorName = null;
                    currentFid = null;
                    connection = getDirectDatabaseConnection(
                            "jdbc:mysql://localhost:3306/moviedb",
                            "cs122b",
                            "pwd_is_team_NO_1");

                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementValue.setLength(0); // Clear the StringBuilder for new element value
                    switch (qName.toLowerCase()) {
                        case "directorfilms":
                            currentDirectorName = null; // Reset the current director name at the start of a new directorfilms element
                            break;
                        case "film":
                            try {
                                insertMovieToDb();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            if (currentFid != null && currentMovie != null && currentMovie.getMovieId() != null) {
                                sharedMovieMap.put(currentFid, currentMovie);
                            }
                            currentMovie = new MovieEntity();
                            currentMovie.setDirector(currentDirectorName);
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    elementValue.append(ch, start, length);
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    String value = elementValue.toString().trim();
                    switch (qName.toLowerCase()) {
                        case "dirname" -> currentDirectorName = value;
                        case "film" -> {
                            currentMovie.setGenres(currentMovieGenres.stream().toList());
                            currentMovieGenres = new HashSet<>();
                        }
                        case "t" -> currentMovie.setTitle(value);
                        case "year" -> currentMovie.setYear(extractYear(value));
                        case "released" -> {
                            if (currentMovie.getYear() == null) {
                                currentMovie.setYear(extractYear(value));
                            }
                        }
                        case "cat" -> {
                            if (currentMovie.getGenres().size() == 0 && currentMovie.getMainGenre() == null) {
                                currentMovie.setMainGenre(value);
                            } else {
                                currentMovieGenres.add(value);
                            }
                        }
                        case "fid" -> currentFid = value;
                    }
                    elementValue.setLength(0); // Clear the StringBuilder after handling
                }

                private void insertMovieToDb() throws SQLException {
                    if (currentMovie == null || currentMovie.getTitle() == null || currentMovie.getYear() == null || currentMovie.getDirector() == null) {
                        ++insertedMoviesCount;
                        return;
                    }
                    String newMovieID = execDbProcedure(
                            connection,
                            "{CALL simple_add_movie(?, ?, ?, ?)}",
                            (stmt, hadResults) -> stmt.getString(4),
                            new Object[]{currentMovie.getTitle(), currentMovie.getYear(), currentMovie.getDirector(), null},
                            new int[]{Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR},
                            new boolean[]{false, false, false, true}
                    );
                    if (newMovieID == null) {
                        ++duplicateMoviesCount;
                        return;
                    }
                    currentMovie.setMovieId(newMovieID);
                    ++insertedMoviesCount;
                }

//                private void insertMovieGenresRelationsToDb() throws SQLException {
//                    if (currentMovie == null || currentMovie.getGenres() == null || currentMovie.getMovieId() == null) return;
//
//                    for (String genre : currentMovie.getGenres()) {
//                        AtomicInteger updateCount = new AtomicInteger();
//                        execDbUpdate(
//                                connection,
//                                INSER_INTO_GENRES_IN_MOVIES,
//                                updateCount::addAndGet,
//                                currentMovie.get,
//                                currentMovie.getMovieId()
//
//
//                        );
//
//                        if (updateCount.get() == 1) ++starsInMoviesCount;
//                    }
//                }

                private Integer extractYear(String yearString) {
                    if (yearString.length() < 4 || !yearString.matches("^\\d{4}$")) {
                        return null;
                    }
                    return Integer.parseInt(yearString.substring(0, 4));
                }

            });

            System.out.println(invalidMoviesCount + " movies inserted.");
            System.out.println(duplicateMoviesCount + " movie duplicated.");
            System.out.println(invalidMoviesCount + " movie invalid.");

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    }
}
