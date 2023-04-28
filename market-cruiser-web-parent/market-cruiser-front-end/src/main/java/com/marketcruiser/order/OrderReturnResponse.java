package com.marketcruiser.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response for an order return request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderReturnResponse {

    private Long orderId;
}
