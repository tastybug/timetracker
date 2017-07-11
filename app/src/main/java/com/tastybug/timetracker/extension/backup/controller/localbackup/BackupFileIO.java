package com.tastybug.timetracker.extension.backup.controller.localbackup;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

class BackupFileIO {

    private static final String BACKUP_FOLDER_NAME = "timetracker-backup";
    private static final String BACKUP_FILE_NAME = "timetracker-backup.file";
    private Context context;

    BackupFileIO(Context context) {
        this.context = context;
    }

    void writeBackup(byte[] data) throws IOException {
        File backupFile = new File(getTargetDirectory(), new Random().nextLong() + ".file");
        writeToFile(data, backupFile);
        backupFile = replaceOriginalBackupFile(backupFile);
        logInfo(getClass().getSimpleName(), "Successfully wrote backup to %s.", backupFile.getAbsolutePath());
    }

    byte[] readBackup() throws IOException {
        File backupFile = getBackupFile();
        if (!backupFile.exists()) {
            return new byte[0];
        } else {
            try (FileInputStream inputStream = new FileInputStream(getBackupFile())) {
                return IOUtils.toByteArray(inputStream);
            }
        }
    }

    private File replaceOriginalBackupFile(File backupFile) {
        File originalBackup = getBackupFile();
        if (originalBackup.exists()) {
            if (!originalBackup.delete()) {
                throw new RuntimeException("Failed to delete original: " + originalBackup.getAbsolutePath());
            }
        }
        if (!backupFile.renameTo(originalBackup)) {
            throw new RuntimeException("Failed to rename backup to target file name which is" + originalBackup.getAbsolutePath());
        }
        return originalBackup;
    }

    File getBackupFile() {
        return new File(getTargetDirectory(), BACKUP_FILE_NAME);
    }

    private void writeToFile(byte[] data, File target) throws IOException {
        try (FileWriter output = new FileWriter(target)) {
            IOUtils.write(data, output, "UTF-8");
        }
    }

    private File getTargetDirectory() {
        return context.getDir(BACKUP_FOLDER_NAME, Context.MODE_PRIVATE);
    }
}
