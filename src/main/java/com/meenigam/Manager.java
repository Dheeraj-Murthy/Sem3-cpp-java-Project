package com.meenigam;

import com.meenigam.Components.WavFileCreator;

import java.io.File;
import java.io.IOException;

public class Manager {
    Frame frame;
    private String HomePath = "Desktop";
    private String ProjectFolder = "ProjectFiles";
    private String finalFile = "finalFile.wav";
    private String userHome = System.getProperty("user.home");
    public final String finalFilePath;

    public Manager() throws IOException {
        File baseDir = new File(userHome, HomePath);
        // Check if base directory exists
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new IOException("Base path does not exist or is not a directory: " + HomePath);
        }
        // Create the new folder
        File newFolder = new File(baseDir, ProjectFolder);
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                throw new IOException("Failed to create folder: " + newFolder.getAbsolutePath());
            }
        }
        File newFile = new File(newFolder, finalFile);
        WavFileCreator.createBlankWav(newFile.getAbsolutePath(), 10);
        finalFilePath = newFile.getAbsolutePath();

        frame = new Frame(this);
    }

    public void setHomePath(String homePath) {
        this.HomePath = homePath;
    }
}
