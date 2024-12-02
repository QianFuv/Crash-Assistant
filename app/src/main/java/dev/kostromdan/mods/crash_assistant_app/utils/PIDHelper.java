package dev.kostromdan.mods.crash_assistant_app.utils;

import java.util.Optional;

public interface PIDHelper {
    static ProcessHandle findProcessByPID(long pid) {
        Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);
        return processHandle.orElse(null);
    }

    static boolean isProcessAlive(long pid) {
        return findProcessByPID(pid) != null;
    }
}
