package com.nexaworkspace.saas.billing.provider;

import com.nexaworkspace.saas.billing.BillingDtos;
import com.nexaworkspace.saas.billing.Plan;

import java.util.UUID;

public interface BillingGateway {
    BillingDtos.Provider provider();
    BillingDtos.CheckoutResponse createSubscriptionCheckout(UUID tenantId, String customerEmail, Plan plan);
}
