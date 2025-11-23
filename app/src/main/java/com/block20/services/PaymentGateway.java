package com.block20.services;

import com.block20.models.PaymentRequest;
import com.block20.models.PaymentGatewayResponse;

/**
 * Adapter interface shielding the app from a specific payment processor.
 */
public interface PaymentGateway {
    PaymentGatewayResponse charge(PaymentRequest request);
}
