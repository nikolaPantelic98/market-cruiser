package com.marketcruiser.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a return request for an order.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderReturnRequest {

    private Long orderId;
    private String reason;
    private String note;

}
