package com.marketcruiser.checkout.paypal;

import com.marketcruiser.settings.PaymentSettingsBag;
import com.marketcruiser.settings.SettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class PayPalServiceImpl implements PayPalService{

    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    private final SettingsServiceImpl settingsService;

    @Autowired
    public PayPalServiceImpl(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }


    // validates an order by getting its details from the PayPal API and checking if the order is completed.
    @Override
    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);

        return orderResponse.validate(orderId);
    }

    private void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message = null;

        switch (statusCode) {
            case NOT_FOUND:
                message = "Order ID not found";

            case BAD_REQUEST:
                message = "Bad Request to PayPal Checkout API";

            case INTERNAL_SERVER_ERROR:
                message = "PayPal server error";

            default:
                message = "PayPal returned non-OK status code";
        }

        throw new PayPalApiException(message);
    }

    // gets the details of an order from the PayPal API
    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = response.getStatusCode();

        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNonOKResponse(statusCode);
        }

        return response.getBody();
    }

    // sends an HTTP GET request to the PayPal API to get the details of an order
    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingsBag paymentSettings = settingsService.getPaymentSettings();
        String baseUrl = paymentSettings.getUrl();
        String requestUrl = baseUrl + GET_ORDER_API + orderId;
        String clientId = paymentSettings.getClientId();
        String clientSecret = paymentSettings.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(requestUrl, HttpMethod.GET, request, PayPalOrderResponse.class);
    }
}
