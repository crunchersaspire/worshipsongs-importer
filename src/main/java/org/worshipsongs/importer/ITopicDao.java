package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ITopicDao
{
    Topic getTopic(Topic topic);

    boolean insertTopicSongs(Song song);

    Topic insertTopic(Topic topic);
}
