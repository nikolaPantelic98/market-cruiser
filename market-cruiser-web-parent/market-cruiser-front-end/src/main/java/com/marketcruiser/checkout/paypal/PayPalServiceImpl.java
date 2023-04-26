package com.marketcruiser.checkout.paypal;

import com.marketcruiser.settings.PaymentSettingsBag;
import com.marketcruiser.settings.SettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * This class represents the service layer for providing the business logic related to PayPal.
 */
@Component
public class PayPalServiceImpl implements PayPalService{

    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    private final SettingsServiceImpl settingsService;

    @Autowired
    public PayPalServiceImpl(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }


    /**
     * Validates an order by getting its details from the PayPal API and checking if the order is completed.
     *
     * @param orderId the ID of the order to validate
     * @return true if the order is valid, false otherwise
     * @throws PayPalApiException if there is an error while validating the order
     */
    @Override
    public boolean validateOrder(String orderId) throws PayPalApiException {
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);

        return orderResponse.validate(orderId);
    }

    /**
     * Throws an exception for non-OK response status codes from the PayPal API
     *
     * @param statusCode the HTTP status code returned by the PayPal API
     * @throws PayPalApiException if the status code is not OK
     */
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

    /**
     * Gets the details of an order from the PayPal API
     *
     * @param orderId the ID of the order to retrieve the details for
     * @return the PayPalOrderResponse object containing the order details
     * @throws PayPalApiException if there is an error while retrieving the order details
     */
    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);

        HttpStatus statusCode = response.getStatusCode();

        if (!statusCode.equals(HttpStatus.OK)) {
            throwExceptionForNonOKResponse(statusCode);
        }

        return response.getBody();
    }

    /**
     * Sends an HTTP GET request to the PayPal API to get the details of an order
     *
     * @param orderId the ID of the order to retrieve the details for
     * @return the ResponseEntity object containing the order details
     */
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
