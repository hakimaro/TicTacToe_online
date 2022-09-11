package com.company.model.online;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Socket client;
    private BufferedWriter out;
    private BufferedReader in;

    public Client() throws IOException {
        client = new Socket("localhost", 8000);
        out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    }

    public void sendMessage(String message) {
        try {
            System.out.println(message);
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
