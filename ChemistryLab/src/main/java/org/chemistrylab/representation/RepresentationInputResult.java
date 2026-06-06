package org.chemistrylab.representation;

public class RepresentationInputResult {

    private final String value;
    private final RepresentationInputSource source;
    private final String reason;

    public RepresentationInputResult(String value, RepresentationInputSource source, String reason) {
        this.value = value;
        this.source = source;
        this.reason = reason;
    }

    public static RepresentationInputResult of(String value, RepresentationInputSource source, String reason) {
        return new RepresentationInputResult(value, source, reason);
    }

    public String getValue() {
        return value;
    }

    public RepresentationInputSource getSource() {
        return source;
    }

    public String getReason() {
        return reason;
    }

    public boolean hasValue() {
        return value != null && !value.isBlank();
    }

    @Override
    public String toString() {
        return "RepresentationInputResult{" +
                "value='" + value + '\'' +
                ", source=" + source +
                ", reason='" + reason + '\'' +
                '}';
    }
}
