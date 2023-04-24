package com.marketcruiser.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderReturnRequest {

    private Long orderId;
    private String reason;
    private String note;

}
