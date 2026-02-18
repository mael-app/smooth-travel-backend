package com.smoothtravel.transport.spi.exception;

/**
 * Thrown when a {@link com.smoothtravel.transport.spi.TransportModule} encounters
 * an unrecoverable error while executing an operation against its data provider.
 *
 * <p>Implementations should wrap provider-specific exceptions (e.g. HTTP 5xx, timeout,
 * parse failure) in this exception before letting it propagate.
 *
 * <p>Example usage inside a module:
 * <pre>
 *     try {
 *         return sncfClient.searchJourneys(...);
 *     } catch (IOException e) {
 *         throw new TransportModuleException("sncf", "searchJourneys",
 *                 "SNCF API unreachable: " + e.getMessage(), e);
 *     }
 * </pre>
 */
public class TransportModuleException extends RuntimeException {

    private final String moduleId;
    private final String operationName;

    /**
     * Constructs an exception with a descriptive message.
     *
     * @param moduleId      identifier of the failing module, e.g. {@code "sncf"}
     * @param operationName name of the operation that failed, e.g. {@code "searchJourneys"}
     * @param message       human-readable description of the failure
     */
    public TransportModuleException(String moduleId, String operationName, String message) {
        super("[" + moduleId + "/" + operationName + "] " + message);
        this.moduleId = moduleId;
        this.operationName = operationName;
    }

    /**
     * Constructs an exception with a descriptive message and a root cause.
     *
     * @param moduleId      identifier of the failing module
     * @param operationName name of the operation that failed
     * @param message       human-readable description of the failure
     * @param cause         the underlying exception
     */
    public TransportModuleException(String moduleId, String operationName, String message, Throwable cause) {
        super("[" + moduleId + "/" + operationName + "] " + message, cause);
        this.moduleId = moduleId;
        this.operationName = operationName;
    }

    /**
     * Returns the identifier of the module that raised this exception.
     *
     * @return module id, e.g. {@code "sncf"}
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * Returns the name of the operation that failed.
     *
     * @return operation name, e.g. {@code "searchJourneys"}
     */
    public String getOperationName() {
        return operationName;
    }
}
