package com.example.demo.Repository.database;

import com.example.demo.Repository.PageableFriendshipView;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.User;
import com.example.demo.utils.Page;
import com.example.demo.utils.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipsViewRepo implements PageableFriendshipView {
    private String url;
    private String username;
    private String password;
    private UserDbRepo userDbRepo;

    public FriendshipsViewRepo(String url, String username, String password, UserDbRepo userDbRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userDbRepo = userDbRepo;
    }

    private int count(int id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT count(*) FROM friendship_table WHERE ((from_user)=(?) OR (to_user)=(?)) AND accepted=true");
        ) {
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();
            int totalNumberOfMovies = 0;
            if (resultSet.next()) {
                totalNumberOfMovies = resultSet.getInt("count");
            }
            return totalNumberOfMovies;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Page<FriendshipsView> findAllOnPage(Pageable pageable, User loggedIn) {
        int totalNrofFriends = count(loggedIn.getID());
        List<FriendshipsView> friendshipsViews = new ArrayList<>();
        if (totalNrofFriends > 0) {
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("SELECT * from friendship_table WHERE accepted=true AND (from_user = ? OR to_user=?) LIMIT ? OFFSET ?");)
            {
                statement.setInt(1, loggedIn.getID());
                statement.setInt(2, loggedIn.getID());
                statement.setInt(3, pageable.getPageSize());
                statement.setInt(4, pageable.getPageSize() * pageable.getPageNumber());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int to = resultSet.getInt("to_user");
                    boolean accepted = resultSet.getBoolean("accepted");
                    Timestamp created = resultSet.getTimestamp("created");
                    int from_user = resultSet.getInt("from_user");
                    int from = resultSet.getInt("from_original");
                    if (from_user==loggedIn.getID()) {
                        Optional<User> userName = userDbRepo.findOne(to);
                        if (userName.isPresent()) {
                            FriendshipsView friendshipsView = new FriendshipsView(userName.get().getName(), created, from, userName.get().getID());
                            friendshipsViews.add(friendshipsView);
                        }
                    } else {
                        Optional<User> userName = userDbRepo.findOne(from_user);
                        if (userName.isPresent()) {
                            FriendshipsView friendship = new FriendshipsView(userName.get().getName(), created, to, userName.get().getID());
                            friendshipsViews.add(friendship);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new Page<>(friendshipsViews, totalNrofFriends);
    }

    @Override
    public Optional<FriendshipsView> findOne(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Iterable<FriendshipsView> findAll() {
        return null;
    }

    @Override
    public Optional<FriendshipsView> save(FriendshipsView entity) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendshipsView> delete(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendshipsView> update(FriendshipsView entity) {
        return Optional.empty();
    }
}
