package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/5/15.
 */

import java.util.regex.*;

public class SongParser
{
    public String parseTitle(String input)
    {
        String title = parseAttribute(input, "title");
        if(title == "") {
            throw new NullPointerException("Title should not be empty");
        }
        return title;
    }

    public String parseAuthor(String input)
    {
        return parseAttribute(input, "author");
    }

    public String parseAlternateTitle(String input)
    {
        return parseAttribute(input, "alternateTitle");
    }

    public String parseSearchTitle(String title, String alternateTitle)
    {
        return (title + "@" + alternateTitle).toLowerCase();
    }

    public String parseSearchLyrics(String input)
    {
        return input.toLowerCase();
    }

    public String parseVerseOrder(String input)
    {
        return parseAttribute(input, "verseOrder");
    }

    public String parseLyrics(String input)
    {
        return parseAttribute(input, "lyrics");
    }

    public String parseAttribute(String input, String attributeName)
    {
        if (!input.isEmpty()) {
            if(attributeName.equalsIgnoreCase("lyrics")) {
                return input.split(".*=")[0].trim();
            }
            String attribute = findMatchingData(input, attributeName);
            if (attribute.contains("=")) {
                return attribute.split("=")[1];
            }
        }
        return "";
    }

    public String findMatchingData(String input, String attributeName)
    {
        Pattern pattern = Pattern.compile(attributeName + "=.*");
        Matcher matcher = pattern.matcher(input);
        String matchingData = "";

        while (matcher.find()) {
            matchingData = matcher.group(0);
        }
        return matchingData;
    }
}
