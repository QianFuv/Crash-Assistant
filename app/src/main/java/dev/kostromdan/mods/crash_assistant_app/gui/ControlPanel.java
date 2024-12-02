package dev.kostromdan.mods.crash_assistant_app.gui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel {
    private JPanel panel;
    private FileListPanel fileListPanel;

    public ControlPanel(FileListPanel fileListPanel) {
        this.fileListPanel = fileListPanel;

        panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton uploadAllButton = new JButton("upload all and copy report msg");
        uploadAllButton.addActionListener(e -> uploadAllFiles());

        buttonPanel.add(uploadAllButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        JButton joinDiscordButton = new JButton("join Discord");
        panel.add(joinDiscordButton, BorderLayout.EAST);
    }

    public JPanel getPanel() {
        return panel;
    }

    private void uploadAllFiles() {
        fileListPanel.uploadAllFiles();
    }
}

