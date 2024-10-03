package com.mumuca.dnsresolver.dns.enums;

/**
 * Representa a enumeração de alguns tipos de Response Code (RCODE).
 *
 * <p>Esta enumeração define alguns dos códigos de resposta utilizados nas mensagens DNS,
 * conforme descrito na <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.1">RFC 1035</a>
 * e na <a href="https://datatracker.ietf.org/doc/html/rfc6895#section-2.3">RFC 6895</a>.
 *
 * <p><strong>Códigos:</strong>
 * <ul>
 *   <li><strong>NO_ERROR</strong> (0) - Indica que não houve erro na consulta.</li>
 *   <li><strong>FORM_ERROR</strong> (1) - Indica que houve um erro de formato na consulta.</li>
 *   <li><strong>SERVER_FAILURE</strong> (2) - Indica que o servidor falhou ao processar a consulta.</li>
 *   <li><strong>NAME_ERROR</strong> (3) - Indica que o nome de domínio não existe.</li>
 *   <li><strong>NOT_IMPLEMENTED</strong> (4) - Indica que o tipo de consulta não é suportado.</li>
 *   <li><strong>REFUSED</strong> (5) - Indica que a consulta foi recusada pelo servidor.</li>
 * </ul>
 */
public enum ResultCode {
    NO_ERROR(0),
    FORM_ERROR(1),
    SERVER_FAILURE(2),
    NAME_ERROR(3),
    NOT_IMPLEMENTED(4),
    REFUSED(5);

    private final int code;

    ResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Invalid result code: " + code);
    }
}