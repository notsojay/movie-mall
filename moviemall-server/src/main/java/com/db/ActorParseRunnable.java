package com.db;

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

import static com.db.DatabaseManager.getDirectDatabaseConnection;

public class ActorParseRunnable implements Runnable {
    private String xmlFilePath;
    private Map<String, StarEntity> sharedActorMap;
    private CountDownLatch latch;

    public ActorParseRunnable(String xmlFilePath, Map<String, StarEntity> sharedActorMap, CountDownLatch latch) {
        this.xmlFilePath = xmlFilePath;
        this.sharedActorMap = sharedActorMap;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(xmlFilePath, new DefaultHandler() {
                private Connection connection;
                private StringBuilder elementValue;
                private StarEntity currentStar;

                {
                    currentStar = new StarEntity();
                    elementValue = new StringBuilder();
                    connection = getDirectDatabaseConnection(
                        "jdbc:mysql://localhost:3306/moviedb",
                        "cs122b",
                        "pwd_is_team_NO_1");
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    elementValue.setLength(0); // Clear the StringBuilder for new element value
                    switch (qName.toLowerCase()) {
                        case "actor":
                            currentStar = new StarEntity();
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
                        case "stagename" -> currentStar.setStarName(value);
                        case "dob" -> currentStar.setStarBirthYear(extractYear(value));
                        case "actor" -> {
                            sharedActorMap.put(currentStar.getStarName(), currentStar);
                            currentStar = new StarEntity();
                        }
                    }
                    elementValue.setLength(0);
                }

                private Integer extractYear(String yearString) {
                    if (yearString.length() < 4 || !yearString.matches("^\\d{4}$")) {
                        return null;
                    }
                    return Integer.parseInt(yearString.substring(0, 4));
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
