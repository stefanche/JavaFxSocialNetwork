package com.example.demo.Repository.database;

import com.example.demo.Repository.Repository;
import com.example.demo.domain.User;
import com.example.demo.domain.validators.Validator;

import java.sql.*;
import java.util.*;

public class UserDbRepo implements Repository<Integer, User> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<User> validator;


    public UserDbRepo(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Optional<User> findOne(Integer id) {
        Optional<User> user = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_table WHERE (id)=(?)");
        ) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                user = Optional.of(new User(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    @Override
    public Iterable<User> findAll() {
        LinkedHashSet<User> users = new LinkedHashSet<User>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from user_table ORDER BY id");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                User usr = new User(id, name);
                users.add(usr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    @Override
    public Optional<User> save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }
        validator.validate(user);
        Integer id = user.getID();
        Optional<User> exists = findOne(id);
        if (exists.isPresent()) {
            return exists;
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO user_table (name, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(2, user.getPassword());
            statement.setString(1, user.getName());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setID(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    @Override
    public Optional<User> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        Optional<User> userToDelete = findOne(id);
        if (userToDelete.isEmpty()) {
            return userToDelete;
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM user_table WHERE id = ?")) {

            // Set the ID in the prepared statement
            statement.setInt(1, id);

            // Execute the delete operation
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userToDelete;
    }
    @Override
    public Optional<User> update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        validator.validate(user);
        Integer id = user.getId();
        Optional<User> exists = findOne(id);
        if (exists.isPresent()) {
            return Optional.of(user);
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE user_table SET name = ? WHERE id = ?")) {

            statement.setString(1, user.getName());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    public Optional<User> checkCredentials(String provided_name, String provided_password) {
        Optional<User> user;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM user_table WHERE name=(?) AND password=(?)");
        ) {
            statement.setString(1, provided_name);
            statement.setString(2, provided_password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Integer id = resultSet.getInt("id");
                return Optional.of(new User(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
