package org.worshipsongs.importer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

/**
 * Created by pitchumani on 10/26/15.
 */
public class SongService
{
    private static Logger logger = Logger.getLogger(SongParser.class.getName());
    public void parseAndInsertSongs(String songsDirectory, String dbFilePath) throws IOException
    {
        List<Song> list;
        SongParser songParser = new SongParser();
        File[] files = new File(songsDirectory).listFiles();
        for (int i = 0; i < files.length; i++) {
            logger.log(INFO, "Reading the file : " + files[i].getName() + "\n");
            list = songParser.readFileAndParseSong(songsDirectory, files[i].getName());
            Song song = list.get(0);
            songParser.insertRecords(song, dbFilePath);
        }
    }
}