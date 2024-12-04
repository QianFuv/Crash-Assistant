package dev.kostromdan.mods.crash_assistant.loading_utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public interface PIDHelper {
    static String getCurrentProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }
}
