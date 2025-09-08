package com.example.compressor;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {

    public interface ProgressCallback { void onProgress(long processedBytes); }

    public static CompressionStats compress(List<File> files, File outputZip, ProgressCallback callback) throws IOException {
        long originalBytes = 0L;
        for (File f : files) originalBytes += f.length();

        // Ensure parent dirs
        File parent = outputZip.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        long processed = 0L;

        try (FileOutputStream fos = new FileOutputStream(outputZip);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            byte[] buffer = new byte[8192];
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {

                    ZipEntry entry = new ZipEntry(file.getName());
                    // Preserve last modified time
                    entry.setTime(file.lastModified());
                    zos.putNextEntry(entry);

                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
                        processed += len;
                        if (callback != null) callback.onProgress(processed);
                    }
                    zos.closeEntry();
                }
            }
        }

        long compressedBytes = outputZip.length();
        return new CompressionStats(originalBytes, compressedBytes);
    }
}
