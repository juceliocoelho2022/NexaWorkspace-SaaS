package com.nexaworkspace.saas.billing;

import jakarta.validation.constraints.NotNull;

public final class BillingDtos {
    private BillingDtos() {}

    public enum Provider {
        STRIPE,
        MERCADO_PAGO
    }

    public record CheckoutRequest(@NotNull Plan plan, @NotNull Provider provider) {}

    public record CheckoutResponse(
        Provider provider,
        String externalId,
        String checkoutUrl,
        String status,
        boolean sandbox
    ) {}
}
