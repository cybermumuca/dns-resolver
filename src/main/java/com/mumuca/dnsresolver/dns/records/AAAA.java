package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

import java.net.Inet6Address;
import java.net.UnknownHostException;

/**
 * Representa um registro de recurso DNS do tipo AAAA (IPv6 Address Record), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc3596#section-2.1">RFC 3596</a>.
 *
 * <p>O registro AAAA é utilizado para mapear um nome de domínio a um endereço IPv6. Cada registro AAAA contém um
 * endereço IP de 128 bits, o destino para o qual o nome de domínio é resolvido.</p>
 *
 * <p><strong>Atributos:</strong>
 * <ul>
 *   <li><b>name</b>: O nome de domínio ao qual o registro AAAA se aplica.</li>
 *   <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *   <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *   <li><b>length</b>: O comprimento do campo de endereço (16 bytes para IPv6).</li>
 *   <li><b>address</b>: O endereço IPv6 associado ao nome de domínio.</li>
 * </ul>
 */
public record AAAA(String name, short qClass, int ttl, int length, String address) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.AAAA;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());
            buffer.write16b(16);

            byte[] rawAddress = Inet6Address.getByName(address).getAddress();

            for (byte b : rawAddress) {
                buffer.write(b);
            }
        } catch (UnknownHostException | EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
