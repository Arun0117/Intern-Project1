package com.example.compressor;

public class CompressionStats {
    private final long originalBytes;
    private final long compressedBytes;

    public CompressionStats(long originalBytes, long compressedBytes) {
        this.originalBytes = originalBytes;
        this.compressedBytes = compressedBytes;
    }

    public long getOriginalBytes() { return originalBytes; }
    public long getCompressedBytes() { return compressedBytes; }

    public double getCompressionRatio() {
        if (originalBytes <= 0) return 1.0;
        return (double) compressedBytes / (double) originalBytes;
    }
}
