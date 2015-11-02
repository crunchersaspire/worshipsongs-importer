package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ITopicDao
{
    Topic findByName(String name) throws SQLException;

    void createTopicSongs(Song song) throws SQLException;

    void create(Topic topic) throws SQLException;
}
