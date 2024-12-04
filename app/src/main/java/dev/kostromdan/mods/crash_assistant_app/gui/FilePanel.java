package dev.kostromdan.mods.crash_assistant_app.gui;

import dev.kostromdan.mods.crash_assistant_app.CrashAssistantApp;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class FilePanel {
    private JPanel panel;
    private Path file;
    private String fileName;

    public FilePanel(String fileName, Path file) {
        this.file = file;

        this.fileName = fileName;

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel fileNameLabel = new JLabel(fileName);
        panel.add(fileNameLabel, BorderLayout.CENTER);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(10, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        JButton openButton = new JButton("open");
        openButton.setPreferredSize(new Dimension(62, 25));
        openButton.addActionListener(e -> openFile());

        JButton showButton = new JButton("show in explorer");
        showButton.setPreferredSize(new Dimension(129, 25));
        showButton.addActionListener(e -> showInExplorer());

        JButton uploadButton = new JButton("upload and copy link");
        uploadButton.setPreferredSize(new Dimension(149, 25));
        uploadButton.addActionListener(e -> uploadFile());

        buttonPanel.add(spacerPanel);
        buttonPanel.add(openButton);
        buttonPanel.add(showButton);
        buttonPanel.add(uploadButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        panel.setMinimumSize(new Dimension(0, 35));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Opens the file using the default application associated with its type.
     */
    private void openFile() {
        try {
            Desktop.getDesktop().open(file.toFile());
        } catch (IOException e) {
            CrashAssistantApp.LOGGER.error("Failed to open file: ", e);
        }
    }


    /**
     * Opens the file's directory in the system file explorer and selects the file.
     */
    private void showInExplorer() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("explorer.exe", "/select,", file.toAbsolutePath().toString()).start();
            } else {
                Desktop.getDesktop().open(file.toFile().getParentFile());
            }
        } catch (Exception e) {
            CrashAssistantApp.LOGGER.error("Failed to show file in explorer: ", e);
        }

    }

    private void uploadFile() {
        System.out.println("Uploading file: " + fileName);
        JOptionPane.showMessageDialog(null, "File '" + fileName + "' uploaded.");
    }
}


