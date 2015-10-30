package org.worshipsongs.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Pitchu on 10/18/2015.
 */
public class TopicDao implements ITopicDao
{
    private Connection connection;
    public TopicDao(Connection connection)
    {
        this.connection = connection;
    }

    public Topic getTopic(Topic topic)
    {
        int id = 0;
        try {
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM TOPICS where name = '" + topic.getTopic() + "';");
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        topic.setId(id);
        return topic;
    }

    public boolean insertTopicSongs(Song song)
    {
        try {
            String query = "insert into songs_topics (song_id, topic_id) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, song.getId());
            preparedStatement.setInt(2, song.getTopic().getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            return false;
        }
    }

    public Topic insertTopic(Topic topic)
    {
        try {
            String query = "insert into topics (name) values (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, topic.getTopic());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }
        return getTopic(topic);
    }
}