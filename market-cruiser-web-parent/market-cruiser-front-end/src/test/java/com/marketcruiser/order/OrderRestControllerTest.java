package com.marketcruiser.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    /**
     * Tests the scenario where the order ID provided in the return request does not exist in the system.
     * Expects the method to return an HTTP status of 404 Not Found.
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithUserDetails("npantelic4266@gmail.com")
    public void testSendOrderReturnRequestFailed() throws Exception {
        Long orderId = 91919L;
        OrderReturnRequest returnRequest = new OrderReturnRequest(orderId, "", "");

        String requestURL = "/orders/return";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    /**
     * Tests the scenario where a valid order return request is submitted and processed successfully.
     * Expects the method to return an HTTP status of 200 OK and a response containing an OrderReturnResponse object
     * with the ID of the order that was requested for return.
     * @throws Exception if an error occurs during the test
     */
    @Test
    @WithUserDetails("npantelic4266@gmail.com")
    public void testSendOrderReturnRequestSuccessful() throws Exception {
        Long orderId = 14L;
        String reason = "I bought the wrong items.";
        String note = "Please return my money.";
        OrderReturnRequest returnRequest = new OrderReturnRequest(orderId, reason, note);

        String requestURL = "/orders/return";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
