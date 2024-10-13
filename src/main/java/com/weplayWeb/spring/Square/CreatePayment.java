package com.weplayWeb.spring.Square;

import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.authentication.BearerAuthModel;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.Money;
import com.squareup.square.models.RetrieveLocationResponse;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CreatePayment {

   
    private SquareClient squareClient;

    @Value("${square.accessToken}")
    private String accessToken;

    @Value("${square.locationId}")
    private String locationId;

    @Value("${square.environment}")
    private String environment;

    @PostConstruct
    public void init() {
        // Initialize SquareClient after all fields have been injected
        squareClient = new SquareClient.Builder()
                .environment(environment.equals("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION)
                .bearerAuthCredentials(new BearerAuthModel.Builder(accessToken).build())
                .build();
    }
    // Payment processing method
    public PaymentResult process(TokenWrapper tokenObject) throws ApiException, ExecutionException, InterruptedException {

        // Get location information (currency) for the payment
        RetrieveLocationResponse locationResponse = getLocationInformation().get();
        String currency = locationResponse.getLocation().getCurrency();

        // Create Money object for the payment
        Money bodyAmountMoney = new Money.Builder()
                .amount(100L)  // Replace this with the actual amount from frontend or logic
                .currency(currency)
                .build();

        // Build the CreatePaymentRequest using the correct parameters from the Square API
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest.Builder(
                tokenObject.getToken(),                // Source ID (payment token)
                tokenObject.getIdempotencyKey()         // Unique idempotency key
        )
        .amountMoney(bodyAmountMoney)                  // Set the amount (Money object)
        .autocomplete(true)                            // Autocomplete the payment
        .locationId(locationId)                       // Specify the location ID
        .build();

        // Call the Square Payments API to process the payment
        PaymentsApi paymentsApi = squareClient.getPaymentsApi();
        return paymentsApi.createPaymentAsync(createPaymentRequest).thenApply(result -> {
            return new PaymentResult("SUCCESS", null);
        }).exceptionally(exception -> {
            ApiException e = (ApiException) exception.getCause();
            return new PaymentResult("FAILURE", e.getErrors());
        }).join();
    }

    // Helper method to retrieve location information asynchronously
    private CompletableFuture<RetrieveLocationResponse> getLocationInformation() {
        return squareClient.getLocationsApi().retrieveLocationAsync(locationId)
                .thenApply(result -> result)
                .exceptionally(exception -> {
                    System.out.println("Failed to retrieve location information");
                    return null;
                });
    }
}
