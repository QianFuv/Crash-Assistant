package dev.kostromdan.mods.crash_assistant_app.gui;

import javax.swing.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileListPanel {
    private JPanel fileListPanel;
    private JScrollPane scrollPane;
    private final Set<FilePanel> filePanelList = new HashSet<>();

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

    public void addFile(String fileName, Path file) {
        FilePanel filePanel = new FilePanel(fileName, file);
        filePanelList.add(filePanel);
        fileListPanel.add(filePanel.getPanel());
        fileListPanel.revalidate();
    }

    public void uploadAllFiles() {
        HashMap<String, String> nameLink = new HashMap<>();
        for (FilePanel panel : filePanelList) {
            panel.uploadFile(false);
            nameLink.put(panel.getFileName(), panel.getUploadButtonText() != "Empty file!" ? panel.getUploadedLink() : "Empty file!");
        }
    }
}

