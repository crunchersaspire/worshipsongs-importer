package org.worshipsongs.importer;

import java.sql.Connection;

/**
 * Created by pitchumani on 10/30/15.
 */
public interface ITopicDao
{
    int getTopicId(Connection connection, String topic);
    boolean insertTopicSongs(Connection connection, int topicId, int songId);
    int insertTopic(Connection connection, String topic);
}
