package com.gyptor.losfapp.network.FileTransfer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileListServer {
    private static final int PORT = 5001;
    private static final String SHARED_FOLDER = "D:/videos";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[FILE_LIST_SERVER] Listening on PORT " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[FILE_LIST_SERVER] Connection from: " + socket.getInetAddress());

                File folder = new File(SHARED_FOLDER);
                File[] files = folder.listFiles((dir, name) -> !new File(dir, name).isDirectory());

                if (files == null) {
                    System.out.println("[FILE_LIST_SERVER] No files to share.");
                    socket.close();
                    continue;
                }

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // send file count
                dos.writeInt(files.length);

                for (File file : files) {
                    dos.writeUTF(file.getName());
                    dos.writeLong(file.length());
                }

                dos.flush();
                socket.close();
                System.out.println("[FILE_LIST_SERVER] File list sent to client");

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
