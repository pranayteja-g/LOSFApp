package com.gyptor.losfapp.gui;

import com.gyptor.losfapp.util.UILogger;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainWindow extends JFrame {

    private JTextArea logArea;
//    private JProgressBar progressBar;
    private JLabel statusLabel;

    public MainWindow() {
        setTitle("LOSFApp - LAN file sharing");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window
        setLayout(new BorderLayout());

        // ==== NORTH: IP Address ====
        JLabel ipLabel = new JLabel("Your IP: " + getLocalIP(), SwingConstants.CENTER);
        ipLabel.setFont(new Font("Arial", Font.BOLD, 18));
        ipLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(ipLabel, BorderLayout.NORTH);

        // ==== CENTER: Log Area ====
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // connect logger to text area
        UILogger.setOutputArea(logArea);

        // ==== SOUTH: Buttons, Progress & Status ====
        JPanel southPanel = new JPanel(new BorderLayout());

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        JButton sendBtn = new JButton("Send");
        JButton receiveBtn = new JButton("Receive");
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.add(sendBtn);
        buttonPanel.add(receiveBtn);
        southPanel.add(buttonPanel, BorderLayout.NORTH);

        // Status
        statusLabel = new JLabel("Status: Ready", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        southPanel.add(statusLabel, BorderLayout.CENTER);

//        // Progress Bar
//        progressBar = new JProgressBar(0, 100);
//        progressBar.setStringPainted(true);
//        southPanel.add(progressBar, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // ==== Actions ====
        sendBtn.addActionListener(e -> startSendProcess());
        receiveBtn.addActionListener(e -> startReceiveProcess());

        setVisible(true);
    }

    private void startSendProcess() {
        new Thread(() -> {
            UILogger.log("Starting peer discovery...");
            updateStatus("Discovering peers...");
            // Insert your TCPFileClient logic here, but instead of popping new windows,
            // call `log()`, `updateStatus()` and `updateProgress()` from here.
            try {
                com.gyptor.losfapp.network.FileTransfer.TCPFileClient.main(null);
                UILogger.log("Done.");
                new Timer(5000, e -> UILogger.clear()).start();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to start send Process. \n" + ex.getMessage());
            }
        }).start();
    }

    private void startReceiveProcess() {
        new Thread(() -> {
            UILogger.log("Waiting for incoming files...");
            updateStatus("Waiting for sender...");
            // Insert your TCPFileServer logic here
            try {
                com.gyptor.losfapp.network.FileTransfer.TCPFileServer.main(null);
                new Timer(5000, e -> UILogger.clear()).start();
            } catch (Exception ex1) {
                ex1.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to start receive process.\n" + ex1.getMessage());
            }
        }).start();
    }

    // ==== Helper methods ====

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText("Status: " + status));
    }

//    public void updateProgress(int value) {
//        SwingUtilities.invokeLater(() -> progressBar.setValue(value));
//    }

    private String getLocalIP() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
