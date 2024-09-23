package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.utils.PacketBuffer;

public class DNSHeader {
    /**
     * Representa o cabeçalho de uma mensagem DNS.
     *
     * <p>Esta classe encapsula os detalhes do cabeçalho de uma mensagem DNS, conforme
     * definido na <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.1">RFC 1035</a>.
     *
     * <p><strong>Atributos:</strong>
     * <ul>
     *   <li><strong>id</strong> - Um identificador único para a consulta, utilizado
     *   para associar perguntas e respostas.</li>
     *   <li><strong>query</strong> - Um flag que indica se a mensagem é uma consulta
     *   (true) ou uma resposta (false).</li>
     *   <li><strong>opcode</strong> - O código da operação que especifica o tipo
     *   de consulta (por exemplo, padrão, inversa, etc.).</li>
     *   <li><strong>authoritativeAnswer</strong> - Um flag que indica se a resposta
     *   é autoritativa.</li>
     *   <li><strong>truncatedMessage</strong> - Um flag que indica se a mensagem
     *   foi truncada devido a um tamanho excessivo.</li>
     *   <li><strong>recursionDesired</strong> - Um flag que indica se o cliente
     *   deseja que o servidor realize uma consulta recursiva.</li>
     *   <li><strong>recursionAvailable</strong> - Um flag que indica se o servidor
     *   suporta consultas recursivas.</li>
     *   <li><strong>z</strong> - Reservado para uso futuro; deve ser definido como 0.</li>
     *   <li><strong>resultCode</strong> - O código de resultado que indica o status
     *   da resposta (por exemplo, nenhum erro, falha, etc.).</li>
     *   <li><strong>questionCount</strong> - A contagem de perguntas na mensagem.</li>
     *   <li><strong>answerRecordCount</strong> - A contagem de registros de resposta
     *   na mensagem.</li>
     *   <li><strong>authoritativeRecordCount</strong> - A contagem de registros
     *   autoritativos na mensagem.</li>
     *   <li><strong>additionalRecordCount</strong> - A contagem de registros
     *   adicionais na mensagem.</li>
     * </ul>
     *
     * @see ResultCode
     */

    public int id;
    public boolean query;
    public short opcode;
    public boolean authoritativeAnswer;
    public boolean truncatedMessage;
    public boolean recursionDesired;
    public boolean recursionAvailable;
    public short z;
    public ResultCode resultCode;
    public int questionCount;
    public int answerRecordCount;
    public int authoritativeRecordCount;
    public int additionalRecordCount;

    public DNSHeader() {
        this.resultCode = ResultCode.NO_ERROR;
    }

    public DNSHeader(int id,
                     boolean query,
                     short opcode,
                     boolean authoritativeAnswer,
                     boolean truncatedMessage,
                     boolean recursionDesired,
                     boolean recursionAvailable,
                     short z,
                     ResultCode resultCode,
                     int questionCount,
                     int answerRecordCount,
                     int authoritativeRecordCount,
                     int additionalRecordCount) {
        this.id = id;
        this.query = query;
        this.opcode = opcode;
        this.authoritativeAnswer = authoritativeAnswer;
        this.truncatedMessage = truncatedMessage;
        this.recursionDesired = recursionDesired;
        this.recursionAvailable = recursionAvailable;
        this.z = z;
        this.resultCode = resultCode;
        this.questionCount = questionCount;
        this.answerRecordCount = answerRecordCount;
        this.authoritativeRecordCount = authoritativeRecordCount;
        this.additionalRecordCount = additionalRecordCount;
    }

    public void readBuffer(PacketBuffer buffer) throws Exception {
        this.id = buffer.read16b();
        var flags = buffer.read16b();

        this.query = ((flags >> 15) & 0b1) == 0;
        this.opcode = (short) ((flags >> 11) & 0b1111);
        this.authoritativeAnswer = ((flags >> 10) & 0b1) > 0;
        this.truncatedMessage = ((flags >> 9) & 0b1) > 0;
        this.recursionDesired = ((flags >> 8) & 0b1) > 0;
        this.recursionAvailable = ((flags >> 7) & 0b1) > 0;
        this.z = (short) ((flags >> 4) & 0b111);
        this.resultCode = ResultCode.fromCode(flags & 0b1111);

        this.questionCount = buffer.read16b();
        this.answerRecordCount = buffer.read16b();
        this.authoritativeRecordCount = buffer.read16b();
        this.additionalRecordCount = buffer.read16b();
    }

    public void writeInBuffer(PacketBuffer buffer) throws Exception {
        buffer.write16b(this.id);

        int flags = (this.query ? 0 : 1) << 15;
        flags |= (this.opcode & 0xF) << 11;
        flags |= (this.authoritativeAnswer ? 1 : 0) << 10;
        flags |= (this.truncatedMessage ? 1 : 0) << 9;
        flags |= (this.recursionDesired ? 1 : 0) << 8;
        flags |= (this.recursionAvailable ? 1 : 0) << 7;
        flags |= (this.z & 0x7) << 4;
        flags |= this.resultCode.getCode() & 0xF ;

        buffer.write16b(flags);

        buffer.write16b(questionCount);
        buffer.write16b(answerRecordCount);
        buffer.write16b(authoritativeRecordCount);
        buffer.write16b(additionalRecordCount);
    }

    @Override
    public String toString() {
        return "DNSHeader{" +
                "id=" + id +
                ", query=" + query +
                ", opcode=" + opcode +
                ", authoritativeAnswer=" + authoritativeAnswer +
                ", truncatedMessage=" + truncatedMessage +
                ", recursionDesired=" + recursionDesired +
                ", recursionAvailable=" + recursionAvailable +
                ", z=" + z +
                ", resultCode=" + resultCode +
                ", questionCount=" + questionCount +
                ", answerRecordCount=" + answerRecordCount +
                ", authoritativeRecordCount=" + authoritativeRecordCount +
                ", additionalRecordCount=" + additionalRecordCount +
                '}';
    }
}
