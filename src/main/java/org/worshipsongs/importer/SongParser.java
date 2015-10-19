package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import org.apache.commons.io.IOUtils;
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
import static java.util.logging.Level.SEVERE;

public class SongParser
{
    final static Logger logger = Logger.getLogger(SongParser.class.getName());
    ClassLoader classLoader;
    Song song = new Song();
    Topic topic = new Topic();
    Author author = new Author();
    SongBook  songBook = new SongBook();
    AuthorDao authorDao = new AuthorDao();
    TopicDao topicDao = new TopicDao();
    SongBookDao songBookDao = new SongBookDao();
    SongDao songDao = new SongDao();
    Connection connection;

    Song parseSong(String fileName) throws IOException
    {
        classLoader = getClass().getClassLoader();

        String input = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        song.setTitle(parseTitle(input));
        topic.setTopic(parseTopic(input));
        song.setAlternateTitle(parseAlternateTitle(input));
        author.setAuthor(parseAuthor(input));
        song.setVerseOrder(parseVerseOrder(input));
        songBook.setSongBook(parseSongBook(input));
        song.setLyrics(parseLyrics(input));
        song.setXmlLyrics(getXmlLyrics(parseLyrics(input), parseVerseOrder(input)));
        song.setSearchTitle(parseSearchTitle(parseTitle(input), parseAlternateTitle(input)));
        song.setSearchLyrics(parseSearchLyrics(parseLyrics(input)));
        return song;
    }

    List parseSongs(String directory)
    {
        classLoader = getClass().getClassLoader();
        BufferedReader bufferedReader = null;
        String input="";
        StringBuffer stringBuffer = new StringBuffer();
        List list = new ArrayList();
        int i;

        File[] files = new File(directory).listFiles();
        for(i = 0; i < files.length; i++)
        {
            try {
                logger.log(INFO, "Reading the file : "+files[i].getName() +"\n");
                bufferedReader = new BufferedReader(new FileReader(directory+"/"+files[i].getName()));
                while ((input = bufferedReader.readLine()) != null) {
                    stringBuffer.append(input);
                    stringBuffer.append("\n");
                }
                logger.log(INFO, "Parsing the file : "+files[i].getName() +"\n");
                song.setTitle(parseTitle(stringBuffer.toString()));
                topic.setTopic(parseTopic(stringBuffer.toString()));
                song.setAlternateTitle(parseAlternateTitle(stringBuffer.toString()));
                author.setAuthor(parseAuthor(stringBuffer.toString()));
                song.setVerseOrder(parseVerseOrder(stringBuffer.toString()));
                songBook.setSongBook(parseSongBook(stringBuffer.toString()));
                song.setLyrics(parseLyrics(stringBuffer.toString()));
                song.setXmlLyrics(getXmlLyrics(parseLyrics(stringBuffer.toString()), parseVerseOrder(stringBuffer.toString())));
                song.setSearchTitle(parseSearchTitle(parseTitle(stringBuffer.toString()), parseAlternateTitle(stringBuffer.toString())));
                song.setSearchLyrics(parseSearchLyrics(parseLyrics(stringBuffer.toString())));
                insertRecords(stringBuffer.toString());
                logger.log(INFO, "Parsed the file : " + files[i].getName() +"\n");
                list.add(song.toString());
            } catch (Exception e) {
                logger.log(SEVERE, "Problem while parsing/reading the file " + e +"\n");
            }
        }
        logger.log(INFO, "Parsed "+ i +" files.");
        return list;
    }

    void insertRecords(String input)
    {
        int songId;
        int authorId;
        int topicId;
        int songBookId;
//        if(!getEnvironmentVariable("OPENLP_HOME").isEmpty())
//        {
            connection = authorDao.connectDb("/home/pitchumani");

            authorId = authorDao.getAuthorId(connection, parseAuthor(input));
            if(authorId > 0) {
                author.setId(authorId);
            }
            else {
                // insert author
            }
            topicId = topicDao.getTopicId(connection, parseTopic(input));
            topic.setId(topicId);
            songBookId = songBookDao.getSongBookId(connection, parseSongBook(input));
            songBook.setId(songBookId);

            if(songDao.insertSong(connection, song, songBook))
            {
                songId = songDao.getSongId(connection, song.getTitle());
                if(authorDao.insertAuthor(connection, author, songId))
                {
                    topicDao.insertTopic(connection, topic, songId);
                }
            }
//        }
    }
    
    String parseTitle(String input)
    {
        String title = parseAttribute(input, "title");
        if(title == "") {
            throw new NullPointerException("Title should not be empty");
        }
        return title;
    }

    String parseAuthor(String input)
    {
        return parseAttribute(input, "author");
    }

    String parseAlternateTitle(String input)
    {
        return parseAttribute(input, "alternateTitle");
    }

    String parseSearchTitle(String title, String alternateTitle)
    {
        return (title + "@" + alternateTitle).toLowerCase();
    }

    String parseSearchLyrics(String lyrics)
    {
        String verses[] = splitVerse(lyrics);
        String searchLyrics = "";

        for(int i = 1; i < verses.length; i++) {
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

    String getXmlLyrics(String lyrics, String verseOrder)
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

            String verseOrders[] = splitVerseOrder(verseOrder);
            String verses[] = splitVerse(lyrics);

            for (int i = 0; i < verseOrders.length; i++)
            {
                Element verseElement = getVerseElement(document, verseOrders[i], verses[i+1]);
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

    String[] splitVerseOrder(String verseOrder)
    {
        return verseOrder.split(" ");
    }

    String splitVerseType(String verse)
    {
        String verseType = "";
        if(!verse.isEmpty()) {
            verseType = verse.split("(?!^)")[0].toLowerCase();
        }
        return verseType;
    }

    String splitVerseLabel(String verse)
    {
        String verseLabel = "";
        if(!verse.isEmpty()) {
            verseLabel = verse.split("(?!^)")[1];
        }
        return verseLabel;
    }

    String[] splitVerse(String lyrics)
    {
        return lyrics.split("\\[..\\]\\n");
    }

    String parseSongBook(String input)
    {
        return parseAttribute(input, "songBook");
    }

    public String parseTopic(String input) {
        return parseAttribute(input, "topic");
    }

    public String getEnvironmentVariable(String variableName)
    {
        if (!variableName.isEmpty()) {
            return System.getenv(variableName);
        }
        else {
            return "";
        }
    }
}