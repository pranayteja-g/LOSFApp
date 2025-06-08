package com.gyptor.losfapp.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("server started. waiting for connection...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("client connected: " + clientSocket.getInetAddress());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            //Read messge from client
            String messageFromClinet = in.readLine();
            System.out.println("client says: " + messageFromClinet);

            // reply to client
            out.println("Hello from server!");

            clientSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
