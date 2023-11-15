package com.db.xmlParser.workers;

import com.models.MovieEntity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.db.DatabaseManager.*;
import static com.utils.ConversionUtils.extractYear;
import static com.utils.FileUtils.writeFile;

public class MovieParserRunnable implements Runnable {
    private final String xmlFilePath;
    private final Map<String, MovieEntity> sharedMovieMap;
    private final CountDownLatch latch;
    private int insertedMoviesCount;
    private int duplicateMoviesCount;
    private int invalidMoviesCount;
    private int insertGenresCount;
    private int genresInMoviesCount;
    private int movieWithNoStarsCount;
    private int movieWithNoGenresCount;
    private final String INSERT_INTO_GENRES_IN_MOVIES = """
            INSERT INTO genres_in_movies (genreID, movieId) VALUES (?, ?);
            """;

    public MovieParserRunnable(String xmlFilePath, Map<String, MovieEntity> sharedMovieMap, CountDownLatch latch) {
        this.xmlFilePath = xmlFilePath;
        this.sharedMovieMap = sharedMovieMap;
        this.latch = latch;
        this.insertedMoviesCount = 0;
        this.duplicateMoviesCount = 0;
        this.invalidMoviesCount = 0;
        this.insertGenresCount = 0;
        this.genresInMoviesCount = 0;
        this.movieWithNoStarsCount = 0;
        this.movieWithNoGenresCount = 0;
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
                private final StringBuilder elementValue;
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
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementValue.setLength(0); // Clear the StringBuilder for new element value
                    switch (qName.toLowerCase()) {
                        case "directorfilms" -> currentDirectorName = null; // Reset the current director name at the start of a new directorfilms element
                        case "film" -> {
                            currentFid = null;
                            currentMovie = new MovieEntity();
                            currentMovieGenres = new HashSet<>();
                            currentMovie.setDirector(currentDirectorName);
                        }
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
                            case "dirname" -> currentDirectorName = value;
                            case "film" -> {
                                currentMovie.setGenres(currentMovieGenres.stream().toList());
                                insertMovieToDb();
                                insertMovieGenresRelationsToDb();
                            }
                            case "t" -> currentMovie.setTitle(value);
                            case "year" -> currentMovie.setYear(extractYear(value));
                            case "released" -> {
                                if (currentMovie.getYear() == null) {
                                    currentMovie.setYear(extractYear(value));
                                }
                            }
                            case "cat" -> currentMovieGenres.add(value);
                            case "fid" -> currentFid = value;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Error processing element " + qName + ": " + e.getMessage());
                    } finally {
                        elementValue.setLength(0); // Clear the StringBuilder after handling
                    }
                }

                private void insertMovieToDb() throws SQLException, IOException {
                    if (isCurrMovieInvalid()) {
                        ++invalidMoviesCount;
                        return;
                    }
                    AbstractMap.SimpleEntry<String, Boolean> movieResult = execDbProcedure(
                            connection,
                            "{CALL simple_add_movie(?, ?, ?, ?, ?)}",
                            (stmt, hadResults) -> new AbstractMap.SimpleEntry<>(stmt.getString(4), stmt.getBoolean(5)),
                            new Object[]{currentMovie.getTitle(), currentMovie.getYear(), currentMovie.getDirector(), null},
                            new int[]{Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR},
                            new boolean[]{false, false, false, true}
                    );
                    currentMovie.setMovieId(movieResult.getKey());
                    if (isCurrMovieDuplicate(movieResult.getValue())
                        || isCurrMovieHasNoStar()) {
                        return;
                    }
                    sharedMovieMap.put(currentFid, currentMovie);
                    ++insertedMoviesCount;
                }

