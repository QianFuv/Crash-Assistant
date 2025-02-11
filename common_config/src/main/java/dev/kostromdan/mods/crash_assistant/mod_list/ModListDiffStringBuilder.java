package dev.kostromdan.mods.crash_assistant.mod_list;

import java.util.ArrayList;
import java.util.List;

public class ModListDiffStringBuilder {
    public List<ColoredString> sb;

    public ModListDiffStringBuilder() {
        sb = new ArrayList<>();
    }

    public void append(String text, String color, boolean endsWithNewLine) {
        sb.add(new ColoredString(text, color, endsWithNewLine));
    }

    public void append(String text, String color) {
        append(text, color, true);
    }

    public void append(String text, boolean endsWithNewLine) {
        append(text, "", false);
    }

    public void append(String text) {
        append(text, "");
    }

    public String toText() {
        StringBuilder result = new StringBuilder();
        for (ColoredString cs : sb) {
            result.append(cs.text());
            if (cs.endsWithNewLine()) {
                result.append("\n");
            }
        }
        return result.toString().trim();
    }

    public String toHtml() {
        StringBuilder result = new StringBuilder();
        result.append("<html><body style='font-family: Arial; font-size: 12px;white-space: nowrap;'>");

        for (ColoredString cs : sb) {
            result.append("<span" + (cs.color.isEmpty() ? "" : " style='color: " + cs.color + ";'") + ">" + cs.text() + "</span>");
            if (cs.endsWithNewLine()) result.append("<br>");
        }
        result.append("</body></html>");
        return result.toString();
    }

    public String toAnsi() {
        StringBuilder result = new StringBuilder();
        result.append(ModListDiff.getFilePrefix());
        result.append(ModListDiff.getFirstString(true, false, null));
        result.append("\n```ansi\n");
        boolean first = true;
        for (ColoredString cs : sb) {
            if (first) {
                first = false;
                continue;
            }
            if (!cs.color.isEmpty()) {
                result.append(Enum.valueOf(AnsiColor.class, cs.color.toUpperCase()).getColorPrefix());
                result.append(cs.text());
                result.append(AnsiColor.postfix);
            } else {
                result.append(cs.text());
            }
            if (cs.endsWithNewLine()) {
                result.append("\n");
            }
        }
        return result.toString().trim() + "\n```";
    }

    public record ColoredString(String text, String color, boolean endsWithNewLine) {
    }
}
