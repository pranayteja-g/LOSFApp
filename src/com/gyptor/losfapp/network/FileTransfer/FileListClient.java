package com.gyptor.losfapp.network.FileTransfer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class FileListClient {
    public static void main(String[] args) {
//        String peerIp = "192.168.31.24";
        String peerIp = "localhost";
        int port = 5001;

        try (Socket socket = new Socket(peerIp, port)) {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            int fileCount = dis.readInt();
            System.out.println("Files available on " + peerIp + ":");

            for (int i = 0; i < fileCount; i++) {
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();

                System.out.printf("- %s (%.2f MB)%n", fileName, fileSize / (1024.0 * 1024));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
