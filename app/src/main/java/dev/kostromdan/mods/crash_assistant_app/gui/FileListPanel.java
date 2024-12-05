package dev.kostromdan.mods.crash_assistant_app.gui;

import dev.kostromdan.mods.crash_assistant_app.CrashAssistantApp;
import dev.kostromdan.mods.crash_assistant_app.exceptions.UploadException;
import dev.kostromdan.mods.crash_assistant_app.utils.ClipboardUtils;
import gs.mclo.api.response.UploadLogResponse;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FileListPanel {
    private final JPanel fileListPanel;
    private final JScrollPane scrollPane;
    public final LinkedHashSet<FilePanel> filePanelList = new LinkedHashSet<>();

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
}

