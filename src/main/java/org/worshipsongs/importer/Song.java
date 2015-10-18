package org.worshipsongs.importer;

/**
 * Created by pitchumani on 10/8/15.
 */
public class Song
{
    private String title;
    private String alternateTitle;
    private String author;
    private String verseOrder;
    private String songBook;
    private String lyrics;
    private String xmlLyrics;
    private String searchTitle;
    private String searchLyrics;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAlternateTitle()
    {
        return alternateTitle;
    }

    public void setAlternateTitle(String alternateTitle)
    {
        this.alternateTitle = alternateTitle;
    }

    public String getVerseOrder()
    {
        return verseOrder;
    }

    public void setVerseOrder(String verseOrder)
    {
        this.verseOrder = verseOrder;
    }

    public String getSongBook()
    {
        return songBook;
    }

    public void setSongBook(String songBook)
    {
        this.songBook = songBook;
    }

    public String getLyrics()
    {
        return lyrics;
    }

    public void setLyrics(String lyrics)
    {
        this.lyrics = lyrics;
    }

    public String getXmlLyrics()
    {
        return xmlLyrics;
    }

    public void setXmlLyrics(String xmlLyrics)
    {
        this.xmlLyrics = xmlLyrics;
    }

    public String getSearchTitle()
    {
        return searchTitle;
    }

    public void setSearchTitle(String searchTitle)
    {
        this.searchTitle = searchTitle;
    }

    public String getSearchLyrics()
    {
        return searchLyrics;
    }

    public void setSearchLyrics(String searchLyrics)
    {
        this.searchLyrics = searchLyrics;
    }

    @Override
    public boolean equals(Object obj)
    {
        Song song = (Song) obj;
        return ((song.title.equals(this.title)) && (song.author.equals(this.author)));
    }

    @Override
    public String toString()
    {
        return "Title --> "+this.getTitle();
    }
}