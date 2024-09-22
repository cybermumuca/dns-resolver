package dns;

import dns.enums.ResultCode;
import utils.PacketBuffer;

public class DNSHeader {
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
