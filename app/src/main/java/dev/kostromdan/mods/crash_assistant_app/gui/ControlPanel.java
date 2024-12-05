package dev.kostromdan.mods.crash_assistant_app.gui;

import dev.kostromdan.mods.crash_assistant_app.CrashAssistantApp;
import dev.kostromdan.mods.crash_assistant_app.utils.ClipboardUtils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ControlPanel {
    private final JPanel panel;
    private final FileListPanel fileListPanel;
    private final JButton uploadAllButton;
    private String generatedMsg = null;

    public ControlPanel(FileListPanel fileListPanel) {
        this.fileListPanel = fileListPanel;

        panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        uploadAllButton = new JButton("upload all and copy msg with links to all files");
        uploadAllButton.addActionListener(e -> uploadAllFiles());

        buttonPanel.add(uploadAllButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        JButton joinDiscordButton = new JButton("request help in our Discord");
        joinDiscordButton.addActionListener(e -> joinDiscord());
        panel.add(joinDiscordButton, BorderLayout.EAST);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void joinDiscord(){
        try {
            Desktop.getDesktop().browse(new URL("https://discord.gg/moddedmc").toURI());
        } catch (Exception e) {
            CrashAssistantApp.LOGGER.error("Failed to open Discord invite link: ", e);
        }
    }

    private void uploadAllFiles() {
        new Thread(() -> {
            uploadAllButton.setEnabled(false);
            if (generatedMsg == null) {
                uploadAllButton.setText("Uploading!");
                for (FilePanel panel : fileListPanel.filePanelList) {
                    panel.uploadFile(false);
                }
                outerLoop:
                while (true) {
                    int successCounter = 0;
                    for (FilePanel filePanel : fileListPanel.filePanelList) {
                        if (filePanel.getLastError() != null) {
                            JOptionPane.showMessageDialog(
                                    panel,
                                    "Failed to upload file \"" + filePanel.getFilePath() + "\": " + filePanel.getLastError(),
                                    "Failed to upload file!",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            uploadAllButton.setText("Error!");
                            new java.util.Timer().schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            uploadAllButton.setText("upload all and copy msg with links to all files");
                                            uploadAllButton.setEnabled(true);
                                        }
                                    },
                                    3000
                            );
                            return;
                        }
                        if (filePanel.getUploadedLink() != null) {
                            successCounter++;
                        }
                        if (successCounter == fileListPanel.filePanelList.size()) {
                            break outerLoop;
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
                generatedMsg = "Minecraft crashed!\n";
                for (FilePanel panel : fileListPanel.filePanelList) {
                    generatedMsg += panel.getFileName() + ": " + panel.getUploadedLink() + "\n";
                }
            }
            ClipboardUtils.copy(generatedMsg);
            uploadAllButton.setText("Copied!");
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            uploadAllButton.setText("copy msg with links to all files");
                            uploadAllButton.setEnabled(true);
                        }
                    },
                    3000
            );
        }).start();
    }
}
