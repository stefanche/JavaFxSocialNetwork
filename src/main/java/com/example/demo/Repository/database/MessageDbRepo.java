package com.example.demo.Repository.database;

import com.example.demo.Repository.Repository;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.*;
import com.example.demo.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDbRepo implements Repository<Integer, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    private final UserDbRepo userDbRepo;


    public MessageDbRepo(String url, String username, String password, Validator<Message> validator, UserDbRepo userDbRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userDbRepo = userDbRepo;
    }
    @Override
    public Optional<Message> findOne(Integer integer) {
        return Optional.empty();
    }
    public String findText(Integer id) {
        String text = "";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT message FROM message_table WHERE (id)=(?)");
        ) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                text = resultSet.getString("message");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return text;
    }
    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    @Override
    public Optional<Message> save(Message entity) {
        Optional<Message> messageOptional = Optional.empty();
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO message_table (message, from_user_id, reply_to_message_id) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, entity.getMessage());
            statement.setInt(2, entity.getFrom());
            if (entity.getReply() == null || entity.getReply() == 0) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, entity.getReply());
            }
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setID(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Integer id : entity.getTo()) {
            // with transaction
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO messagerecipients (message_id, recipient_user_id) VALUES (?, ?)");
            ) {
                statement.setInt(1, entity.getID());
                statement.setInt(2, id);
                statement.executeUpdate();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setID(generatedKeys.getInt(1));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    public Iterable<Conversation> findAllConversations(Integer id) {
        List<Conversation> conversations = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(""" 
                    WITH AggregatedParticipants AS (
                        SELECT
                            m.id AS message_id,
                            ARRAY_AGG(DISTINCT u.id ORDER BY u.id) AS participants
                        FROM
                            message_table m
                        JOIN
                            user_table u
                        ON
                            u.id = m.from_user_id OR u.id IN (
                                SELECT recipient_user_id
                                FROM MessageRecipients
                                WHERE message_id = m.id
                            )
                        GROUP BY
                            m.id
                    )
                    SELECT DISTINCT
                        participants
                    FROM
                        AggregatedParticipants
                    WHERE
                        ? = ANY(participants);"""

             );)
        {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Array sqlArray = resultSet.getArray("participants");
                Integer[] participants = (Integer[]) sqlArray.getArray();
                List<User> users = new ArrayList<>();
                for (Integer participant : participants) {
                    Optional<User> user = userDbRepo.findOne(participant);
                    user.ifPresent(users::add);
                }
                conversations.add(new Conversation(users));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }
    public Iterable<Message> findAllMessagesFromConversation(Conversation conversation) {
        List<Message> messages = new ArrayList<>();
        StringBuilder array = new StringBuilder("ARRAY[");
        for (Integer id : conversation.getParticipants()) {
            array.append(id).append(",");
        }
        array.deleteCharAt(array.length() - 1);
        array.append("]");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     WITH AggregatedParticipants AS (
                         SELECT
                             m.id AS message_id,
                             ARRAY_AGG(DISTINCT u.id ORDER BY u.id) AS participants
                         FROM
                             message_table m
                                 JOIN user_table u
                                      ON u.id = m.from_user_id OR u.id IN (
                                          SELECT recipient_user_id
                                          FROM MessageRecipients
                                          WHERE message_id = m.id
                                      )
                         GROUP BY
                             m.id
                     )
                     SELECT
                         m.id AS message_id,
                         m.message AS message_body,
                         m.from_user_id AS sender,
                         ARRAY_AGG(DISTINCT r.recipient_user_id) AS recipients,
                         m.reply_to_message_id AS reply_id,
                         m.data AS data
                     FROM
                         message_table m
                             LEFT JOIN MessageRecipients r ON m.id = r.message_id
                             JOIN AggregatedParticipants ap ON ap.message_id = m.id
                    WHERE
                        ap.participants @>""" + array.toString() +"""
                         AND ARRAY_LENGTH(ap.participants, 1) = ?
                    GROUP BY
                    m.id, m.message, m.from_user_id, m.reply_to_message_id, m.data;"""

             );)
        {
            statement.setInt(1, conversation.getParticipants().size());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Array sqlArray = resultSet.getArray("recipients");
                Object[] objArray = (Object[]) sqlArray.getArray();
                List<Integer> recipients = new ArrayList<>();
                for (Object obj : objArray) {
                    if (obj instanceof Integer) {
                        recipients.add((Integer) obj);
                    }
                }
                int reply = resultSet.getInt("reply_id");
                Message message;
                if (reply == -1) {
                    message = new Message(resultSet.getString("message_body"), resultSet.getInt("sender"), recipients, null);
                } else {
                    message = new Message(resultSet.getString("message_body"), resultSet.getInt("sender"), recipients, reply);
                }
                message.setTime(resultSet.getTimestamp("data").toLocalDateTime());
                message.setID(resultSet.getInt("message_id"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    public Optional<Message> mostRecentMessage(Message message) {
        Optional<Message> messageOptional = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM message_table WHERE data > ? ORDER BY data DESC LIMIT 1");
        ) {
            statement.setTimestamp(1, message.getTime());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("data");

                messageOptional = Optional.of(new Message("asddsa",1,new ArrayList<>(), null, timestamp.toLocalDateTime()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageOptional;
    }
}
