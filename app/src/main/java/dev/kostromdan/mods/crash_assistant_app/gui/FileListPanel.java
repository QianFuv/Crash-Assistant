package dev.kostromdan.mods.crash_assistant_app.gui;

import javax.swing.*;
import java.awt.*;

public class FileListPanel {
    private JPanel fileListPanel;
    private JScrollPane scrollPane;

    public FileListPanel() {
        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(fileListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available log files:"));

        this.addFile("lastest.log");
        this.addFile("debug.log");
        this.addFile("crash-2024-11-29_14.34.03-client.txt");
        this.addFile("crash-2024-11-29_14.35.07-fml.txt");
        this.addFile("hs_err_pid29788.log");
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void addFile(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {
            FilePanel filePanel = new FilePanel(fileName, this);
            fileListPanel.add(filePanel.getPanel());
            fileListPanel.revalidate();
        }
    }

    public void uploadAllFiles() {
        Component[] components = fileListPanel.getComponents();
        if (components.length == 0) {
            JOptionPane.showMessageDialog(null, "No files to upload!");
            return;
        }

        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel filePanel = (JPanel) component;
                JLabel fileNameLabel = (JLabel) filePanel.getComponent(0);
                String fileName = fileNameLabel.getText();
                System.out.println("Uploading: " + fileName);
            }
        }
        JOptionPane.showMessageDialog(null, "All files uploaded successfully!");
    }
}

