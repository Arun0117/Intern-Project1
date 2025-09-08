package com.example.compressor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private final DefaultListModel<File> fileListModel = new DefaultListModel<>();
    private final JList<File> fileList = new JList<>(fileListModel);
    private final JTextField outputZipField = new JTextField();
    private final JTextField inputZipField = new JTextField();
    private final JTextField extractDirField = new JTextField();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JTextArea logArea = new JTextArea(10, 60);

    public Main() {
        super("File Compressor & Decompressor (Swing)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildCompressionPanel(), BorderLayout.NORTH);
        add(buildDecompressionPanel(), BorderLayout.CENTER);
        add(buildLogPanel(), BorderLayout.SOUTH);

        progressBar.setStringPainted(true);
        setMinimumSize(new Dimension(820, 680));
        setLocationRelativeTo(null);
    }

    private JPanel buildCompressionPanel() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBorder(BorderFactory.createTitledBorder("Compression"));

        // File list with controls
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Files to compress:"), BorderLayout.NORTH);
        fileList.setVisibleRowCount(6);
        left.add(new JScrollPane(fileList), BorderLayout.CENTER);

        JPanel fileButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        fileButtons.add(new JButton(new AbstractAction("Add Files") {
            @Override public void actionPerformed(ActionEvent e) { onAddFiles(); }
        }));
        fileButtons.add(new JButton(new AbstractAction("Remove Selected") {
            @Override public void actionPerformed(ActionEvent e) { onRemoveSelected(); }
        }));
        fileButtons.add(new JButton(new AbstractAction("Clear") {
            @Override public void actionPerformed(ActionEvent e) { fileListModel.clear(); }
        }));
        left.add(fileButtons, BorderLayout.SOUTH);

        panel.add(left, BorderLayout.WEST);

        // Output zip chooser and compress button
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel outZipRow = new JPanel(new BorderLayout(6,6));
        outZipRow.add(new JLabel("Output ZIP:"), BorderLayout.WEST);
        outputZipField.setEditable(false);
        outZipRow.add(outputZipField, BorderLayout.CENTER);
        JButton chooseOutZip = new JButton("Choose...");
        chooseOutZip.addActionListener(e -> onChooseOutputZip());
        outZipRow.add(chooseOutZip, BorderLayout.EAST);
        right.add(outZipRow);

        right.add(Box.createVerticalStrut(8));
        right.add(progressBar);
        right.add(Box.createVerticalStrut(8));

        JButton compressBtn = new JButton("Compress");
        compressBtn.addActionListener(e -> onCompress());
        compressBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(compressBtn);

        panel.add(right, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildDecompressionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Decompression"));

        JPanel zipRow = new JPanel(new BorderLayout(6,6));
        zipRow.add(new JLabel("ZIP to extract:"), BorderLayout.WEST);
        inputZipField.setEditable(false);
        zipRow.add(inputZipField, BorderLayout.CENTER);
        JButton chooseZip = new JButton("Choose...");
        chooseZip.addActionListener(e -> onChooseInputZip());
        zipRow.add(chooseZip, BorderLayout.EAST);
        panel.add(zipRow);

        JPanel dirRow = new JPanel(new BorderLayout(6,6));
        dirRow.add(new JLabel("Extract to directory:"), BorderLayout.WEST);
        extractDirField.setEditable(false);
        dirRow.add(extractDirField, BorderLayout.CENTER);
        JButton chooseDir = new JButton("Choose...");
        chooseDir.addActionListener(e -> onChooseExtractDir());
        dirRow.add(chooseDir, BorderLayout.EAST);
        panel.add(dirRow);

        JButton extractBtn = new JButton("Decompress");
        extractBtn.addActionListener(e -> onDecompress());
        extractBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(6));
        panel.add(extractBtn);

        return panel;
    }

    private JScrollPane buildLogPanel() {
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(logArea);
        sp.setBorder(BorderFactory.createTitledBorder("Log & Stats"));
        return sp;
    }

    private void onAddFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File f : chooser.getSelectedFiles()) {
                fileListModel.addElement(f);
            }
            log("Added %d file(s).", chooser.getSelectedFiles().length);
        }
    }

    private void onRemoveSelected() {
        List<File> selected = fileList.getSelectedValuesList();
        for (File f : selected) {
            fileListModel.removeElement(f);
        }
    }

    private void onChooseOutputZip() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose output ZIP");
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP files", "zip"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".zip")) {
                f = new File(f.getParentFile(), f.getName() + ".zip");
            }
            outputZipField.setText(f.getAbsolutePath());
        }
    }

    private void onChooseInputZip() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose ZIP file to extract");
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP files", "zip"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            inputZipField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onChooseExtractDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose directory to extract to");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            extractDirField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void onCompress() {
        if (fileListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Add at least one file.", "No files", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String outPath = outputZipField.getText().trim();
        if (outPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose an output ZIP path.", "Output missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<File> files = new ArrayList<>();
        long totalBytes = 0;
        for (int i = 0; i < fileListModel.size(); i++) {
            File f = fileListModel.get(i);
            if (f.isFile()) {
                files.add(f);
                totalBytes += f.length();
            }
        }
        final long grandTotal = Math.max(totalBytes, 1); // avoid division by zero

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setValue(0);
                log("Compressing %d file(s) to %s ...", files.size(), outPath);
                CompressionStats stats = Compressor.compress(files, new File(outPath), (processed) -> {
                    int pct = (int) Math.min(100, Math.round((processed * 100.0) / grandTotal));
                    publish(pct);
                });
                log("Compression done.");
                log("Original size: %,d bytes", stats.getOriginalBytes());
                log("Compressed size: %,d bytes", stats.getCompressedBytes());
                log("Compression ratio: %.2f%%", stats.getCompressionRatio() * 100);
                return null;
            }
            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) progressBar.setValue(chunks.get(chunks.size() - 1));
            }
            @Override
            protected void done() {
                progressBar.setValue(100);
            }
        };
        worker.execute();
    }

    private void onDecompress() {
        String zipPath = inputZipField.getText().trim();
        String outDir = extractDirField.getText().trim();
        if (zipPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose a ZIP file to extract.", "ZIP missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (outDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose an output directory.", "Output dir missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setValue(0);
                log("Extracting %s to %s ...", zipPath, outDir);
                Decompressor.extract(new File(zipPath), new File(outDir), (pct) -> publish(pct));
                log("Extraction done.");
                return null;
            }
            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) progressBar.setValue(chunks.get(chunks.size() - 1));
            }
            @Override
            protected void done() {
                progressBar.setValue(100);
            }
        };
        worker.execute();
    }

    private void log(String fmt, Object... args) {
        logArea.append(String.format(fmt, args) + System.lineSeparator());
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
