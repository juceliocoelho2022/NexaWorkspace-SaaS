package com.nexaworkspace.saas.billing;

import com.nexaworkspace.saas.audit.AuditService;
import com.nexaworkspace.saas.billing.provider.BillingGateway;
import com.nexaworkspace.saas.common.ApiException;
import com.nexaworkspace.saas.event.DomainEvent;
import com.nexaworkspace.saas.event.DomainEventPublisher;
import com.nexaworkspace.saas.security.SaasPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class BillingService {
    private final SubscriptionRepository subscriptions;
    private final AuditService audit;
    private final DomainEventPublisher events;
    private final Map<BillingDtos.Provider, BillingGateway> gateways;
    private final boolean sandbox;
    private final String frontendUrl;

    public BillingService(SubscriptionRepository subscriptions,
                          AuditService audit,
                          DomainEventPublisher events,
                          List<BillingGateway> gateways,
                          @Value("${app.billing.mode:sandbox}") String billingMode,
                          @Value("${app.billing.frontend-url}") String frontendUrl) {
        this.subscriptions = subscriptions;
        this.audit = audit;
        this.events = events;
        this.gateways = new EnumMap<>(BillingDtos.Provider.class);
        gateways.forEach(g -> this.gateways.put(g.provider(), g));
        this.sandbox = !"live".equalsIgnoreCase(billingMode);
        this.frontendUrl = frontendUrl;
    }

    public Map<String, Object> current(SaasPrincipal principal) {
        var subscription = subscriptions.findByTenant_Id(principal.tenantId()).orElseThrow();
        return Map.of(
            "plan", subscription.getPlan(),
            "status", subscription.getStatus(),
            "updatedAt", subscription.getUpdatedAt(),
            "billingMode", sandbox ? "SANDBOX" : "LIVE"
        );
    }

    @Transactional
    public Map<String, Object> changeSandboxPlan(SaasPrincipal principal, Plan plan) {
        if (!sandbox) {
            throw new ApiException(HttpStatus.CONFLICT, "Billing LIVE exige checkout do provedor; alteração direta de plano está desabilitada.");
        }
        var subscription = subscriptions.findByTenant_Id(principal.tenantId()).orElseThrow();
        subscription.changePlan(plan);
        subscriptions.save(subscription);
        audit.record(principal.tenantId(), principal.userId(), "CHANGE_PLAN", "SUBSCRIPTION", subscription.getId().toString());
        events.publish(DomainEvent.of(principal.tenantId(), principal.userId(), "SUBSCRIPTION_PLAN_CHANGED", "SUBSCRIPTION", subscription.getId().toString(), Map.of("plan", plan.name(), "mode", "SANDBOX")));
        return Map.of("plan", subscription.getPlan(), "status", subscription.getStatus(), "billingMode", "SANDBOX");
    }

    public BillingDtos.CheckoutResponse checkout(SaasPrincipal principal, BillingDtos.CheckoutRequest request) {
        if (request.plan() == Plan.FREE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Plano FREE não exige checkout");
        }

        if (sandbox) {
            String id = "sandbox-" + java.util.UUID.randomUUID();
            String url = frontendUrl + "/billing?checkout=sandbox&provider=" + request.provider().name() + "&plan=" + request.plan().name();
            events.publish(DomainEvent.of(principal.tenantId(), principal.userId(), "BILLING_CHECKOUT_CREATED", "SUBSCRIPTION", id, Map.of("provider", request.provider().name(), "plan", request.plan().name(), "mode", "SANDBOX")));
            return new BillingDtos.CheckoutResponse(request.provider(), id, url, "PENDING", true);
        }

        BillingGateway gateway = gateways.get(request.provider());
        if (gateway == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Provedor de pagamento não suportado");
        }
        var checkout = gateway.createSubscriptionCheckout(principal.tenantId(), principal.email(), request.plan());
        audit.record(principal.tenantId(), principal.userId(), "CREATE_CHECKOUT", "SUBSCRIPTION", checkout.externalId());
        events.publish(DomainEvent.of(principal.tenantId(), principal.userId(), "BILLING_CHECKOUT_CREATED", "SUBSCRIPTION", checkout.externalId(), Map.of("provider", request.provider().name(), "plan", request.plan().name(), "mode", "LIVE")));
        return checkout;
    }
}
