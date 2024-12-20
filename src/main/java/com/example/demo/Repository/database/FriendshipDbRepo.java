package com.example.demo.Repository.database;

import com.example.demo.Repository.Repository;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Network;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.domain.validators.Validator;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class FriendshipDbRepo implements Repository<Tuple<Integer>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;


    public FriendshipDbRepo(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        Optional<Friendship> updated = findOne(entity.getID());
        if (updated.isEmpty()) {
            return updated;
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE friendship_table SET accepted = ?  WHERE from_user = ? and to_user = ?")) {

            statement.setBoolean(1, entity.isAccepted());
            statement.setInt(2, entity.getID().getFrom());
            statement.setInt(3, entity.getID().getTo());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Friendship> delete(Tuple<Integer> id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        Optional<Friendship> friendshipToDelete = findOne(id);
        if (friendshipToDelete.isEmpty()) {
            return friendshipToDelete;
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM friendship_table WHERE to_user = ? AND from_user = ?")) {

            // Set the ID in the prepared statement
            statement.setInt(1, id.getTo());
            statement.setInt(2, id.getFrom());
            // Execute the delete operation
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipToDelete;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        validator.validate(entity);
        if (entity == null) {
            return Optional.empty();
        }
        Optional<Friendship> exists = findOne(entity.getID());
        if (exists.isPresent()) {
            return exists;
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO friendship_table (from_user, to_user, created, accepted, from_original) VALUES (?, ?, ?, ?, ?)");
        ) {
            statement.setInt(1, entity.getID().getFrom());
            statement.setInt(2, entity.getID().getTo());
            statement.setInt(5, entity.getFrom());
            statement.setBoolean(4, entity.isAccepted());
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    @Override
    public Optional<Friendship> findOne(Tuple<Integer> id) {
        Optional<Friendship> friendship = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM friendship_table WHERE (from_user)=(?) AND (to_user)=(?)");
        ) {
            statement.setInt(1, id.getFrom());
            statement.setInt(2, id.getTo());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int from = resultSet.getInt("from_user");
                int to = resultSet.getInt("to_user");
                boolean accepted = resultSet.getBoolean("accepted");
                Timestamp created = resultSet.getTimestamp("created");

                friendship = Optional.of(new Friendship(from, to, accepted, created));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new LinkedHashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_table ORDER BY from_user");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int to = resultSet.getInt("to_user");
                boolean accepted = resultSet.getBoolean("accepted");
                Timestamp created = resultSet.getTimestamp("created");
                int from = resultSet.getInt("from_user");

                Friendship friendship = new Friendship(from, to, accepted, created);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
    public LinkedHashSet<Network> findNetworks() {
        LinkedHashSet<Network> networks = new LinkedHashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_table WHERE accepted = true ORDER BY from_user ");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int to = resultSet.getInt("to_user");
                int from = resultSet.getInt("from_user");
                if (networks.isEmpty()) {
                    Network first = new Network();
                    first.addUser(from);
                    first.addUser(to);
                    networks.add(first);
                } else {
                    Set<Network> helperSet;
                    helperSet = networks
                           .stream()
                           .filter(network -> network.hasUser(from))
                           .collect(Collectors.toSet());
                    if ((long) helperSet.size() > 1) {
                        Network helperNetwork = new Network();
                        for (Network network : helperSet) {
                            helperNetwork.swallowNetwork(network);
                            networks.remove(network);
                        }
                        helperNetwork.addUser(to);
                        networks.add(helperNetwork);
                    } else if((long) helperSet.size() == 1) {
                        helperSet.forEach(network -> network.addUser(to)); // intrebare
                    } else {
                        // nu exista
                        Network first = new Network();
                        first.addUser(from);
                        first.addUser(to);
                        networks.add(first);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return networks;
    }
    public Iterable<FriendshipsView> userSentRequests(User user) {
        Set<FriendshipsView> friendships = new LinkedHashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_table WHERE from_original=?");)
            {
                statement.setInt(1, user.getID());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int to = resultSet.getInt("to_user");
                    Timestamp created = resultSet.getTimestamp("created");
                    int from_user = resultSet.getInt("from_user");
                    int from = resultSet.getInt("from_original");
                    if (from==from_user) {
                        Optional<User> userName = findUserById(to);
                        if (userName.isPresent()) {
                            FriendshipsView friendship = new FriendshipsView(userName.get().getName(), created, from);
                            friendships.add(friendship);
                        }
                    } else {
                        Optional<User> userName = findUserById(from);
                        if (userName.isPresent()) {
                            FriendshipsView friendship = new FriendshipsView(userName.get().getName(), created, to);
                            friendships.add(friendship);
                        }
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
    public Iterable<FriendshipsView> userRecievedRequests(User user) {
        Set<FriendshipsView> friendships = new LinkedHashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_table WHERE from_original !=? AND accepted=false AND (from_user = ? OR to_user=?)");)
        {
            statement.setInt(1, user.getID());
            statement.setInt(2, user.getID());
            statement.setInt(3, user.getID());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int to = resultSet.getInt("to_user");
                boolean accepted = resultSet.getBoolean("accepted");
                Timestamp created = resultSet.getTimestamp("created");
                int from_user = resultSet.getInt("from_user");
                int from = resultSet.getInt("from_original");
                if (from==from_user) {
                    Optional<User> userName = findUserById(from);
                    if (userName.isPresent()) {
                        FriendshipsView friendship = new FriendshipsView(userName.get().getName(), created, from);
                        friendships.add(friendship);
                    }
                } else {
                    Optional<User> userName = findUserById(to);
                    if (userName.isPresent()) {
                        FriendshipsView friendship = new FriendshipsView(userName.get().getName(), created, to);
                        friendships.add(friendship);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }
    public Optional<Friendship> mostRecentFriendship(User user) {
        Optional<Friendship> friendship = Optional.empty();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM friendship_table WHERE ((from_user)=(?) OR (to_user)=(?)) AND from_original!=? ORDER BY created DESC LIMIT 1");
        ) {
            statement.setInt(1, user.getID());
            statement.setInt(2, user.getID());
            statement.setInt(3, user.getID());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int to = resultSet.getInt("to_user");
                boolean accepted = resultSet.getBoolean("accepted");
                Timestamp created = resultSet.getTimestamp("created");
                int from_user = resultSet.getInt("from_user");
                int from = resultSet.getInt("from_original");
                friendship = Optional.of(new Friendship(from_user, to, accepted, created));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendship;
    }
    public Integer nrOfNewFriendships(Friendship friendship, int loggedInId) {
        Integer nrOfNewFriendships = 0;
        System.out.println(friendship.getTimestamp());
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) AS newFr FROM friendship_table WHERE ((from_user)=(?) OR (to_user)=(?)) AND from_original!=? AND created>(?)");
        ) {
            statement.setInt(1, loggedInId);
            statement.setInt(2, loggedInId);
            statement.setInt(3, loggedInId);
            statement.setTimestamp(4, friendship.getTimestamp());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                nrOfNewFriendships = resultSet.getInt("newFr");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nrOfNewFriendships;
    }
    private Optional<User> findUserById(int id) {
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
    public Iterable<FriendshipsView> friendshipsOfUser(Integer id) {
        List<FriendshipsView> friendshipsViews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_table WHERE accepted=true AND (from_user = ? OR to_user=?)");)
        {
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int to = resultSet.getInt("to_user");
                boolean accepted = resultSet.getBoolean("accepted");
                Timestamp created = resultSet.getTimestamp("created");
                int from_user = resultSet.getInt("from_user");
                int from = resultSet.getInt("from_original");
                if (from==from_user) {
                    Optional<User> userName = findUserById(to);
                    if (userName.isPresent()) {
                        FriendshipsView friendshipsView = new FriendshipsView(userName.get().getName(), created, from, userName.get().getID());
                        friendshipsViews.add(friendshipsView);
                    }
                } else {
                    Optional<User> userName = findUserById(from_user);
                    if (userName.isPresent()) {
                        FriendshipsView friendship = new FriendshipsView(userName.get().getName(), created, to, userName.get().getID());
                        friendshipsViews.add(friendship);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipsViews;
    }
}
