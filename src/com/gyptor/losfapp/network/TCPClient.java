package com.gyptor.losfapp.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) {
        String serverIP = "localhost";
        int port = 5000;

        try (Socket socket = new Socket(serverIP, port)) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // send a message
            out.println("hello form client!");

            // read server's reply
            String reply = in.readLine();
            System.out.println("server says: " + reply);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
