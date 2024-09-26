package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.exceptions.QuestionMalformedException;
import com.mumuca.dnsresolver.dns.exceptions.SuspiciousDomainNamePayloadException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.BufferPositionOutOfBoundsException;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;
import com.mumuca.dnsresolver.dns.utils.exceptions.JumpLimitExceededException;

public class DNSQuestion {
    /**
     * Representa a seção de Pergunta de uma mensagem DNS.
     *
     * <p>Esta classe encapsula os detalhes de uma pergunta DNS, conforme definido na
     * <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.2">RFC 1035</a>.
     *
     * <p><strong>Atributos:</strong>
     * <ul>
     *   <li><strong>qName</strong> - O nome do domínio a ser consultado, representado
     *   como uma string.</li>
     *   <li><strong>qType</strong> - O tipo de consulta DNS, especificando o tipo
     *   de registro desejado (por exemplo, A, AAAA, CNAME, etc.).</li>
     *   <li><strong>qClass</strong> - A classe do registro, normalmente definida como
     *   1 para registros da Internet.</li>
     * </ul>
     *
     * @see QueryType
     */

    public String qName;
    public QueryType qType;
    public short qClass;

    public DNSQuestion() {}

    public DNSQuestion(String qName, QueryType qType, short qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }

    public void readBuffer(PacketBuffer buffer) throws SuspiciousDomainNamePayloadException, QuestionMalformedException {
        try {
            this.qName = buffer.readQName();
            this.qType = QueryType.fromValue(buffer.read16b());
            this.qClass = (short) buffer.read16b();
        } catch (JumpLimitExceededException e) {
            throw new SuspiciousDomainNamePayloadException();
        } catch (BufferPositionOutOfBoundsException | EndOfBufferException e) {
            throw new QuestionMalformedException();
        }
    }

    public void writeToBuffer(PacketBuffer buffer) {
        try {
            buffer.writeQName(this.qName);
            buffer.write16b(this.qType.getValue());
            buffer.write16b(1);
        } catch (EndOfBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "DNSQuestion{" +
                "qName='" + qName + '\'' +
                ", qType=" + qType +
                ", qClass=" + qClass +
                '}';
    }
}
