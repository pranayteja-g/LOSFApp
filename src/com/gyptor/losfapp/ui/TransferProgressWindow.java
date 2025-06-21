package com.gyptor.losfapp.ui;

import javax.swing.*;
import java.awt.*;

public class TransferProgressWindow extends JFrame {

    private JLabel fileIndexLabel;
    private JLabel fileLabel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JButton homeButton;

    public TransferProgressWindow(String title) {
        setTitle(title);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        fileIndexLabel = new JLabel("File 0 of 0", SwingConstants.CENTER);
        fileLabel = new JLabel("File: ", SwingConstants.CENTER);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        statusLabel = new JLabel("Status: ", SwingConstants.CENTER);

        homeButton = new JButton("Home");
        homeButton.setVisible(false); // initially hidden
        homeButton.addActionListener(e -> {
            this.dispose();
            new com.gyptor.losfapp.gui.MainWindow().setVisible(true); // reopen home
        });

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        centerPanel.add(fileIndexLabel);
        centerPanel.add(fileLabel);
        centerPanel.add(progressBar);
        centerPanel.add(statusLabel);
        add(centerPanel, BorderLayout.CENTER);
        add(homeButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void updateFile(int index, int total, String fileName) {
        fileIndexLabel.setText("File " + index + " of " + total);
        fileLabel.setText("File: " + fileName);
        updateProgress(0);
    }

    public void updateProgress(int percent) {
        progressBar.setValue(percent);
    }

    public void updateStatus(String status) {
        statusLabel.setText("Status: " + status);
    }

    public void finish() {
        updateStatus("Done 👍");
    }
}
