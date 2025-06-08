package com.gyptor.losfapp.network.FileTransfer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPFileClient {
    public static void main(String[] args) {
        String serverIp = "192.168.31.24";
//        String serverIp = "localhost";
        int port = 5000;

        // file path
        File folder = new File("D:/videos/");
        File[] filesToSend = folder.listFiles(File::isFile); // lists all files. skips sub-folders

        try (Socket socket = new Socket(serverIp, port)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // send number of files
            assert filesToSend != null;
            dos.writeInt(filesToSend.length);

            // send all metadata first
            for(File file : filesToSend){
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
            }

            for (File file : filesToSend) {
                // send bytes
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }

                fis.close();
                System.out.println("sent file: " + file.getName());
            }

            dos.flush();
            System.out.println("all files sent.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
