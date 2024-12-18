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

        JPanel labelButtonPanel = new JPanel();
        labelButtonPanel.setLayout(new BoxLayout(labelButtonPanel, BoxLayout.X_AXIS));

        if (CrashAssistantConfig.get("modpack_modlist.enabled")) {
            ModListDiff diff = ModListUtils.getDiff();
            String labelMsg;
            JButton showModListButton = new JButton("show modlist diff");
            if (diff.addedMods().isEmpty() && diff.removedMods().isEmpty()) {
                labelMsg = "Modpack modlist wasn't changed:";
                showModListButton.setEnabled(false);
                showModListButton.setToolTipText(labelMsg);
            } else {
                labelMsg = "<html><div style='white-space:nowrap;'>Detected <span style='color:green;'>" + diff.addedMods().size() + "</span> mods added, "
                        + "<span style='color:red;'>" + diff.removedMods().size() + "</span> mods removed:</div></html>";
            }

            JLabel label = new JLabel(labelMsg);
            label.setMaximumSize(label.getPreferredSize());
            showModListButton.addActionListener(e -> showModList());

            showModListButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, showModListButton.getPreferredSize().height));

            labelButtonPanel.add(label);
            labelButtonPanel.add(Box.createHorizontalStrut(10));
            labelButtonPanel.add(showModListButton);
            labelButtonPanel.add(Box.createHorizontalGlue());

            panel.add(labelButtonPanel, BorderLayout.NORTH);
        }

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        uploadAllButton = new JButton("upload all and copy msg with links to all files");
        uploadAllButton.addActionListener(e -> uploadAllFiles());
        gbc.gridy = 0;
        bottomPanel.add(uploadAllButton, gbc);

        JButton requestHelpButton = new JButton(CrashAssistantConfig.get("text.request_help_button").toString());
        requestHelpButton.addActionListener(e -> requestHelp());
        gbc.gridy = 1;
        bottomPanel.add(requestHelpButton, gbc);

        panel.add(bottomPanel, BorderLayout.SOUTH);
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

    private void showModList() {
        // Mock function for displaying mod list
        JOptionPane.showMessageDialog(null, ModListUtils.generateDiffMsg(), "Mod List DIff", JOptionPane.INFORMATION_MESSAGE);
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
                generatedMsg += "\n";
                generatedMsg += ModListUtils.generateDiffMsg();
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
