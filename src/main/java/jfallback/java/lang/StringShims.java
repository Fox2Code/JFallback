package jfallback.java.lang;

import java.util.Formatter;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class StringShims {
    private static final Pattern NEW_LINE = Pattern.compile("\\r?\\n");

    // Added in java 11
    public static String strip(String self) {
        final int len = self.length();
        int start;
        for (start = 0; start < len; start++) {
            if (!Character.isWhitespace(self.charAt(start)))
                break;
        }
        if (start == len)
            return "";
        int end;
        for (end = len - 1; end >= 0; end--) {
            if (!Character.isWhitespace(self.charAt(end)))
                break;
        }
        return self.substring(start, end + 1);
    }

    // Added in java 11
    public static String stripLeading(String self) {
        final int len = self.length();
        int start;
        for (start = 0; start < len; start++) {
            if (!Character.isWhitespace(self.charAt(start)))
                break;
        }
        if (start == len)
            return "";
        return self.substring(start);
    }

    // Added in java 11
    public static String stripTrailing(String self) {
        final int len = self.length();
        int end;
        for (end = len - 1; end >= 0; end--) {
            if (!Character.isWhitespace(self.charAt(end)))
                break;
        }
        end++;
        return end == 0 ? "" : self.substring(0, end);
    }

    // Added in java 11
    public static boolean isBlank(String self) {
        return self.isEmpty() || self.chars().allMatch(Character::isWhitespace);
    }

    // Added in java 11
    public static Stream<String> lines(String self) {
        return NEW_LINE.splitAsStream(self);
    }

    // Added in java 11
    public static String repeat(String self, int count) {
        if (count < 1) throw new IllegalStateException();
        if (count == 1) return self;
        StringBuilder stringBuilder = new StringBuilder(self.length() * count);
        while (count-->0) {
            stringBuilder.append(self);
        }
        return stringBuilder.toString();
    }

    // Added in java 15
    public static String formatted(String string, Object... args) {
        return new Formatter().format(string, args).toString();
    }
}
