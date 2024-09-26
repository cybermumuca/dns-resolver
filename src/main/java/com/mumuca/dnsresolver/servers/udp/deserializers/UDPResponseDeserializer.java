package com.mumuca.dnsresolver.servers.udp.deserializers;

import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.exceptions.*;
import com.mumuca.dnsresolver.dns.utils.PacketBuffer;
import com.mumuca.dnsresolver.servers.exceptions.FormatErrorException;
import com.mumuca.dnsresolver.servers.exceptions.NotImplementedException;
import com.mumuca.dnsresolver.servers.exceptions.RefusedException;

public class UDPResponseDeserializer {
    public static DNSResponse deserialize(PacketBuffer buffer) {
        try {
            return DNSResponse.fromBuffer(buffer);
        } catch (QuestionMalformedException e) {
            throw new FormatErrorException("Question malformed.");
        } catch (InvalidHeaderSizeException e) {
            throw new FormatErrorException("Invalid header size.");
        } catch (SuspiciousDomainNamePayloadException e) {
            throw new RefusedException("Suspicious Payload.");
        } catch (QueryTypeUnsupportedException e) {
            throw new NotImplementedException("Query type not implemented.");
        } catch (ResourceRecordMalformedException e) {
            throw new FormatErrorException("Some resource record malformed.");
        }
    }
}
