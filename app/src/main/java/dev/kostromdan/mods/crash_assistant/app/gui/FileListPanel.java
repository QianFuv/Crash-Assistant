package dev.kostromdan.mods.crash_assistant.app.gui;

import javax.swing.*;
import java.nio.file.Path;
import java.util.LinkedHashSet;

public class FileListPanel {
    public final LinkedHashSet<FilePanel> filePanelList = new LinkedHashSet<>();
    private final JPanel fileListPanel;
    private final JScrollPane scrollPane;

    public FileListPanel() {
        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(fileListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
}

