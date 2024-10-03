package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

/**
 * Representa um registro de recurso DNS do tipo NS (Name Server), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.11">RFC 1035</a>.
 *
 * <p>O registro NS é utilizado para especificar um servidor de nomes que é responsável por fornecer informações
 * sobre um domínio, incluindo outros registros de recurso associados a ele. Os registros NS indicam quais
 * servidores de nomes são autorizados para um domínio específico.</p>
 *
 * <p><strong>Atributos:</strong>
 * <ul>
 *   <li><b>name</b>: O nome de domínio para o qual o registro NS se aplica.</li>
 *   <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *   <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *   <li><b>length</b>: O comprimento do campo NS em bytes, que é calculado com base no tamanho do nome do servidor de nomes.</li>
 *   <li><b>ns</b>: O nome de domínio do servidor de nomes responsável por resolver consultas para o domínio.</li>
 * </ul>
 */
public record NS(String name, short qClass, int ttl, int length, String ns) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.NS;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(name());
            buffer.write16b(type().getValue());
            buffer.write16b(qClass());
            buffer.write32b(ttl());

            int position = buffer.getPosition();
            buffer.write16b(0);

            buffer.writeQName(ns());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
