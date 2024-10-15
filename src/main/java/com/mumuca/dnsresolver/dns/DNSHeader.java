package com.mumuca.dnsresolver.dns;

import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.dns.exceptions.InvalidHeaderSizeException;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.dns.utils.exceptions.EndOfBufferException;

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

    private int id;
    private boolean query;
    private short opcode;
    private boolean authoritativeAnswer;
    private boolean truncatedMessage;
    private boolean recursionDesired;
    private boolean recursionAvailable;
    private short z;
    private ResultCode resultCode;
    private int questionCount;
    private int answerRecordCount;
    private int authoritativeRecordCount;
    private int additionalRecordCount;

    public DNSHeader() {
        this.resultCode = ResultCode.NO_ERROR;
    }

    public DNSHeader(int id, boolean query, short opcode, boolean recursionDesired, boolean recursionAvailable, ResultCode resultCode) {
        this.id = id;
        this.query = query;
        this.opcode = opcode;
        this.authoritativeAnswer = false;
        this.truncatedMessage = false;
        this.recursionDesired = recursionDesired;
        this.recursionAvailable = recursionAvailable;
        this.z = 0;
        this.resultCode = resultCode;
        this.questionCount = 0;
        this.answerRecordCount = 0;
        this.authoritativeRecordCount = 0;
        this.additionalRecordCount = 0;
    }

    public DNSHeader(int id, short opcode, boolean recursionDesired, int questionCount) {
        this(id, true, opcode, recursionDesired, false, ResultCode.NO_ERROR);
        this.questionCount = questionCount;
    }

    public static DNSHeader fromBuffer(PacketBuffer buffer) throws InvalidHeaderSizeException {
        var dnsHeader = new DNSHeader();

        try {
            dnsHeader.id = buffer.read16b();
            var flags = buffer.read16b();

            dnsHeader.query = ((flags >> 15) & 0b1) == 0;
            dnsHeader.opcode = (short) ((flags >> 11) & 0b1111);
            dnsHeader.authoritativeAnswer = ((flags >> 10) & 0b1) > 0;
            dnsHeader.truncatedMessage = ((flags >> 9) & 0b1) > 0;
            dnsHeader.recursionDesired = ((flags >> 8) & 0b1) > 0;
            dnsHeader.recursionAvailable = ((flags >> 7) & 0b1) > 0;
            dnsHeader.z = (short) ((flags >> 4) & 0b111);
            dnsHeader.resultCode = ResultCode.fromCode(flags & 0b1111);

            dnsHeader.questionCount = buffer.read16b();
            dnsHeader.answerRecordCount = buffer.read16b();
            dnsHeader.authoritativeRecordCount = buffer.read16b();
            dnsHeader.additionalRecordCount = buffer.read16b();

            return dnsHeader;
        } catch (EndOfBufferException e) {
            throw new InvalidHeaderSizeException();
        }
    }

    public void writeToBuffer(PacketBuffer buffer) throws InvalidHeaderSizeException {
        try {
            buffer.write16b(this.id);

            int flags = (this.query ? 0 : 1) << 15;
            flags |= (this.opcode & 0xF) << 11;
            flags |= (this.authoritativeAnswer ? 1 : 0) << 10;
            flags |= (this.truncatedMessage ? 1 : 0) << 9;
            flags |= (this.recursionDesired ? 1 : 0) << 8;
            flags |= (this.recursionAvailable ? 1 : 0) << 7;
            flags |= (this.z & 0x7) << 4;
            flags |= this.resultCode.getCode() & 0xF;

            buffer.write16b(flags);

            buffer.write16b(questionCount);
            buffer.write16b(answerRecordCount);
            buffer.write16b(authoritativeRecordCount);
            buffer.write16b(additionalRecordCount);
        } catch (EndOfBufferException e) {
            throw new InvalidHeaderSizeException();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isQuery() {
        return query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public boolean isAuthoritativeAnswer() {
        return authoritativeAnswer;
    }

    public void setAuthoritativeAnswer(boolean authoritativeAnswer) {
        this.authoritativeAnswer = authoritativeAnswer;
    }

    public boolean isTruncatedMessage() {
        return truncatedMessage;
    }

    public void setTruncatedMessage(boolean truncatedMessage) {
        this.truncatedMessage = truncatedMessage;
    }

    public boolean isRecursionDesired() {
        return recursionDesired;
    }

    public void setRecursionDesired(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
    }

    public boolean isRecursionAvailable() {
        return recursionAvailable;
    }

    public void setRecursionAvailable(boolean recursionAvailable) {
        this.recursionAvailable = recursionAvailable;
    }

    public short getZ() {
        return z;
    }

    public void setZ(short z) {
        this.z = z;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getAnswerRecordCount() {
        return answerRecordCount;
    }

    public void setAnswerRecordCount(int answerRecordCount) {
        this.answerRecordCount = answerRecordCount;
    }

    public int getAuthoritativeRecordCount() {
        return authoritativeRecordCount;
    }

    public void setAuthoritativeRecordCount(int authoritativeRecordCount) {
        this.authoritativeRecordCount = authoritativeRecordCount;
    }

    public int getAdditionalRecordCount() {
        return additionalRecordCount;
    }

    public void setAdditionalRecordCount(int additionalRecordCount) {
        this.additionalRecordCount = additionalRecordCount;
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
