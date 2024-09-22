package dns.enums;

public enum QueryType {
    /**
     * Representa a enumeração de alguns tipos de Resource Records (RR).
     *
     * <p>Esta enumeração define os tipos de consulta utilizados nas mensagens DNS,
     * conforme descrito na <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.2.2">RFC 1035</a>.
     *
     * <p><strong>Tipos de Consulta:</strong>
     * <ul>
     *   <li><strong>UNKNOWN</strong> (0) - Indica um tipo de consulta desconhecido.</li>
     *   <li><strong>A</strong> (1) - Representa um registro de endereço IPv4.</li>
     * </ul>
     */

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
