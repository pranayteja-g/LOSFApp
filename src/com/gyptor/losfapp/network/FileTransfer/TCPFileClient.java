package com.gyptor.losfapp.network.FileTransfer;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class TCPFileClient {
    public static void main(String[] args) {
//        String serverIp = "192.168.31.24";
        String serverIp = "localhost";
        int port = 5000;

        // file path
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Select files to send");

        int result = fileChooser.showOpenDialog(null);
        if(result != JFileChooser.APPROVE_OPTION){
            System.out.println("No files selected. Exiting...");
            return;
        }

//        File[] filesToSend = folder.listFiles(File::isFile); // lists all files. skips sub-folders
        File[] filesToSend = fileChooser.getSelectedFiles();

        try (Socket socket = new Socket(serverIp, port)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // send number of files
            assert filesToSend != null;
            dos.writeInt(filesToSend.length);

            // send all metadata first
            for(File file : filesToSend){
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
            }

            // wait for server confirmation
            String serverResponse = dis.readUTF();
            if(!serverResponse.equalsIgnoreCase("Y")){
                System.out.println("Server rejected the file transfer.");
                dis.close();
                dos.close();
                socket.close();
                return;
            }

            // send files
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
