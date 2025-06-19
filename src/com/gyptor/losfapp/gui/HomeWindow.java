package com.gyptor.losfapp.gui;

import com.gyptor.losfapp.network.FileTransfer.TCPFileClient;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HomeWindow extends JFrame {

    public HomeWindow(){
        setTitle("LOSFApp - LAN file sharing");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window
        setLayout(new BorderLayout());

        // == top: show IP ==
        JLabel ipLabel = new JLabel("Your IP: " + getLocalIP(), SwingConstants.CENTER);
        ipLabel.setFont(new Font("Arial", Font.BOLD, 18));
        ipLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(ipLabel, BorderLayout.NORTH);

        // == bottom: buttons ==
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2,20,10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30,20,30,20));

        JButton sendBtn = new JButton("Send");
        JButton receiveBtn = new JButton("Receive");

        sendBtn.setFont(new Font("Arial", Font.BOLD, 16));
        receiveBtn.setFont(new Font("Arial", Font.BOLD, 16));
//        sendBtn.setPreferredSize(new Dimension(120, 40));
//        receiveBtn.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(sendBtn);
        buttonPanel.add(receiveBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // == Action listeners ==
        /*
        *  since calling main() directly in a swing app can freeze UI,
        *  we'll run these in a separate Thread to keep the GUI responsive
        * */

        sendBtn.addActionListener(e -> {
            // Open send UI or trigger TCPFileClient
            new Thread(() -> {
                try{
                    com.gyptor.losfapp.network.FileTransfer.TCPFileClient.main(null);
                } catch (Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to start send Process. \n" + ex.getMessage());
                }
            }).start();
        });

        receiveBtn.addActionListener(e -> {
            // Open receive UI or trigger TCPFileServer
            new Thread(() -> {
                try {
                    com.gyptor.losfapp.network.FileTransfer.TCPFileServer.main(null);
                } catch (Exception ex1){
                    ex1.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to start receive process.\n" + ex1.getMessage());
                }
            }).start();
        });

    }

    public String getLocalIP(){
        try{
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e){
            return "unknown";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeWindow().setVisible(true));
    }
}
