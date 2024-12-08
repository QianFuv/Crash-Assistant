package dev.kostromdan.mods.crash_assistant_app.gui;

import dev.kostromdan.mods.crash_assistant_app.CrashAssistantApp;
import dev.kostromdan.mods.crash_assistant_app.exceptions.UploadException;
import dev.kostromdan.mods.crash_assistant_app.utils.ClipboardUtils;
import gs.mclo.api.response.UploadLogResponse;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class FilePanel {
    private final JPanel panel;
    private final Border thinBorder;
    private final JButton uploadButton;
    private final Path filePath;
    private final String fileName;
    private String uploadedLink = null;
    private Exception lastError = null;

    public FilePanel(String fileName, Path filePath) {
        this.filePath = filePath;

        this.fileName = fileName;

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel fileNameLabel = new JLabel(fileName);
        panel.add(fileNameLabel, BorderLayout.CENTER);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(10, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        thinBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(new JButton().getBorder().getBorderInsets(new JButton()).top, 9, new JButton().getBorder().getBorderInsets(new JButton()).bottom, 9)
        );


        JButton openButton = createButton("open", e -> openFile());

        JButton showButton = createButton("show in explorer", e -> showInExplorer());

        uploadButton = createButton("upload and copy link", e -> uploadFile());


        buttonPanel.add(spacerPanel);
        buttonPanel.add(openButton);
        buttonPanel.add(showButton);
        buttonPanel.add(uploadButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        panel.setMinimumSize(new Dimension(0, 35));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }

    public static JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 25));
        return button;
    }

    public static JButton createButtonWithBorder(String text, ActionListener actionListener, Border border) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setBorder(border);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 25));
        return button;
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Opens the file using the default application associated with its type.
     */
    private void openFile() {
        try {
            Desktop.getDesktop().open(filePath.toFile());
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
                new ProcessBuilder("explorer.exe", "/select,", filePath.toAbsolutePath().toString()).start();
            } else {
                Desktop.getDesktop().open(filePath.toFile().getParentFile());
            }
        } catch (Exception e) {
            CrashAssistantApp.LOGGER.error("Failed to show file in explorer: ", e);
        }

    }

    private void openInBrowser() {
        try {
            Desktop.getDesktop().browse(new URL(uploadedLink).toURI());
        } catch (Exception e) {
            CrashAssistantApp.LOGGER.error("Failed to open in link browser: ", e);
        }

    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadedLink() {
        return uploadedLink;
    }

    public String getUploadButtonText() {
        return uploadButton.getText();
    }

    public Path getFilePath() {
        return filePath;
    }

    public Exception getLastError() {
        return lastError;
    }

    private void uploadFile() {
        uploadFile(true);
    }

    public void uploadFile(boolean fromButton) {
        new Thread(() -> {
            if (uploadedLink == null) {
                lastError = null;
                uploadButton.setEnabled(false);
                uploadButton.setText("Uploading...");

                try {
                    UploadLogResponse response = CrashAssistantApp.MCLogsClient.uploadLog(filePath).get();
                    response.setClient(CrashAssistantApp.MCLogsClient);

                    if (response.isSuccess()) {
                        uploadedLink = response.getUrl();
                    } else {
                        throw new UploadException("An error occurred when uploading file: " + response.getError());
                    }
                } catch (IOException | ExecutionException | InterruptedException | UploadException e) {
                    {
                        lastError = e;
                        CrashAssistantApp.LOGGER.info("Failed to upload file \"" + filePath + "\": ", e);
                        uploadButton.setText("Error!");
                        if (fromButton) {
                            JOptionPane.showMessageDialog(
                                    panel,
                                    "Failed to upload file \"" + filePath + "\": " + e,
                                    "Failed to upload file!",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        uploadButton.setText("upload and copy link");
                                        uploadButton.setEnabled(true);
                                    }
                                },
                                3000
                        );
                        return;
                    }
                }
            }
            if (fromButton) {
                ClipboardUtils.copy(uploadedLink);
                uploadButton.setText("Copied!");
                uploadButton.setEnabled(false);
            }
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            uploadButton.setText("copy link");
                            uploadButton.setEnabled(true);
                            panel.revalidate();
                        }
                    },
                    fromButton ? 2000 : 0
            );
        }).start();
    }
}


