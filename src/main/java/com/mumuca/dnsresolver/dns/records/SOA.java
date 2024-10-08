package com.mumuca.dnsresolver.dns.records;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QueryTypeUnsupportedException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

/**
 * Representa um registro de recurso DNS do tipo SOA (Start of Authority), conforme definido na
 * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.3.13">RFC 1035</a>.
 *
 * <p>O registro SOA fornece informações administrativas sobre uma zona de DNS, incluindo o servidor
 * de nomes responsável pela zona, o endereço de e-mail do administrador, e parâmetros de controle
 * como o número de série, o tempo de atualização e o tempo de expiração.</p>
 *
 * <p><strong>Atributos:</strong>
 * <ul>
 *     <li><b>name</b>: O nome de domínio da zona a qual o registro SOA se aplica.</li>
 *     <li><b>qClass</b>: A classe do registro, normalmente IN (Internet), identificada por um valor numérico de 16 bits.</li>
 *     <li><b>ttl</b>: O tempo de vida (Time to Live) em segundos, indicando por quanto tempo a resposta pode ser armazenada em cache.</li>
 *     <li><b>length</b>: O comprimento do registro SOA em bytes, calculado com base no tamanho dos campos mname e rname.</li>
 *     <li><b>mname</b>: O nome do servidor de nomes autoritativo para a zona.</li>
 *     <li><b>rname</b>: O endereço de e-mail do administrador da zona (com o caractere "@" substituído por um ponto).</li>
 *     <li><b>serial</b>: O número de série do registro, que é incrementado cada vez que a zona é atualizada.</li>
 *     <li><b>refresh</b>: O intervalo de tempo (em segundos) que os servidores secundários devem esperar antes de consultar o servidor primário para verificar se há atualizações.</li>
 *     <li><b>retry</b>: O tempo (em segundos) que os servidores secundários devem esperar antes de tentar novamente uma consulta ao servidor primário após uma falha.</li>
 *     <li><b>expire</b>: O tempo (em segundos) que um servidor secundário deve manter os dados antes de descartá-los se não conseguir contatar o servidor primário.</li>
 *     <li><b>minimum</b>: O tempo (em segundos) que um registro pode ser armazenado em cache, utilizado como TTL para registros que não têm um TTL definido.</li>
 * </ul>
 */
public record SOA(String name, short qClass, int ttl, int length, String mname, String rname, long serial, int refresh, int retry, long expire, int minimum) implements ResourceRecord {
    @Override
    public QueryType type() {
        return QueryType.SOA;
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

            buffer.writeQName(mname());
            buffer.writeQName(rname());

            buffer.write32b((int) (serial() & 0xFFFFFFFFL)); // truncated value
            buffer.write32b(refresh());
            buffer.write32b(retry());
            buffer.write32b((int) (expire() & 0xFFFFFFFFL)); // truncated value
            buffer.write32b(minimum());

            int size = buffer.getPosition() - (position + 2);
            buffer.set16b(position, size);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
