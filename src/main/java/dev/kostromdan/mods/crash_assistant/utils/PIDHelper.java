package dev.kostromdan.mods.crash_assistant.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Optional;

public interface PIDHelper {
    static long getCurrentProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Long.parseLong(runtimeMXBean.getName().split("@")[0]);
    }
}
