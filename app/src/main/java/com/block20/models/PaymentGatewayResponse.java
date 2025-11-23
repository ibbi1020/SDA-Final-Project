package com.block20.models;

/**
 * Simplified response from the payment gateway adapter.
 */
public class PaymentGatewayResponse {
    private final boolean success;
    private final String referenceId;
    private final String message;

    public PaymentGatewayResponse(boolean success, String referenceId, String message) {
        this.success = success;
        this.referenceId = referenceId;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getReferenceId() { return referenceId; }
    public String getMessage() { return message; }
}
