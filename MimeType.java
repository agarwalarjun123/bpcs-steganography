package BPCS;

import java.util.HashMap;
import java.util.Map;

public enum MimeType {

    TEXT(1, Extraction.TEXT),
    IMAGE(2, Extraction.IMAGE);

    private final int value;
    private final String[] extension;

    MimeType(int value, String[] extension) {
        this.value = value;
        this.extension = extension;
    }

    public static final Map<Integer, String[]> typeCodes = new HashMap<Integer, String[]>();

    static {
        for (MimeType m : MimeType.values()) {
            typeCodes.put(m.value, m.extension);
        }
    }

    public int getValue() {
        return value;
    }
}