                private void insertMovieGenresRelationsToDb() throws SQLException, IOException {
                    if (currentMovie == null || currentMovie.getMovieId() == null || isCurrMovieHasNoGenre()) {
                        return;
                    }

                    for (String genre : currentMovie.getGenres()) {
                        AbstractMap.SimpleEntry<Integer, Boolean> genreResult = execDbProcedure(
                                connection,
                                "{CALL get_or_add_genre(?, ?, ?)}",
                                (stmt, hadResults) -> new AbstractMap.SimpleEntry<>(stmt.getInt(2), stmt.getBoolean(3)),
                                new Object[]{genre, null},
                                new int[]{Types.VARCHAR, Types.INTEGER},
                                new boolean[]{false, true}
                        );
                        if (genreResult.getKey() == null) continue;
                        if (genreResult.getValue() != null && genreResult.getValue()) ++insertGenresCount;

                        AtomicInteger updateCount = new AtomicInteger();
                        execDbUpdate(
                                connection,
                                INSERT_INTO_GENRES_IN_MOVIES,
                                updateCount::addAndGet,
                                genreResult.getKey(),
                                currentMovie.getMovieId()
                        );
                        if (updateCount.get() == 1) ++genresInMoviesCount;
                    }
                }

                private boolean isCurrMovieInvalid() throws IOException {
                    if (currentMovie != null && currentMovie.getTitle() != null && currentMovie.getYear() != null && currentMovie.getDirector() != null) {
                        return false;
                    }
                    ++invalidMoviesCount;
                    if (currentMovie == null) return true;
                    String content = buildMovieContent(currentMovie, currentFid);
                    writeFile("result_output/xml_parser", "inconsistent_movies.txt", content, true);
                    return true;
                }

                private boolean isCurrMovieHasNoStar() throws IOException {
                    if (currentFid != null && currentMovie != null) {
                        return false;
                    }
                    ++movieWithNoStarsCount;
                    String content = buildMovieContent(currentMovie, null);
                    writeFile("result_output/xml_parser", "no_stars_movies.txt", content, true);
                    return true;
                }

                private boolean isCurrMovieHasNoGenre() throws IOException {
                    if (currentMovie != null && currentMovie.getGenres() != null && !currentMovie.getGenres().isEmpty()) {
                        return false;
                    }
                    ++movieWithNoGenresCount;
                    String content = buildMovieContent(currentMovie, currentFid);
                    writeFile("result_output/xml_parser", "no_genres_movies.txt", content, true);
                    return true;
                }

                private boolean isCurrMovieDuplicate(Boolean isDuplicate) throws IOException {
                    if (currentMovie != null && currentMovie.getMovieId() != null && !isDuplicate) {
                        return false;
                    }
                    ++duplicateMoviesCount;
                    String content = buildMovieContent(currentMovie, currentFid);
                    writeFile("result_output/xml_parser", "duplicate_movies.txt", content, true);
                    return true;
                }

                private String buildMovieContent(MovieEntity movie, String fid) {
                    String title = Optional.ofNullable(movie)
                            .map(MovieEntity::getTitle)
                            .orElse("N/A");

                    String year = Optional.ofNullable(movie)
                            .map(MovieEntity::getYear)
                            .map(Object::toString)
                            .orElse("N/A");

                    String directorName = Optional.ofNullable(movie)
                            .map(MovieEntity::getDirector)
                            .orElse("N/A");

                    String movieID = Optional.ofNullable(movie)
                            .map(MovieEntity::getMovieId)
                            .orElse("N/A");

                    fid = Optional.ofNullable(fid).orElse("N/A");

                    String genresString = Optional.ofNullable(movie)
                            .map(MovieEntity::getGenres)
                            .filter(genres -> !genres.isEmpty())
                            .map(genres -> genres.stream().collect(Collectors.joining(", ", "[", "]")))
                            .orElse("[]");

                    return  "movie id: " + movieID + ", "
                            + "fid: " + fid + ", "
                            + "title: " + title + ", "
                            + "year: " + year + ", "
                            + "director: " + directorName + ", "
                            + "genres: " + genresString;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Insert " + insertedMoviesCount + " movies.");
            System.out.println("Insert " + insertGenresCount + " genres.");
            System.out.println("Insert " + genresInMoviesCount + " genres_in_movies.");
            System.out.println(duplicateMoviesCount + " movie are duplicated.");
            System.out.println(invalidMoviesCount + " movie are inconsistent.");
            System.out.println(movieWithNoStarsCount + " movies has no stars.");
            System.out.println(movieWithNoGenresCount + " movies has no genres.");
            latch.countDown();
        }
    }
}