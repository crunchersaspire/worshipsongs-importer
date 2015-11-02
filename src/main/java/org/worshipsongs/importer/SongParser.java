package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.logging.Level.INFO;

public class SongParser implements ISongParser
{
    private static Logger logger = Logger.getLogger(SongParser.class.getName());
    private ClassLoader classLoader;
    private Connection connection;
    private IAuthorDao authorDao;
    private ITopicDao topicDao;
    private ISongBookDao songBookDao;
    private DatabaseUtils databaseUtils;

    public SongParser(String dbDir)
    {
        databaseUtils = new DatabaseUtils();
        connection = databaseUtils.connectDb(dbDir);
        authorDao = new AuthorDao(connection);
        topicDao = new TopicDao(connection);
        songBookDao = new SongBookDao(connection);
    }

    public Song parseSong(File file) throws IOException, SQLException
    {
        BufferedReader bufferedReader = null;
        String input = "";
        StringBuffer stringBuffer = new StringBuffer();
        Song song;

        bufferedReader = new BufferedReader(new FileReader(file));
        while ((input = bufferedReader.readLine()) != null) {
            stringBuffer.append(input);
            stringBuffer.append("\n");
        }
        logger.log(INFO, "Parsing the file : " + file.getName() + "\n");
        song = parseSongFromText(stringBuffer.toString());
        logger.log(INFO, "Parsed the file : " + file.getName() + "\n");
        return song;
    }

    Song parseSongFromText(String input) throws IOException, SQLException
    {
        classLoader = getClass().getClassLoader();
        Song song = new Song();
        song.setTitle(parseTitle(input));
        song.setAlternateTitle(parseAlternateTitle(input));
        song.setVerseOrder(parseVerseOrder(input));
        song.setXmlLyrics(getXmlLyrics(parseLyrics(input)));
        song.setSearchTitle(parseSearchTitle(parseTitle(input), parseAlternateTitle(input)));
        song.setSearchLyrics(parseSearchLyrics(parseLyrics(input)));

        song.setAuthor(findOrCreateAuthor(parseAuthor(input)));
        song.setSongBook(findOrCreateSongBook(parseSongBook(input)));
        song.setTopic(findOrCreateTopic(parseTopic(input)));
        return song;
    }

    String parseTitle(String input)
    {
        String title = parseAttribute(input, "title");
        if (title == "") {
            throw new NullPointerException("Title should not be empty");
        }
        return title;
    }

    String parseAlternateTitle(String input)
    {
        return parseAttribute(input, "alternateTitle");
    }

    String parseAuthor(String input)
    {
        return parseAttribute(input, "author");
    }

    String parseSearchTitle(String title, String alternateTitle)
    {
        return (title + "@" + alternateTitle).toLowerCase();
    }

    String parseSearchLyrics(String lyrics)
    {
        String verses[] = splitLyrics(lyrics);
        String searchLyrics = "";

        for (int i = 1; i < verses.length; i++) {
            searchLyrics = searchLyrics.concat(verses[i].replace("\n", " ").replaceAll("[^a-zA-Z0-9\\s]", ""));
        }
        return searchLyrics.toLowerCase().trim();
    }

    String parseVerseOrder(String input)
    {
        return parseAttribute(input, "verseOrder");
    }

    String parseLyrics(String lyrics)
    {
        return lyrics.split(".*=")[0].trim();
    }

    String parseTopic(String input)
    {
        return parseAttribute(input, "topic");
    }

    String parseAttribute(String input, String attributeName)
    {
        if (!input.isEmpty()) {
            String attribute = findMatchingData(input, attributeName);
            if (attribute.contains("=")) {
                return attribute.split("=")[1];
            }
        }
        return "";
    }

    String findMatchingData(String input, String attributeName)
    {
        Pattern pattern = Pattern.compile(attributeName + "=.*");
        Matcher matcher = pattern.matcher(input);
        String matchingData = "";
        while (matcher.find()) {
            matchingData = matcher.group(0);
        }
        return matchingData;
    }

