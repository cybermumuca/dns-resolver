package com.mumuca.dnsresolver.servers.udp.resolvers;

import com.mumuca.dnsresolver.dns.DNSHeader;
import com.mumuca.dnsresolver.dns.DNSQuery;
import com.mumuca.dnsresolver.dns.DNSQuestion;
import com.mumuca.dnsresolver.dns.DNSResponse;
import com.mumuca.dnsresolver.dns.enums.QueryType;
import com.mumuca.dnsresolver.dns.enums.ResultCode;
import com.mumuca.dnsresolver.dns.records.*;
import com.mumuca.dnsresolver.servers.exceptions.ServerFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("Cloudflare DNS Resolver Integration Test")
class CloudflareDNSResolverIntegrationTest {

    private final CloudflareDNSResolver cloudflareDNSResolver = new CloudflareDNSResolver();

    @DisplayName("Should resolve DNS query for different query types")
    @ParameterizedTest
    @MethodSource("provideQueryTypesAndDomains")
    void shouldResolveDNSQuery(QueryType queryType, String domain, Class<? extends ResourceRecord> expectedRecordClass) {
        // Arrange
        DNSQuery dnsQuery = new DNSQuery(createDNSHeader(), createDNSQuestion(domain, queryType));

        // Act
        long startTime = System.currentTimeMillis();
        DNSResponse dnsResponse = cloudflareDNSResolver.resolve(dnsQuery);
        long endTime = System.currentTimeMillis();

        // Assert
        assertNotNull(dnsResponse, "DNS response should not be null");

        DNSHeader dnsResponseHeader = dnsResponse.getHeader();
        assertEquals(dnsQuery.getHeader().getId(), dnsResponseHeader.getId(), "Response ID should match query ID");
        assertFalse(dnsResponseHeader.isQuery(), "Response should not be a query");
        assertEquals(dnsQuery.getHeader().getOpcode(), dnsResponseHeader.getOpcode(), "Response opcode should match query opcode");
        assertEquals(dnsQuery.getHeader().isRecursionDesired(), dnsResponseHeader.isRecursionDesired(), "Response recursion desired should match query recursion desired");
        assertTrue(dnsResponseHeader.isRecursionAvailable(), "Response should have recursion available");
        assertEquals(dnsQuery.getHeader().getZ(), dnsResponseHeader.getZ(), "Response Z field should match query Z field");
        assertEquals(ResultCode.NO_ERROR, dnsResponseHeader.getResultCode(), "Response should have NO_ERROR result code");
        assertEquals(1, dnsResponseHeader.getQuestionCount(), "Response should have one question");
        assertTrue(dnsResponseHeader.getAnswerRecordCount() >= 1, "Response should have at least one answer record");

        DNSQuestion dnsResponseQuestion = dnsResponse.getQuestion();
        assertEquals(dnsQuery.getQuestion().qName, dnsResponseQuestion.qName, "Query name should match");
        assertEquals(dnsQuery.getQuestion().qType, dnsResponseQuestion.qType, "Query type should match");
        assertEquals(dnsQuery.getQuestion().qClass, dnsResponseQuestion.qClass, "Query class should match");

        List<ResourceRecord> dnsResponseAnswerRecords = dnsResponse.getAnswerRecords();
        assertFalse(dnsResponseAnswerRecords.isEmpty(), "Response should have answer records");
        assertEquals(dnsResponseHeader.getAnswerRecordCount(), dnsResponseAnswerRecords.size(), "Answer record count should match header count");

        dnsResponseAnswerRecords.forEach(resourceRecord -> {
            assertInstanceOf(expectedRecordClass, resourceRecord, "Resource record should be of expected type");
            assertEquals(resourceRecord.type(), dnsQuery.getQuestion().qType, "Record type should match query type");
        });

        long responseTime = endTime - startTime;
        assertTrue(responseTime < 1000, "DNS resolution should take less than 1 second, but took " + responseTime + " ms");
    }

    @DisplayName("Should throw ServerFailureException when error sending upstream request")
    @Test
    void shouldThrowServerFailureExceptionWhenErrorSendingUpstreamRequest() {
        // Arrange
        DNSQuery dnsQuery = new DNSQuery(createDNSHeader(), createDNSQuestion("www.google.com", QueryType.A));
        CloudflareDNSResolver cloudflareDNSResolverMock = Mockito.mock(CloudflareDNSResolver.class);

        Mockito.when(cloudflareDNSResolverMock.resolve(any(DNSQuery.class)))
                .thenThrow(new ServerFailureException("Error sending upstream request"));


        // Act & Assert
        assertThrows(ServerFailureException.class, () -> cloudflareDNSResolverMock.resolve(dnsQuery),
                "Expected ServerFailureException to be thrown");
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