package com.example.demo.oldCode;

import com.example.demo.Repository.Repository;
import com.example.demo.Repository.database.FriendshipDbRepo;
import com.example.demo.Repository.database.UserDbRepo;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Network;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.domain.validators.FriendshipValidator;
import com.example.demo.domain.validators.UserValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

// add/ remove user
// add/ remove friend
// afisare nr comunitati
// afisare cea mai sociabila comunitate
// arhitectura stratificata
// doc java
// interfata cu utilizator minimala
public class Main {

    public static final String username="postgres";
    public static final String pasword="postgres";
    public static final String url="jdbc:postgresql://localhost:5432/social_network";
    public static ArrayList<User> readUsers () {
        String filePath = "src/users.csv";
        ArrayList<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                users.add(new User(Integer.parseInt(values[0].trim()), values[1].trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    public static void main(String[] args) {
        ArrayList<User> users = readUsers();
        Repository<Integer, User> userDbRepo = new UserDbRepo(url,username, pasword,  new UserValidator());
        FriendshipDbRepo friendshipDbRepo = new FriendshipDbRepo(url,username, pasword, new FriendshipValidator());
        String command = "";
        while (!command.equals("exit")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Possible commands are admin login, user login");
            command = scanner.nextLine();
            if (command.equals("admin login")) {
                while (!command.equals("logout")) {
                    System.out.println("Possible commands are: show users, show networks, show biggest network, add user, remove user");
                    command = scanner.nextLine();
                    if(command.equals("show users")) {
                        userDbRepo.findAll().forEach(System.out::println);
                    }
                    if(command.equals("add user")) {
                        System.out.println("Give id");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.println("Give name");
                        String name = scanner.nextLine();
                        User user = new User(id,name);
                        if (userDbRepo.save(user).isEmpty()){
                            System.out.println("User added");
                        } else {
                            System.out.println("User id was already existing");
                        }
                    }
                    if(command.equals("remove user")) {
                        System.out.println("Give id");
                        int id = Integer.parseInt(scanner.nextLine());
                        if (userDbRepo.delete(id).isPresent()) {
                            System.out.println("User removed");
                        }
                    }
                    if (command.equals("show networks")){
                        friendshipDbRepo.findNetworks().forEach(System.out::println);
                    }
                    if (command.equals("show biggest network")){
                        Network bigNet = new Network();
                        int maxLen = -1;
                        for (final Network network : friendshipDbRepo.findNetworks()) {
                            if (network.getLength() > maxLen) {
                                bigNet = network;
                                maxLen = network.getLength();
                            }
                        }
                        System.out.println(bigNet);
                    }
                }
            }
            if (command.equals("user login")) {
                while (!command.equals("back")) {
                    System.out.println("Enter id or write back");
                    command = scanner.nextLine();
                    int id;
                    try {
                        id = Integer.parseInt(command);
                    } catch (NumberFormatException e) {
                        System.out.println("----");
                        System.out.println("id not valid");
                        System.out.println("----");
                        continue;
                    }
                    User loggedIn;
                    Optional<User> optionalUser = userDbRepo.findOne(id);
                    if(optionalUser.isPresent()) {
                        loggedIn = optionalUser.get();
                        System.out.println("Welcome "+ loggedIn.getName()+ " what do you want to do?");
                        while (!command.equals("logout")) {
                            System.out.println("possible commands are: add or remove friend, logout");
                            command = scanner.nextLine();
                            if (command.equals("add")) {
                                while (!command.equals("back")) {
                                    System.out.println("Enter id or write back");
                                    command = scanner.nextLine();
                                    try {
                                        int friendId = Integer.parseInt(command);
                                        Optional<User> optionalUser2 = userDbRepo.findOne(friendId);
                                        if(optionalUser2.isPresent()) {
                                            Friendship newFr = new Friendship(loggedIn.getID(), friendId, true);
                                            friendshipDbRepo.save(newFr);
                                            System.out.println("Friendship created");
                                        } else {
                                            System.out.println("non existing user");
                                        }
                                    } catch (NumberFormatException e) {
                                        if (command.equals("back")) {
                                            System.out.println("going back");
                                        }
                                        System.out.println("not a valid number or a valid command");
                                    }
                                }
                            } else if (command.equals("remove")) {
                                while (!command.equals("back")) {
                                    System.out.println("Enter id or write back");
                                    command = scanner.nextLine();
                                    try {
                                        int friendId = Integer.parseInt(command);
                                        Optional<User> optionalUser2 = userDbRepo.findOne(friendId);
                                        if (optionalUser2.isPresent()) {
                                            Tuple<Integer> fr = new Tuple<Integer>(loggedIn.getID(), friendId);
                                            Optional<Friendship> friendshipTest = friendshipDbRepo.findOne(fr);
                                            if (friendshipTest.isPresent()) {
                                                if(friendshipTest.get().isAccepted()) {
                                                    System.out.println("deleting friendship");
                                                    friendshipDbRepo.delete(fr);
                                                } else {
                                                    System.out.println("not accepted friendship");
                                                }
                                            } else {
                                                System.out.println("You dont have this friend");
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        if (command.equals("back")) {
                                            System.out.println("going back");
                                        } else {
                                            System.out.println("not a valid number or a valid command");
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("User not found");
                    }
                }

            }
        }
    }
}