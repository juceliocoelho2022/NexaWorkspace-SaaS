package com.nexaworkspace.saas.billing.provider;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.nexaworkspace.saas.billing.BillingDtos;
import com.nexaworkspace.saas.billing.Plan;
import com.nexaworkspace.saas.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StripeBillingGateway implements BillingGateway {
    private final ObjectMapper mapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final String secretKey;
    private final String proPriceId;
    private final String businessPriceId;
    private final String frontendUrl;

    public StripeBillingGateway(ObjectMapper mapper,
                                @Value("${app.billing.stripe.secret-key:}") String secretKey,
                                @Value("${app.billing.stripe.pro-price-id:}") String proPriceId,
                                @Value("${app.billing.stripe.business-price-id:}") String businessPriceId,
                                @Value("${app.billing.frontend-url}") String frontendUrl) {
        this.mapper = mapper;
        this.secretKey = secretKey;
        this.proPriceId = proPriceId;
        this.businessPriceId = businessPriceId;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public BillingDtos.Provider provider() {
        return BillingDtos.Provider.STRIPE;
    }

    @Override
    public BillingDtos.CheckoutResponse createSubscriptionCheckout(UUID tenantId, String customerEmail, Plan plan) {
        String priceId = switch (plan) {
            case PRO -> proPriceId;
            case BUSINESS -> businessPriceId;
            case FREE -> throw new ApiException(HttpStatus.BAD_REQUEST, "Plano FREE não exige checkout");
        };
        if (secretKey.isBlank() || priceId.isBlank()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Stripe não configurado. Defina STRIPE_SECRET_KEY e os Price IDs.");
        }

        Map<String, String> form = new LinkedHashMap<>();
        form.put("mode", "subscription");
        form.put("line_items[0][price]", priceId);
        form.put("line_items[0][quantity]", "1");
        form.put("client_reference_id", tenantId.toString());
        form.put("customer_email", customerEmail);
        form.put("success_url", frontendUrl + "/billing?checkout=success&session_id={CHECKOUT_SESSION_ID}");
        form.put("cancel_url", frontendUrl + "/billing?checkout=cancelled");
        form.put("metadata[tenant_id]", tenantId.toString());
        form.put("metadata[plan]", plan.name());

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.stripe.com/v1/checkout/sessions"))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + secretKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(encode(form)))
                .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode json = mapper.readTree(response.body());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Stripe recusou o checkout: " + json.path("error").path("message").asText("erro externo"));
            }
            return new BillingDtos.CheckoutResponse(provider(), json.path("id").asText(), json.path("url").asText(), "PENDING", false);
        } catch (ApiException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Comunicação com Stripe interrompida");
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Falha ao comunicar com Stripe");
        }
    }

    private String encode(Map<String, String> form) {
        return form.entrySet().stream()
            .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }
}