    String getXmlLyrics(String lyrics)
    {
        Writer out = new StringWriter();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            document.setXmlStandalone(true);

            Element songElement = getSongElement(document);
            document.appendChild(songElement);

            Element lyricsElement = getLyricsElement(document);
            songElement.appendChild(lyricsElement);

            List verseOrders = splitVerse(lyrics);
            String verse[] = splitLyrics(lyrics);

            for (int i = 0; i < verseOrders.size(); i++) {
                Element verseElement = getVerseElement(document, verseOrders.get(i).toString(), verse[i + 1]);
                lyricsElement.appendChild(verseElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(out));
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "Exception occurs in:", e);
        } catch (TransformerException e) {
            logger.log(Level.SEVERE, "Exception occurs in:", e);
        }
        return out.toString();
    }

    Element getSongElement(Document document)
    {
        Element song = document.createElement("song");
        Attr version = document.createAttribute("version");
        version.setValue("1.0");
        song.setAttributeNode(version);
        return song;
    }

    Element getLyricsElement(Document document)
    {
        return document.createElement("lyrics");
    }

    Element getVerseElement(Document document, String verseOrders, String verse)
    {
        Element verseElement = document.createElement("verse");
        Attr type = document.createAttribute("type");
        type.setValue(splitVerseType(verseOrders));
        verseElement.setAttributeNode(type);
        Attr label = document.createAttribute("label");
        label.setValue(splitVerseLabel(verseOrders));
        verseElement.setAttributeNode(label);
        verseElement.appendChild(document.createCDATASection(verse.trim()));
        return verseElement;
    }

    String splitVerseType(String verse)
    {
        String verseType = "";
        if (!verse.isEmpty()) {
            verseType = verse.split("(?!^)")[0].toLowerCase();
        }
        return verseType;
    }

    String splitVerseLabel(String verse)
    {
        String verseLabel = "";
        if (!verse.isEmpty()) {
            verseLabel = verse.split("(?!^)")[1];
        }
        return verseLabel;
    }

    String[] splitLyrics(String lyrics)
    {
        return lyrics.split("\\[..\\]\\n");
    }

    String parseSongBook(String input)
    {
        return parseAttribute(input, "songBook");
    }

    List splitVerse(String input)
    {
        return findVerse(input);
    }

    List findVerse(String input)
    {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(input);
        List verses = new ArrayList();
        while (matcher.find()) {
            verses.add(matcher.group(1));
        }
        return verses;
    }

    String parseComment(String comment)
    {
        return comment.split("\\[comment\\]")[1].trim();
    }

    Author findOrCreateAuthor(String authorDisplayName) throws SQLException
    {
        Author author = new Author();
        if (authorDisplayName.isEmpty()) {
            authorDisplayName = "Author Unknown";
        }
        author = authorDao.findByDisplayName(authorDisplayName);
        author.setAuthor(authorDisplayName);
        if (author.getId() > 0) {
            return author;
        } else {
            authorDao.create(author);
            author = authorDao.findByDisplayName(authorDisplayName);
        }
        return author;
    }

    Topic findOrCreateTopic(String topicName) throws SQLException
    {
        Topic topic = new Topic();
        if (!topicName.isEmpty()) {
            topic.setTopic(topicName);
            topic = topicDao.findByName(topicName);
            topic.setTopic(topicName);
            if (topic.getId() > 0) {
                return topic;
            } else {
                topicDao.create(topic);
                topic = topicDao.findByName(topicName);
            }
        }
        return topic;
    }

    SongBook findOrCreateSongBook(String songBookName) throws SQLException
    {
        SongBook songBook = new SongBook();
        if (!songBookName.isEmpty()) {
            songBook = songBookDao.findByName(songBookName);
            songBook.setSongBook(songBookName);
            if (songBook.getId() > 0) {
                return songBook;
            } else {
                songBookDao.create(songBook);
                songBook = songBookDao.findByName(songBookName);
            }
        }
        return songBook;
    }
}