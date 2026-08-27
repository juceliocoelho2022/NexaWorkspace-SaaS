package com.nexaworkspace.saas.billing.provider;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.nexaworkspace.saas.billing.BillingDtos;
import com.nexaworkspace.saas.billing.Plan;
import com.nexaworkspace.saas.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class MercadoPagoBillingGateway implements BillingGateway {
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final String accessToken;
    private final BigDecimal proMonthly;
    private final BigDecimal businessMonthly;
    private final String frontendUrl;

    public MercadoPagoBillingGateway(ObjectMapper mapper,
                                     @Value("${app.billing.mercado-pago.access-token:}") String accessToken,
                                     @Value("${app.billing.mercado-pago.pro-monthly-brl}") BigDecimal proMonthly,
                                     @Value("${app.billing.mercado-pago.business-monthly-brl}") BigDecimal businessMonthly,
                                     @Value("${app.billing.frontend-url}") String frontendUrl) {
        this.mapper = mapper;
        this.accessToken = accessToken;
        this.proMonthly = proMonthly;
        this.businessMonthly = businessMonthly;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public BillingDtos.Provider provider() {
        return BillingDtos.Provider.MERCADO_PAGO;
    }

    @Override
    public BillingDtos.CheckoutResponse createSubscriptionCheckout(UUID tenantId, String customerEmail, Plan plan) {
        BigDecimal amount = switch (plan) {
            case PRO -> proMonthly;
            case BUSINESS -> businessMonthly;
            case FREE -> throw new ApiException(HttpStatus.BAD_REQUEST, "Plano FREE não exige checkout");
        };
        if (accessToken.isBlank()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Mercado Pago não configurado. Defina MERCADO_PAGO_ACCESS_TOKEN.");
        }

        Map<String, Object> autoRecurring = new LinkedHashMap<>();
        autoRecurring.put("frequency", 1);
        autoRecurring.put("frequency_type", "months");
        autoRecurring.put("transaction_amount", amount);
        autoRecurring.put("currency_id", "BRL");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("reason", "NexaWorkspace " + plan.name());
        payload.put("external_reference", tenantId + ":" + plan.name());
        payload.put("payer_email", customerEmail);
        payload.put("auto_recurring", autoRecurring);
        payload.put("back_url", frontendUrl + "/billing?checkout=success");
        payload.put("status", "pending");

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mercadopago.com/preapproval"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = mapper.readTree(response.body());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Mercado Pago recusou a assinatura: " + json.path("message").asText("erro externo"));
            }
            return new BillingDtos.CheckoutResponse(provider(), json.path("id").asText(), json.path("init_point").asText(), json.path("status").asText("PENDING"), false);
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Comunicação com Mercado Pago interrompida");
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Falha ao comunicar com Mercado Pago");
        }
    }
}
