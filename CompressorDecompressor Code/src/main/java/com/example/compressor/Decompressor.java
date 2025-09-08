package com.example.compressor;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompressor {

    public interface ProgressCallback { void onProgress(int percent); }

    public static void extract(File zipFile, File outputDir, ProgressCallback callback) throws IOException {
        if (!outputDir.exists()) outputDir.mkdirs();

        long totalBytes = zipFile.length();
        long processed = 0L;

        try (FileInputStream fis = new FileInputStream(zipFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)) {

            byte[] buffer = new byte[8192];
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(outputDir, entry.getName());
                    dir.mkdirs();
                    zis.closeEntry();
                    continue;
                }
                File outFile = new File(outputDir, entry.getName());
                // Ensure parent directories
                File parent = outFile.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outFile);
                     BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                        processed += len;
                        if (callback != null && totalBytes > 0) {
                            int pct = (int) Math.min(100, Math.round((processed * 100.0) / totalBytes));
                            callback.onProgress(pct);
                        }
                    }
                }
                outFile.setLastModified(entry.getTime());
                zis.closeEntry();
            }
        }
        if (callback != null) callback.onProgress(100);
    }
}
