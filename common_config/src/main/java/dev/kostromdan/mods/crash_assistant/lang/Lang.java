package dev.kostromdan.mods.crash_assistant.lang;

import java.util.HashMap;

public class Lang {
    public HashMap<String, String> lang;

    public String get(String key) {
        return lang.getOrDefault(key, LanguageProvider.languages.get("en_us").lang.get(key));
    }

    public Lang (HashMap<String, String> lang) {
        this.lang = lang;
    }
}
