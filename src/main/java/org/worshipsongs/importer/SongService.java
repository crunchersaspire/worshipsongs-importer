package org.worshipsongs.importer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

/**
 * Created by pitchumani on 10/26/15.
 */
public class SongService
{
    private static Logger logger = Logger.getLogger(SongParser.class.getName());
    private Connection connection;
    private DatabaseUtils databaseUtils = new DatabaseUtils();
    private SongDao songDao;
    public void parseAndInsertSongs(String songsDirectory, String dbFilePath) throws IOException, SQLException
    {
        connection = databaseUtils.connectDb(dbFilePath);
        songDao = new SongDao(connection);
        SongParser songParser = new SongParser(dbFilePath);
        for (File file : new File(songsDirectory).listFiles()) {
            logger.log(INFO, "Reading the file : " + file.getName() + "\n");
            Song song = songParser.parseSong(file);
            songDao.create(song);
        }
    }
}