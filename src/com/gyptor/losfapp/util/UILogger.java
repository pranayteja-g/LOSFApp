package com.gyptor.losfapp.util;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UILogger {
    private static JTextArea outputArea;

    public static void setOutputArea(JTextArea area) {
        outputArea = area;
    }

    public static void log(String message){
        if(outputArea == null) return;

        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            outputArea.append("[" + timestamp + "]" + message + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });

        System.out.println("[UI] " + message);
    }

    public static void clear(){
        if (outputArea != null){
            SwingUtilities.invokeLater(() -> outputArea.setText(""));
        }
    }
}
