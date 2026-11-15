
package com.pakrose.hrpayroll;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsHelper {

    public static File exportCsv(
            Context context,
            String reportName,
            List<String[]> rows) {

        BufferedWriter writer = null;

        try {
            File folder = new File(
                    context.getExternalFilesDir(
                            Environment.DIRECTORY_DOCUMENTS),
                    "PakRoseReports"
            );

            if (!folder.exists() && !folder.mkdirs()) {
                return null;
            }

            String safeName = reportName
                    .replaceAll("[^a-zA-Z0-9_-]", "_");

            String date = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    Locale.getDefault()
            ).format(new Date());

            File file = new File(
                    folder,
                    safeName + "_" + date + ".csv"
            );

            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file),
                            StandardCharsets.UTF_8
                    )
            );

            // Excel UTF-8 support
            writer.write('\uFEFF');

            for (String[] row : rows) {
                if (row == null) continue;

                for (int i = 0; i < row.length; i++) {
                    if (i > 0) writer.write(",");

                    writer.write(escapeCsv(
                            row[i] == null ? "" : row[i]
                    ));
                }

                writer.newLine();
            }

            writer.flush();
            return file;

        } catch (Exception e) {
            return null;

        } finally {
            try {
                if (writer != null) writer.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static String escapeCsv(String value) {
        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}
