package jfallback.java.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

// Added in java 9
public final class Runtime$Version
        implements Comparable<Runtime$Version> {
    private static final Runtime$Version JAVA_8 =
            new Runtime$Version(Collections.singletonList(8));
    private final List<Integer> version;

    private Runtime$Version(List<Integer> version) {
        this.version = version;
    }

    // Added in java 9
    public static Runtime$Version parse(String s) {
        if ("8".equals(s)) return JAVA_8;
        // Step 1, get the closest char between "+", "-", and "_", and trim.
        int i1 = s.indexOf('-');
        int i2 = s.indexOf('+');
        if (i1 == -1) {
            i1 = i2;
        } else if (i2 != -1) {
            i1 = Math.min(i1, i2);
        }
        i2 = s.indexOf('_');
        if (i1 == -1) {
            i1 = i2;
        } else if (i2 != -1) {
            i1 = Math.min(i1, i2);
        }
        if (i1 != -1) {
            s = s.substring(0, i1);
        }
        // Step 2 get major
        ArrayList<Integer> version = new ArrayList<>();
        i1 = s.indexOf('.');
        version.add(Integer.parseInt(i1 == -1 ? s : s.substring(0, i1)));
        while (i1 != -1) {
            i2 = i1 + 1;
            i1 = s.indexOf('.', i2);
            version.add(Integer.parseInt(i1 == -1 ? s.substring(i2) : s.substring(i2, i1)));
        }
        return new Runtime$Version(Collections.unmodifiableList(version));
    }

    private int getVersionSpot(int i) {
        return (version.size() > i ? version.get(i) : 0);
    }

    // Added in java 9
    public int major() {
        return this.getVersionSpot(0);
    }

    // Added in java 9
    public int minor() {
        return this.getVersionSpot(1);
    }

    // Added in java 9
    public int security() {
        return this.getVersionSpot(2);
    }

    // Added in java 9
    public List<Integer> version() {
        return version;
    }

    // Added in java 10
    public int feature() {
        return this.getVersionSpot(0);
    }

    // Added in java 10
    public int interim() {
        return this.getVersionSpot(1);
    }

    // Added in java 10
    public int update() {
        return this.getVersionSpot(2);
    }

    // Added in java 10
    public int patch() {
        return this.getVersionSpot(3);
    }

    // Added in java 9
    @Override
    public int compareTo(Runtime$Version obj) {
        List<Integer> objVersion = obj.version();
        int mySize = this.version.size();
        int objSize = objVersion.size();
        int min = Math.min(mySize, objSize);
        for (int i = 0; i < min; i++) {
            int myVal = this.version.get(i);
            int objVal = objVersion.get(i);
            if (myVal != objVal) {
                return Integer.compare(myVal, objVal);
            }
        }
        return Integer.compare(mySize, objSize);
    }

    // Added in java 9
    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(".");
        for (Integer i : this.version) {
            stringJoiner.add(i.toString());
        }
        return stringJoiner.toString();
    }
}
