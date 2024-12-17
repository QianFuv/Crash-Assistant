package dev.kostromdan.mods.crash_assistant.app.gui;

import dev.kostromdan.mods.crash_assistant.app.CrashAssistantApp;
import dev.kostromdan.mods.crash_assistant.app.utils.ClipboardUtils;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListDiff;
import dev.kostromdan.mods.crash_assistant.mod_list.ModListUtils;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
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
        JPanel buttonPanel = new JPanel(new BorderLayout());

        uploadAllButton = new JButton("upload all and copy msg with links to all files");
        uploadAllButton.addActionListener(e -> uploadAllFiles());

        buttonPanel.add(uploadAllButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        JButton requestHelpButton = new JButton(CrashAssistantConfig.get("text.request_help_button").toString());
        requestHelpButton.addActionListener(e -> requestHelp());
        panel.add(requestHelpButton, BorderLayout.SOUTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void requestHelp() {
        try {
            URI uri = new URL(CrashAssistantConfig.get("general.help_link").toString()).toURI();
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            CrashAssistantApp.LOGGER.error("Failed to open help_link in browser: ", e);
        }
    }

    private void uploadAllFiles() {
        new Thread(() -> {
            uploadAllButton.setEnabled(false);
            if (generatedMsg == null) {
                uploadAllButton.setText("Uploading...");
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
                generatedMsg = CrashAssistantConfig.get("text.msg").toString() + "\n";
                for (FilePanel panel : fileListPanel.filePanelList) {
                    generatedMsg += panel.getFileName() + ": " + panel.getUploadedLink() + "\n";
                }
                if (CrashAssistantConfig.get("modpack_modlist.enabled")) {
                    ModListDiff diff = ModListUtils.getDiff();
                    generatedMsg += "\nModlist changes beyond the modpack:\n";
                    if (diff.addedMods().isEmpty() && diff.removedMods().isEmpty()) {
                        generatedMsg += "Modpack modlist wasn't modified.\n";
                    } else {
                        generatedMsg += "Added mods:\n";
                        if (diff.addedMods().isEmpty()) {
                            generatedMsg += "Mods weren't added.\n";
                        } else {
                            generatedMsg += String.join("\n", diff.addedMods());
                        }
                        generatedMsg += "\nRemoved mods:\n";
                        if (diff.removedMods().isEmpty()) {
                            generatedMsg += "Mods weren't removed.\n";
                        } else {
                            generatedMsg += String.join("\n", diff.removedMods());
                        }
                    }
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
