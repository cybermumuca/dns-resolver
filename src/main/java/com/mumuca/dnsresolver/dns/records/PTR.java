package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

/**
 * Representa um registro DNS do tipo PTR (Pointer Record), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.12">RFC 1035</a>.
 *
 * <p>O registro PTR é utilizado para consultas reversas de DNS.
 * Um registro PTR mapeia um endereço IP a um nome de domínio, possibilitando a resolução reversa.</p>
 *
 * <p><strong>Atributos:</strong></p>
 * <ul>
 *   <li><b>name</b>: O nome de domínio para o qual o registro PTR é aplicável.</li>
 *   <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *   <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *   <li><b>length</b>: O comprimento dos dados do registro PTR em bytes, calculado dinamicamente com base no nome de domínio apontado.</li>
 *   <li><b>domain</b>: O nome de domínio para o qual o endereço IP está mapeado, conforme a consulta reversa DNS.</li>
 * </ul>
 */
public record PTR(String name, short qClass, int ttl, int length, String domain) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.PTR;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) throws QueryTypeUnsupportedException {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());

            int position = buffer.getPosition();
            buffer.write16b(0);

            buffer.writeQName(domain());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
