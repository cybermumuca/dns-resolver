package dns;

import dns.enums.QueryType;
import utils.PacketBuffer;

public class DNSQuestion {
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
