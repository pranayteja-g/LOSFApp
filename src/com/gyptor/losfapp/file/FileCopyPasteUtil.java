package com.gyptor.losfapp.file;

import java.io.*;
import java.nio.file.Path;

public class FileCopyPasteUtil {

    public void copyPaste(Path source, Path destination) {
        try (
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(String.valueOf(source)));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(String.valueOf(destination)))
        ) {
            byte[] buffer = new byte[8192]; // 8Kb buffer
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            System.out.println("done!");
        } catch (IOException e) {
            System.out.println("file not found?" + e.getMessage());
        }
    }
}
