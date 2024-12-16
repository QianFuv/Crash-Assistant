package dev.kostromdan.mods.crash_assistant.mod_list;

import java.util.TreeSet;

public record ModListDiff(TreeSet<String> addedMods, TreeSet<String> removedMods) {
}

