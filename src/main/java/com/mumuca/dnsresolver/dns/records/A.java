package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Representa um registro de recurso DNS do tipo A (Address Record), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.4.1">RFC 1035</a>.
 *
 * <p> O registro A é utilizado para mapear um nome de domínio a um endereço IPv4. Cada registro A contém um
 * endereço IP de 32 bits, o destino para o qual o nome de domínio é resolvido.</p>
 *
 * <p><strong>Atributos:</strong>
 * <ul>
 *   <li><b>name</b>: O nome de domínio ao qual o registro A se aplica.</li>
 *   <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *   <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *   <li><b>length</b>: O comprimento do campo de endereço (4 bytes para IPv4).</li>
 *   <li><b>address</b>: O endereço IPv4 associado ao nome de domínio.</li>
 * </ul>
 */
public record A(String name, short qClass, int ttl, int length, String address) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.A;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(1);
            buffer.write32b(ttl());
            buffer.write16b(4);

            var rawAddress = InetAddress.getByName(address).getAddress();

            buffer.write(rawAddress[0]);
            buffer.write(rawAddress[1]);
            buffer.write(rawAddress[2]);
            buffer.write(rawAddress[3]);
        } catch (EndOfBufferException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
