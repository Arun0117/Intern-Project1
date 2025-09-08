# File Compressor & Decompressor (Swing, Java)

A desktop tool to zip and unzip files with progress bar, multi-file compression, and compression stats.

## Features
- Select multiple files to compress into one ZIP
- Decompress any ZIP to a chosen folder
- Progress bar for long operations
- Logs with original size, compressed size, and compression ratio
- Simple, dependency-free Swing UI (uses only the JDK)

## How to Run
### Compile
```bash
javac -d out $(find src -name "*.java")
```

On Windows (PowerShell):
```powershell
Get-ChildItem -Recurse -Filter *.java src | % { $_.FullName } | Set-Content sources.txt
javac -d out @sources.txt
```

### Run
```bash
java -cp out com.example.compressor.Main
```

### Make Runnable JAR (optional)
```bash
jar --create --file app.jar --main-class com.example.compressor.Main -C out .
```

Then run:
```bash
java -jar app.jar
```

## Notes
- This project uses **Swing** to avoid JavaFX setup friction.
- Works with JDK 11+ (tested code is standard Java 8+ API).
- For very large files, progress is approximate based on total bytes vs bytes processed.
