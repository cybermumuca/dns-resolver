package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

/**
 * Representa um registro de recurso DNS do tipo CNAME (Canonical Name), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.1">RFC 1035</a>.
 *
 * <p>O registro CNAME é utilizado para mapear um nome de domínio para outro nome de domínio canônico (o alias para o nome real).
 * Isso permite que múltiplos nomes de domínio sejam associados ao mesmo endereço IP ou recurso sem precisar
 * duplicar registros de outros tipos, como A (endereço IPv4) ou AAAA (endereço IPv6).</p>
 *
 * <p><strong>Atributos:</strong>
 * <ul>
 *   <li><b>name</b>: O nome de domínio ao qual o registro CNAME se aplica (alias).</li>
 *   <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *   <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *   <li><b>length</b>: O comprimento do campo CNAME em bytes.</li>
 *   <li><b>cname</b>: O nome de domínio canônico ao qual o nome de domínio é mapeado.</li>
 * </ul>
 */
public record CNAME(String name, short qClass, int ttl, int length, String cname) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.CNAME;
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

            buffer.writeQName(cname());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
