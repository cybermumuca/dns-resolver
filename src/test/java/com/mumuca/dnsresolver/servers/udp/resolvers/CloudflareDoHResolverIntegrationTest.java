package com.mumuca.dnsresolver.servers.udp.resolvers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.dns.records.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// TODO: WIP
@DisplayName("Cloudflare DoH Resolver Integration Test")
class CloudflareDoHResolverIntegrationTest {

    private final CloudflareDoHResolver cloudflareDoHResolver = new CloudflareDoHResolver();

    @DisplayName("Should resolve DoH query for different query types")
    @ParameterizedTest
    @MethodSource("provideQueryTypesAndDomains")
    void shouldResolveDoHQuery(QueryType queryType, String domain, Class<? extends ResourceRecord> expectedRecordClass) {
        // Arrange
        DNSQuery dnsQuery = new DNSQuery(createDNSHeader(), createDNSQuestion(domain, queryType));

        // Act
        long startTime = System.currentTimeMillis();
        DNSResponse doHResponse = cloudflareDoHResolver.resolve(dnsQuery);
        long endTime = System.currentTimeMillis();

        // Assert
        assertNotNull(doHResponse, "DoH response should not be null");
        
        DNSHeader doHResponseHeader = doHResponse.getHeader();
        
        assertEquals(dnsQuery.getHeader().getId(), doHResponseHeader.getId(), "Response ID should match query ID");
        assertFalse(doHResponseHeader.isQuery(), "Response should not be a query");
        assertEquals(dnsQuery.getHeader().getOpcode(), doHResponseHeader.getOpcode(), "Response opcode should match query opcode");
        assertEquals(dnsQuery.getHeader().isRecursionDesired(), doHResponseHeader.isRecursionDesired(), "Response recursion desired should match query recursion desired");
        assertTrue(doHResponseHeader.isRecursionAvailable(), "Response should have recursion available");
        assertEquals(dnsQuery.getHeader().getZ(), doHResponseHeader.getZ(), "Response Z field should match query Z field");
        assertEquals(ResultCode.NO_ERROR, doHResponseHeader.getResultCode(), "Response should have NO_ERROR result code");
        assertEquals(1, doHResponseHeader.getQuestionCount(), "Response should have one question");
        assertTrue(doHResponseHeader.getAnswerRecordCount() >= 1, "Response should have at least one answer record");

        DNSQuestion doHResponseQuestion = doHResponse.getQuestion();
        assertEquals(dnsQuery.getQuestion().qName, doHResponseQuestion.qName, "Query name should match");
        assertEquals(dnsQuery.getQuestion().qType, doHResponseQuestion.qType, "Query type should match");
        assertEquals(dnsQuery.getQuestion().qClass, doHResponseQuestion.qClass, "Query class should match");

        List<ResourceRecord> doHResponseAnswerRecords = doHResponse.getAnswerRecords();
        assertFalse(doHResponseAnswerRecords.isEmpty(), "Response should have answer records");
        assertEquals(doHResponseHeader.getAnswerRecordCount(), doHResponseAnswerRecords.size(), "Answer record count should match header count");

        doHResponseAnswerRecords.forEach(resourceRecord -> {
            assertInstanceOf(expectedRecordClass, resourceRecord, "Resource record should be of expected type");
            assertEquals(resourceRecord.type(), dnsQuery.getQuestion().qType, "Record type should match query type");
        });

        long responseTime = endTime - startTime;
        assertTrue(responseTime < 1000, "DoH resolution should take less than 1 second, but took " + responseTime + " ms");
    }

    static Stream<Arguments> provideQueryTypesAndDomains() {
        return Stream.of(
                Arguments.of(QueryType.A, "www.google.com", A.class),
                Arguments.of(QueryType.NS, "google.com", NS.class),
                Arguments.of(QueryType.CNAME, "www.github.com", CNAME.class),
                Arguments.of(QueryType.MX, "google.com", MX.class),
                Arguments.of(QueryType.AAAA, "www.google.com", AAAA.class),
                Arguments.of(QueryType.SOA, "google.com", SOA.class),
                Arguments.of(QueryType.PTR, "8.8.8.8.in-addr.arpa", PTR.class)
        );
    }

    private DNSHeader createDNSHeader() {
        DNSHeader dnsHeader = new DNSHeader();

        dnsHeader.setId(1);
        dnsHeader.setQuery(true);
        dnsHeader.setOpcode((short) 0);
        dnsHeader.setAuthoritativeAnswer(false);
        dnsHeader.setTruncatedMessage(false);
        dnsHeader.setRecursionDesired(true);
        dnsHeader.setRecursionAvailable(false);
        dnsHeader.setZ((short) 0);
        dnsHeader.setResultCode(ResultCode.NO_ERROR);
        dnsHeader.setQuestionCount((short) 1);
        dnsHeader.setAnswerRecordCount((short) 0);
        dnsHeader.setAuthoritativeRecordCount((short) 0);
        dnsHeader.setAdditionalRecordCount((short) 0);

        return dnsHeader;
    }

    private DNSQuestion createDNSQuestion(String domain, QueryType queryType) {
        return new DNSQuestion(domain, queryType, (short) 1);
    }
}