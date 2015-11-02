package org.worshipsongs.importer;

import java.sql.*;

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

    public Topic findByName(String name) throws SQLException
    {
        Topic topic = new Topic();
        int id = 0;
        Statement statement = null;
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM TOPICS where name = '" + name + "';");
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        resultSet.close();
        statement.close();
        topic.setId(id);
        return topic;
    }

    public void createTopicSongs(Song song) throws SQLException
    {
        String query = "insert into songs_topics (song_id, topic_id) values (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, song.getId());
        preparedStatement.setInt(2, song.getTopic().getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void create(Topic topic) throws SQLException
    {
        String query = "insert into topics (name) values (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, topic.getTopic());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}