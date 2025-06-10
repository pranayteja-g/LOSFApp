package com.gyptor.losfapp.network.FileTransfer;

import com.gyptor.losfapp.network.UDPdiscovery.ServerAnnouncer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TCPFileServer {
    public static void main(String[] args) {
        int port = 5000;

        ServerAnnouncer announcer = new ServerAnnouncer(port);
        announcer.start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("server started, waiting on port " + port + "...");

            Socket socket = serverSocket.accept();
            System.out.println("client connected: " + socket.getInetAddress());

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // receive the number of files
            int fileCount = dis.readInt();
            String[] fileNames = new String[fileCount];
            long[] fileSizes = new long[fileCount];

            // get filenames and sizes
            for (int i = 0; i < fileCount; i++) {
                fileNames[i] = dis.readUTF();
                fileSizes[i] = dis.readLong();
            }

            // Display to the user and ask for confirmation
            System.out.println("Files requested for transfer: ");
            for (int i = 0; i < fileCount; i++) {
                System.out.println(" - " + fileNames[i] + " (" + fileSizes[i] + " bytes)");
            }
            Scanner scaner = new Scanner(System.in);
            System.out.println("Accept these files? [Y/N]: ");
            String response = scaner.nextLine().trim();

            dos.writeUTF(response);

            if (!response.equalsIgnoreCase("Y")) {
                System.out.println("Transfer rejected. closing connection");
                dis.close();
                dos.close();
                socket.close();
                return;
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File batchFolder = new File("recievedFiles/recieved_" + timestamp);
            if (!batchFolder.exists()) {
                if (!batchFolder.mkdirs()) {
                    System.err.println("Failed to create directory: " + batchFolder.getAbsolutePath());
                    dis.close();
                    dos.close();
                    socket.close();
                    return;
                }
            }
            System.out.println("Saving files to folder: " + batchFolder.getAbsolutePath());

            // accepting and saving files
            for (int i = 0; i < fileCount; i++) {
                String fileName = fileNames[i].replaceAll("[\\\\/:*?\"<>|]", "_");
                long fileSize = fileSizes[i];
                System.out.println("Receiving file: " + fileName + " | size: " + fileSize);

                // save to disk
                File outputFile = new File(batchFolder, fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;

                while (totalRead < fileSize) {
                    int chunkSize = buffer.length;
                    if (fileSize - totalRead < chunkSize) {
                        chunkSize = (int) (fileSize - totalRead); // safe cast
                    }

                    bytesRead = dis.read(buffer, 0, chunkSize);
                    if (bytesRead == -1) {
                        System.out.println("unexpected end of stream. file may be incomplete.");
                        break;
                    }

                    fos.write(buffer, 0, chunkSize);
                    totalRead += bytesRead;

                    int progress = (int) ((totalRead * 100) / fileSize);
                    System.out.print("\rProgress: " + progress + "%");
                }


                fos.close();
                System.out.println("\rProgress: 100%");
                System.out.println("File received succesfully as: " + outputFile.getName());

            }
            announcer.stopAnnouncing();
            dis.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
