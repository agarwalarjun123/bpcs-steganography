package BPCS;

// import java.util.HashMap;
// import java.util.Map;

public enum MimeType {

    PNG(3, "png"),
    JPEG(4,"jpeg"),
    TXT(5,"txt"),
    DOCX(6,"docx");

    private final int value;
    private final String extension;

    MimeType(int value, String extension) {
        this.value = value;
        this.extension = extension;
    }
    public int getValue() {
        return value;
    }
    public String getExtension(){
        return extension;
    }
    public static MimeType getMimeTypeFromValue(int value) throws Exception {
        for(MimeType mimeType : MimeType.values()) {
            if(mimeType.value == value) {
                return mimeType;
            }
        }
        throw new Exception("No Mapping for Value Found.");
    }
}