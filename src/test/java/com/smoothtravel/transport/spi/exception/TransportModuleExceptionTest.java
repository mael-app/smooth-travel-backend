package com.smoothtravel.transport.spi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TransportModuleExceptionTest {

    @Test
    void shouldFormatMessageWithModuleAndOperation() {
        TransportModuleException exception = new TransportModuleException("sncf", "searchJourneys", "API unreachable");

        assertEquals("[sncf/searchJourneys] API unreachable", exception.getMessage());
        assertEquals("sncf", exception.getModuleId());
        assertEquals("searchJourneys", exception.getOperationName());
    }

    @Test
    void shouldPreserveCauseInSecondConstructor() {
        RuntimeException cause = new RuntimeException("root cause");

        TransportModuleException exception = new TransportModuleException("tam", "findStop", "Stop not found", cause);

        assertEquals("[tam/findStop] Stop not found", exception.getMessage());
        assertEquals("tam", exception.getModuleId());
        assertEquals("findStop", exception.getOperationName());
        assertSame(cause, exception.getCause());
    }
}
