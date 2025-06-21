package com.gyptor.losfapp.network.FileTransfer;

import com.gyptor.losfapp.network.UDPdiscovery.Peer;
import com.gyptor.losfapp.network.UDPdiscovery.PeerDiscoveryService;
import com.gyptor.losfapp.ui.TransferProgressWindow;
import com.gyptor.losfapp.util.UILogger;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class TCPFileClient {
    public static void main(String[] args) {

        PeerDiscoveryService discoveryService = new PeerDiscoveryService();

        // wait a few seconds for peer discovery
        UILogger.log("waiting 5 seconds to discover peers");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
        }

        List<Peer> peers = discoveryService.getDiscoveredPeers();
        if (peers.isEmpty()) {
            UILogger.log("No peers discovered. Exiting...");
            return;
        }

        // display peers
        UILogger.log("Discovered peers");
        for (int i = 0; i < peers.size(); i++) {
            UILogger.log((i + 1) + ". " + peers.get(i).getAddress().getHostAddress());
        }

        // let user select
        String[] peerIps = peers.stream()
                .map(p -> p.getAddress().getHostAddress())
                .toArray(String[]::new);

        String selectedPeer = (String) JOptionPane.showInputDialog(
                null,
                "Select a peer to send file to",
                "peer Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                peerIps,
                peerIps[0]
        );

        if (selectedPeer == null || selectedPeer.isEmpty()) {
            UILogger.log("no peer selected.exiting");
            return;
        }

//        String serverIp = "192.168.31.24";
        int port = 5000;

        // a temporary JFrame just to own the dialog
        JFrame dummyFrame = new JFrame();
        dummyFrame.setAlwaysOnTop(true);  // makes sure the dialog is on top
        dummyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dummyFrame.setUndecorated(true);  // Optional: hide window borders
        dummyFrame.setLocationRelativeTo(null); // Center it
        dummyFrame.setVisible(true);

        // file path
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Select files to send");

        int result = fileChooser.showOpenDialog(dummyFrame);
        dummyFrame.dispose();
        if (result != JFileChooser.APPROVE_OPTION) {
            UILogger.log("No files selected. Exiting...");
            return;
        }

        TransferProgressWindow progressWindow = new TransferProgressWindow("Sending Files");
//      File[] filesToSend = folder.listFiles(File::isFile); // lists all files. skips sub-folders
        File[] filesToSend = fileChooser.getSelectedFiles();

        try (Socket socket = new Socket(selectedPeer, port)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // send number of files
            assert filesToSend != null;
            dos.writeInt(filesToSend.length);

            // send all metadata first
            for (File file : filesToSend) {
                String cleanFileName = file.getName().replaceAll("[\\\\\\\\/:*?\\\"<>|]", "_");
                dos.writeUTF(cleanFileName);
                dos.writeLong(file.length());
            }

            // wait for server confirmation
            String serverResponse = dis.readUTF();
            if (!serverResponse.equalsIgnoreCase("Y")) {
                UILogger.log("Server rejected the file transfer.");
                dis.close();
                dos.close();
                socket.close();
                return;
            }

            // send files
            for (int i = 0; i < filesToSend.length; i++) {
                File file = filesToSend[i];
                long filesSize = file.length();

                progressWindow.updateFile(i + 1, filesToSend.length, file.getName());
                progressWindow.updateStatus("sending...");

                // send bytes
                FileInputStream fis = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalSent = 0;
                long fileSize = file.length();

                UILogger.log("Sending: " + file.getName() + " (" + fileSize + " bytes");

                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                    totalSent += bytesRead;

                    int progress = (int) ((totalSent * 100) / fileSize);
                    SwingUtilities.invokeLater(() -> progressWindow.updateProgress(progress));
                    System.out.print("\rProgress: " + progress + "%");
                }

                fis.close();
                progressWindow.updateStatus("sent: " + file.getName());
                Thread.sleep(400);
//                UILogger.log("sent file: " + file.getName());
            }

            dos.flush();
            progressWindow.finish();
            progressWindow.dispose();
            UILogger.log("all files sent.");
        } catch (IOException e) {
            UILogger.log("Error: " + e.getMessage());
            System.err.println("Failed to connect to " + selectedPeer + " on port " + port + " " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
