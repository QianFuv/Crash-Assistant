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
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available log files:"));
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
    public JPanel getFileListPanel() {
        return fileListPanel;
    }

    public void addFile(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {
            FilePanel filePanel = new FilePanel(fileName);
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

