package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Pitchu on 10/18/2015.
 */
public class TopicDao {
    public int getAuthorId(Connection connection, String topic)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT * FROM TOPICS where name = '" + topic + "';" );
            id = resultSet.getInt("id");
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void insertTopic(Connection connection, Topic topic, int songId)
    {
        try {
            String query = "insert into songs_topics (song_id, topic_id) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, songId);
            ps.setInt(2, topic.getId());
            ps.executeUpdate();
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}