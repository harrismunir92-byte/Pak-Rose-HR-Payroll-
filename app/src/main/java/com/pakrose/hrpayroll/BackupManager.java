package com.pakrose.hrpayroll;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BackupManager {

    private static final String DATABASE_NAME = "pakrose_hr.db";

    public static File createBackup(Context context) {
        try {
            File database = context.getDatabasePath(DATABASE_NAME);

            if (!database.exists()) {
                return null;
            }

            File folder = new File(
                    context.getExternalFilesDir(
                            Environment.DIRECTORY_DOCUMENTS),
                    "PakRoseBackups"
            );

            if (!folder.exists() && !folder.mkdirs()) {
                return null;
            }

            String timestamp = String.valueOf(
                    System.currentTimeMillis());

            File backup = new File(
                    folder,
                    "PakRose_Backup_" + timestamp + ".db"
            );

            copyFile(database, backup);

            return backup.exists() ? backup : null;

        } catch (Exception e) {
            return null;
        }
    }

    public static boolean restoreBackup(
            Context context, File backupFile) {

        if (backupFile == null || !backupFile.exists()) {
            return false;
        }

        try {
            File database = context.getDatabasePath(DATABASE_NAME);

            File parent = database.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // Remove old database sidecar files
            deleteFile(new File(database.getPath() + "-wal"));
            deleteFile(new File(database.getPath() + "-shm"));

            copyFile(backupFile, database);

            return database.exists()
                    && database.length() > 0;

        } catch (Exception e) {
            return false;
        }
    }

    private static void copyFile(
            File source, File destination) throws IOException {

        try (FileInputStream input =
                     new FileInputStream(source);
             FileOutputStream output =
                     new FileOutputStream(destination)) {

            byte[] buffer = new byte[8192];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
        }
    }

    private static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
