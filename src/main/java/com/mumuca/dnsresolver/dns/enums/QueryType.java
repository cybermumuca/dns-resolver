package com.mumuca.dnsresolver.dns.enums;

import com.mumuca.dnsresolver.servers.exceptions.NotImplementedException;

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
     *   <li><strong>NS</strong> (2) - Representa um registro de servidor de nomes, que aponta para o servidor de nomes autoritativo de um domínio.</li>
     *   <li><strong>CNAME</strong> (5) - Representa um registro de nome canônico, que mapeia um nome de domínio para outro (alias).</li>
     *   <li><strong>MX</strong> (15) - Representa um registro de troca de mensagens (Mail Exchange), que aponta para o servidor responsável pelo recebimento de e-mails em nome de um domínio.</li>
     *   <li><strong>AAAA</strong> (28) - Representa um registro de endereço IPv6.</li>
     * </ul>
     */

    UNKNOWN(0),
    A(1),
    NS(2),
    CNAME(5),
    MX(15),
    AAAA(28);


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

        throw new NotImplementedException("Invalid result value: " + value);
    }
}
