package dev.kostromdan.mods.crash_assistant.mod_list;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public record ModListDiff(TreeSet<String> addedMods,
                          TreeSet<String> removedMods) {}

