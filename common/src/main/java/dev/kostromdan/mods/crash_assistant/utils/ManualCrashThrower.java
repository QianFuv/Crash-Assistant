package dev.kostromdan.mods.crash_assistant.utils;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.util.NativeModuleLister;

public interface ManualCrashThrower {
    static void crashGame() {
        CrashReport crashreport = new CrashReport("Manually triggered debug crash", new Throwable("Manually triggered debug crash"));
        CrashReportCategory crashreportcategory = crashreport.addCategory("Manual crash details");
        NativeModuleLister.addCrashSection(crashreportcategory);
        throw new ReportedException(crashreport);
    }
}
