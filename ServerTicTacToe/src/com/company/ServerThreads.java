package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerThreads {
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Room> rooms = new ArrayList<>();
    private ServerSocket server;

    public ServerThreads(ServerSocket server) {
        this.server = server;
        getUsers getUsers = new getUsers();
        getUsers.start();
    }

    public class Room {
        Player creator;
        Player joiner;
        int size;
        boolean isX;

        public Room(Player creator, int size, boolean isX) {
            this.creator = creator;
            this.size = size;
            this.isX = isX;
        }

        public boolean setJoiner(Player joiner) {
            if (this.joiner == null) {
                this.joiner = joiner;
                return true;
            } else {
                return false;
            }
        }
    }

    public Room findRoom(String nickname) {
        for (Room room : rooms) {
            if (room.creator.nickname.equals(nickname)) {
                System.out.println(room.creator.nickname);
                return room;
            }
        }
        return null;
    }

    public void deleteRoom(String nickname) throws IOException {
        if (findRoom(nickname) != null) {
            rooms.remove(findRoom(nickname));
            System.out.println(rooms.size());
        }
    }

    public class Player extends Thread {
        Socket client;
        BufferedReader in;
        BufferedWriter out;
        boolean isConnected = false;
        String nickname = "";

        public Player(Socket client) throws IOException, InterruptedException {
            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        }

        public void sendRooms() throws IOException {
            for (int i = 0; i < rooms.size(); i++) {
                System.out.println("Комната: " + rooms.get(i).creator.nickname);
                out.write(rooms.get(i).creator.nickname + ". Размер поля: " + rooms.get(i).size + ". Игра за: " + ((rooms.get(i).isX) ? "Нолики": "Крестики") + "\n");
                out.flush();
            }
            out.write("0\n");
            out.flush();
        }

        @Override
        public void run() {
            while (!currentThread().isInterrupted() && !isConnected) {
                try {
                    String sentence = in.readLine();
                    String nickname = sentence.substring(0, sentence.indexOf(" "));
                    String password = sentence.substring(sentence.indexOf(" ") + 1);
                    System.out.println(nickname + " " + password);
                    String answer;
                    if (in.readLine().equals("true")) {
                        answer = (registration(nickname, password)) ? "Регистрация прошла успешно\n" : "Не удалось зарегистрироваться\n";
                    } else {
                        if (autorization(nickname, password)) {
                            answer = "Авторизация прошла успешно\n";
                            this.nickname = nickname;
                            isConnected = true;
                        } else {
                            answer = "Неверный логин и/или пароль\n";
                        }
                    }
                    out.write(answer);
                    out.flush();
                    if (isConnected) sendRooms();
                } catch (IOException e) {
                    System.out.println("Пользователь отключился");
                    players.remove(this);
                    return;
                }
            }
            while (!currentThread().isInterrupted() && isConnected) {
                try {
                    String message = in.readLine();
                    if (message.indexOf("create ") == 0) {
                        System.out.println("Пытается создать комнату");
                        String size = message.substring(message.indexOf(" ") + 1, message.lastIndexOf(" "));
                        System.out.println(message);
                        rooms.add(new Room(this, getSize(size), (message.substring(message.lastIndexOf(" ") + 1).equals("true"))));
                    } else if (message.indexOf("join ") == 0) {
                        System.out.println(message);
                        deleteRoom(message.substring(message.indexOf(" ") + 1));
                    } else if (message.equals("update")) {
                        sendRooms();
                    }
                } catch (IOException e) {
                    System.out.println("Пользователь отключился");
                    players.remove(this);
                    return;
                }
            }
        }
    }

    public int getSize(String size) {
        if (size.equals("3")) return 3;
        if (size.equals("4")) return 4;
        if (size.equals("5")) return 5;
        if (size.equals("6")) return 6;
        if (size.equals("7")) return 7;
        if (size.equals("8")) return 8;
        if (size.equals("9")) return 9;
        if (size.equals("10")) return 10;
        return 5;
    }

    public boolean registration(String nick, String pswd) {
        try {
            FileReader reader = new FileReader("src/info/acc.txt");
            BufferedReader bufferReader = new BufferedReader(reader);
            FileWriter writer = new FileWriter("src/info/acc.txt", true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            String line;
            while((line = bufferReader.readLine()) != null) {
                String name = line.substring(0, line.indexOf(" "));
                if (name.equals(nick)) {
                    System.out.println("Such player already exists");
                    return false;
                }
            }
            String sentence = nick + " " + pswd + "\n";
            bufferWriter.write(sentence);
            bufferWriter.flush();
            return true;
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean autorization(String nick, String pswd) {
        try {
            FileReader reader = new FileReader("src/info/acc.txt");
            BufferedReader bufferReader = new BufferedReader(reader);
            String line;
            while((line = bufferReader.readLine()) != null) {
                String name = line.substring(0, line.indexOf(" "));
                String pass = line.substring(line.indexOf(" ") + 1);
                if (name.equals(nick) && pswd.equals(pass)) {
                    for(int i = 0; i < players.size(); i++) {
                        if (players.get(i).nickname.equals(name)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class getUsers extends Thread {
        @Override
        public void run() {
            System.out.println("Server start working!");
            while (!currentThread().isInterrupted()) {
                try {
                    Socket client = server.accept();
                    Player player = new Player(client);
                    players.add(player);
                    player.start();
                    player.join();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
