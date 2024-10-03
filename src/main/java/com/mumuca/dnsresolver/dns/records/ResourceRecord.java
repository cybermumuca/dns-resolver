package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;

/**
 * Representa um registro de recurso DNS genérico, conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.2.1">RFC 1035</a>.
 *
 * <p>Os tipos de registros que implementam esta interface incluem:</p>
 * <ul>
 *     <li>{@link A} - Registro de endereço IPv4.</li>
 *     <li>{@link NS} - Registro de servidor de nomes.</li>
 *     <li>{@link CNAME} - Registro de nome canônico.</li>
 *     <li>{@link SOA} - Registro de início de autoridade.</li>
 *     <li>{@link MX} - Registro de troca de e-mail.</li>
 *     <li>{@link AAAA} - Registro de endereço IPv6.</li>
 * </ul>
 */
public sealed interface ResourceRecord permits A, NS, CNAME, SOA, MX, AAAA {
    String name();
    QueryType type();
    short qClass();
    int ttl();
    int length();

    default void writeToBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException {
        throw new QueryTypeUnsupportedException();
    }
}
