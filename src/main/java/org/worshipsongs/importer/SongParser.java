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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.logging.Level.INFO;

public class SongParser
{
    private static Logger logger = Logger.getLogger(SongParser.class.getName());
    private ClassLoader classLoader;
    private Song song = new Song();
    private Topic topic = new Topic();
    private Author author = new Author();
    private SongBook songBook = new SongBook();
    private Connection connection;
    private ISongDao songDao;
    private IAuthorDao authorDao;
    private ITopicDao topicDao;
    private ISongBookDao songBookDao;
    private DatabaseUtils databaseUtils = new DatabaseUtils();

    SongParser(String dbDir)
    {
        connection = databaseUtils.connectDb(dbDir);
        songDao = new SongDao(connection);
        authorDao = new AuthorDao(connection);
        topicDao = new TopicDao(connection);
        songBookDao = new SongBookDao(connection);
    }

    Song readFileAndParseSong(String songsDirectory, String fileName) throws IOException
    {
        BufferedReader bufferedReader = null;
        String input = "";
        StringBuffer stringBuffer = new StringBuffer();
        Song song;

        bufferedReader = new BufferedReader(new FileReader(songsDirectory + "/" + fileName));
        while ((input = bufferedReader.readLine()) != null) {
            stringBuffer.append(input);
            stringBuffer.append("\n");
        }
        logger.log(INFO, "Parsing the file : " + fileName + "\n");
        song = parseSong(stringBuffer.toString());
        logger.log(INFO, "Parsed the file : " + fileName + "\n");
        return song;
    }

    Song parseSong(String input) throws IOException
    {
        classLoader = getClass().getClassLoader();
        author.setAuthor(parseAuthor(input));
        songBook.setSongBook(parseSongBook(input));
        topic.setTopic(parseTopic(input));
        song.setTitle(parseTitle(input));
        song.setAlternateTitle(parseAlternateTitle(input));
        song.setAuthor(author);
        song.setSongBook(songBook);
        song.setTopic(topic);
        song.setVerseOrder(parseVerseOrder(input));
        song.setXmlLyrics(getXmlLyrics(parseLyrics(input)));
        song.setSearchTitle(parseSearchTitle(parseTitle(input), parseAlternateTitle(input)));
        song.setSearchLyrics(parseSearchLyrics(parseLyrics(input)));
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
//
//    public String parseComment(String comment)
//    {
//        return comment.split("\\[comment\\]")[0].trim();
//    }

    void insertRecords(Song song)
    {
        author = findOrCreateAuthor(author);
        topic = findOrCreateTopic(topic);
        songBook = findOrCreateSongBook(songBook);

        if (songDao.insertSong(song)) {
            song = songDao.getSongId(song);
            if (author.getId() != 0) {
                authorDao.insertAuthorSongs(song);
            }
            if (topic.getId() != 0) {
                topicDao.insertTopicSongs(song);
            }
        }
    }

    Author findOrCreateAuthor(Author author)
    {
        if (author.getAuthor().isEmpty()) {
            author.setAuthor("Author Unknown");
        }
        author = authorDao.getAuthor(author);
        if (author.getId() > 0) {
            return author;
        } else {
            author = authorDao.insertAuthor(author);
        }
        return author;
    }

    Topic findOrCreateTopic(Topic topic)
    {
        if (!topic.getTopic().isEmpty()) {
            topic = topicDao.getTopic(topic);
            if (topic.getId() > 0) {
                return topic;
            } else {
                topic = topicDao.insertTopic(topic);
            }
        }
        return topic;
    }

    SongBook findOrCreateSongBook(SongBook songBook)
    {
        if (!songBook.getSongBook().isEmpty()) {
            songBook = songBookDao.getSongBook(songBook);
            if (songBook.getId() > 0) {
                return songBook;
            } else {
                songBook = songBookDao.insertSongBook(songBook);
            }
        }
        return songBook;
    }
}