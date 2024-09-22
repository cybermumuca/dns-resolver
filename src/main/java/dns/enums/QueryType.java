package dns.enums;

public enum QueryType {
    UNKNOWN(0),
    A(1);

    private final int value;

    QueryType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static QueryType fromValue(int value) {
        for (QueryType queryType : QueryType.values()) {
            if (queryType.getValue() == value) {
                return queryType;
            }
        }
        throw new IllegalArgumentException("Invalid result value: " + value);
    }
}
