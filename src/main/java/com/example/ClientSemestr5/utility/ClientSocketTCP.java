package com.example.ClientSemestr5.utility;

import lombok.Data;

import java.io.*;
import java.net.Socket;


@Data
public class ClientSocketTCP {
    private final static int PORT = 8055;
    private final static String address = "127.0.0.1";
    private Socket clientSocket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private static ClientSocketTCP instance;

    public ClientSocketTCP() throws IOException {
        this.clientSocket = new Socket(address, PORT);
        this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public static ClientSocketTCP getInstance() {
        if (instance == null) {
            try {
                instance = new ClientSocketTCP();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static void send(String request) {
        try {
            getInstance().getBufferedWriter().write(request);
            getInstance().getBufferedWriter().newLine();
            getInstance().getBufferedWriter().flush();
        }
        catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }

    }

    public static String get() {
        try {
            return getInstance().getBufferedReader().readLine();
        }
        catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }


}
