package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

/**
 * Representa um registro de recurso DNS do tipo MX (Mail Exchange), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.9">RFC 1035</a>.
 *
 * <p>O registro MX é utilizado para especificar um servidor de correio eletrônico responsável por
 * receber e gerenciar e-mails para um domínio específico. Cada registro MX contém um valor de
 * prioridade e um nome de domínio do servidor de e-mail.</p>
 *
 * <p><strong>Atributos:</strong>
 * <ul>
 *   <li><b>name</b>: O nome de domínio para o qual o registro MX se aplica.</li>
 *   <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *   <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *   <li><b>length</b>: O comprimento do campo MX em bytes, que é calculado com base no tamanho do nome do servidor de e-mail.</li>
 *   <li><b>priority</b>: A prioridade do servidor de correio, onde valores mais baixos indicam maior prioridade.</li>
 *   <li><b>mx</b>: O nome de domínio do servidor de correio responsável por receber e-mails para o domínio.</li>
 * </ul>
 */
public record MX(String name, short qClass, int ttl, int length, int priority, String mx) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.MX;
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

            buffer.write16b(priority());
            buffer.writeQName(mx());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
