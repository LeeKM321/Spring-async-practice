package com.codeit.async.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResult {

    private boolean success;
    private String transactionId;
    private String message;

}
