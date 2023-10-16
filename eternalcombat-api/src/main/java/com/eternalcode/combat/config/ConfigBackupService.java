package com.eternalcode.combat.config;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class ConfigBackupService {

    private static final String BACKUP_FOLDER_NAME = "backup";
    private static final String BACKUP_FILE_EXTENSION = ".bak";
    private final File dataFolder;

    public ConfigBackupService(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void createBackup() {
        File backupFolder = new File(this.dataFolder, BACKUP_FOLDER_NAME);

        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        String backupFolderName = BACKUP_FOLDER_NAME + "_" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        File currentBackupFolder = new File(backupFolder, backupFolderName);
        if (!currentBackupFolder.exists()) {
            currentBackupFolder.mkdirs();
        }

        this.copyFolderContents(this.dataFolder, currentBackupFolder);
        this.deleteIfOlderDirectory(backupFolder);
    }

    private void copyFolderContents(File sourceFolder, File targetFolder) {
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            return;
        }

        if (!targetFolder.exists()) {
            boolean targetFolderCreated = targetFolder.mkdirs();

            if (!targetFolderCreated) {
                return;
            }

        }

        File[] filesToBackup = sourceFolder.listFiles();
        if (filesToBackup == null) {
            return;
        }

        for (File file : filesToBackup) {
            if (file.isDirectory() && !file.getName().equals(BACKUP_FOLDER_NAME)) {
                File subFolder = new File(targetFolder, file.getName());
                this.copyFolderContents(file, subFolder);

                continue;
            }

            if (file.isFile() && !file.getName().endsWith(BACKUP_FILE_EXTENSION)) {
                File backupFile = new File(targetFolder, file.getName() + BACKUP_FILE_EXTENSION);

                this.copyToBackupFile(file, backupFile);
            }
        }
    }

    private void copyToBackupFile(File targetFolder, File path) {
        try {
            Files.copy(targetFolder.toPath(), path.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // delete backup folders older than X days
    private void deleteIfOlderDirectory(File backupFolder) {
        File[] backupFolders = backupFolder.listFiles(File::isDirectory);

        if (backupFolders == null) {
            return;
        }

        for (File folder : backupFolders) {
            String folderName = folder.getName();
            if (!folderName.startsWith(BACKUP_FOLDER_NAME)) {
                continue;
            }

            try {
                LocalDate folderDate = LocalDate.parse(folderName.substring(BACKUP_FOLDER_NAME.length() + 1, folderName.lastIndexOf("_")));
                LocalTime folderTime = LocalTime.parse(folderName.substring(folderName.lastIndexOf("_") + 1).replace("-", ":"));
                LocalDateTime folderDateTime = LocalDateTime.of(folderDate, folderTime);
                LocalDateTime currentDateTime = LocalDateTime.now();

                long hours = ChronoUnit.HOURS.between(folderDateTime, currentDateTime);

                if (hours > 72) {
                    FileUtils.deleteDirectory(folder);
                }
            }
            catch (DateTimeParseException | IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
