package org.myongoingscalendar.model;

/**
 * @author firs
 */
public enum MIMEType {
    JPG("jpg"),
    WEBP("webp"),
    PNG("png"),
    SVG("svg");

    private final String text;

    private MIMEType(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static String getFormat(MIMEType mimeType) {
        return "." + mimeType.toString();
    }
}
