package dev.kostromdan.mods.crash_assistant_app.utils;

import java.util.Comparator;

public class LogsComparator implements Comparator<String> {
    @Override
    public int compare(String key1, String key2) {
        // Order for latest.log and debug.log
        if ("latest.log".equals(key1)) return -1;
        if ("latest.log".equals(key2)) return 1;
        if ("debug.log".equals(key1)) return -1;
        if ("debug.log".equals(key2)) return 1;

        // Order for keys starting with crash or hs_err_pid
        if (key1.startsWith("crash") || key1.startsWith("hs_err_pid")) return -1;
        if (key2.startsWith("crash") || key2.startsWith("hs_err_pid")) return 1;

        // Order for keys starting with ending with launcher_log.txt or atlauncher.log
        if (key1.endsWith("launcher_log.txt") || key1.endsWith("atlauncher.log")) return -1;
        if (key2.endsWith("launcher_log.txt") || key2.endsWith("atlauncher.log")) return 1;

        // Order for keys starting with kubejs/
        if (key1.startsWith("kubejs/") && !key2.startsWith("kubejs/")) return -1;
        if (!key1.startsWith("kubejs/") && key2.startsWith("kubejs/")) return 1;

        // "CrashAssistant: latest.log" always last
        if ("CrashAssistant: latest.log".equals(key1)) return 1;
        if ("CrashAssistant: latest.log".equals(key2)) return -1;

        // All other keys are sorted alphabetically
        return key1.compareTo(key2);
    }
}
