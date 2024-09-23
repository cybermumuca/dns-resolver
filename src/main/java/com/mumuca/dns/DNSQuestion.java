package com.mumuca.dns;

import com.mumuca.dns.enums.QueryType;
import com.mumuca.utils.PacketBuffer;

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

    public void readBuffer(PacketBuffer buffer) throws Exception {
        this.qName = buffer.readQName();
        this.qType = QueryType.fromValue(buffer.read16b());
        this.qClass = (short) buffer.read16b();
    }

    public void writeInBuffer(PacketBuffer buffer) throws Exception {
        buffer.writeQName(this.qName);
        buffer.write16b(this.qType.getValue());
        buffer.write16b(1);
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
