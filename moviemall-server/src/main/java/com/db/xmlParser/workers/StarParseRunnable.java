package com.db.xmlParser.workers;

import com.models.StarEntity;
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static com.db.DatabaseManager.execDbProcedure;
import static com.db.DatabaseManager.getDirectDatabaseConnection;
import static com.utils.ConversionUtils.extractYear;
import static com.utils.FileUtils.writeFile;

public class StarParseRunnable implements Runnable {
    private final String xmlFilePath;
    private final Map<String, StarEntity> sharedStarMap;
    private final CountDownLatch latch;
    private int insertStarsCount;
    private int duplicateStarCount;
    private int invalidStarsCount;

    public StarParseRunnable(String xmlFilePath, Map<String, StarEntity> sharedStarMap, CountDownLatch latch) {
        this.xmlFilePath = xmlFilePath;
        this.sharedStarMap = sharedStarMap;
        this.latch = latch;
        this.insertStarsCount = 0;
        this.invalidStarsCount = 0;
        this.duplicateStarCount = 0;
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
                private StarEntity currentStar;

                {
                    currentStar = new StarEntity();
                    elementValue = new StringBuilder();
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementValue.setLength(0); // Clear the StringBuilder for new element value
                    switch (qName.toLowerCase()) {
                        case "actor" -> currentStar = new StarEntity();
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
                            case "stagename" -> currentStar.setStarName(value);
                            case "dob" -> currentStar.setStarBirthYear(extractYear(value));
                            case "actor" -> {
                                insertStarToDb();
                                currentStar = new StarEntity();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Error processing element " + qName + ": " + e.getMessage());
                    } finally {
                        elementValue.setLength(0);
                    }
                }

                private void insertStarToDb() throws SQLException, IOException {
                    if (isCurrStarInvalid()) {
                        ++invalidStarsCount;
                        return;
                    }
                    String newStarID = execDbProcedure(
                            connection,
                            "{CALL add_unique_star(?, ?, ?)}",
                            (stmt, hadResults) -> stmt.getString(3),
                            new Object[]{currentStar.getStarName(), currentStar.getStarBirthYear(), null},
                            new int[]{Types.VARCHAR, Types.INTEGER, Types.VARCHAR},
                            new boolean[]{false, false, true}
                    );
                    if (isCurrStarDuplicated(newStarID)) {
                        return;
                    }
                    currentStar.setStarID(newStarID);
                    sharedStarMap.put(currentStar.getStarName(), currentStar);
                    ++insertStarsCount;
                }

                private boolean isCurrStarInvalid() throws IOException {
                    if (currentStar != null && currentStar.getStarName() != null) {
                        return false;
                    }
                    ++invalidStarsCount;
                    String content = buildStarContent(currentStar);
                    writeFile("result_output/xml_parser", "invalid_stars.txt", content, true);
                    return true;
                }

                private boolean isCurrStarDuplicated(String newStarID) throws IOException {
                    if (newStarID != null) {
                        return false;
                    }
                    ++duplicateStarCount;
                    String content = buildStarContent(currentStar);
                    writeFile("result_output/xml_parser", "duplicated_stars.txt", content, true);
                    return true;
                }

                private String buildStarContent(StarEntity star) {
                    String starID = Optional.ofNullable(star)
                            .map(StarEntity::getStarID)
                            .orElse("N/A");

                    String starName = Optional.ofNullable(star)
                            .map(StarEntity::getStarName)
                            .orElse("N/A");

                    String starBirthYear = Optional.ofNullable(star)
                            .map(StarEntity::getStarBirthYear)
                            .map(Object::toString)
                            .orElse("N/A");

                    return  "star id: " + starID + ", "
                            + "name: " + starName + ", "
                            + "birthYear: " + starBirthYear;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Insert " + insertStarsCount + " stars.");
            System.out.println(duplicateStarCount + " stars are duplicated.");
            System.out.println(invalidStarsCount + " stars not found.");
            latch.countDown();
        }
    }
}