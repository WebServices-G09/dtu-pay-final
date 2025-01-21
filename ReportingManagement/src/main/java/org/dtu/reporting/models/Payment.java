package org.dtu.reporting.models;

import java.util.UUID;

public class Payment {
    private UUID id;
    private UUID customerToken;
    private UUID merchantId;
    private double amount;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(UUID customerToken) {
        this.customerToken = customerToken;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(UUID merchantId) {
        this.merchantId = merchantId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", customerToken=" + customerToken +
                ", merchantId=" + merchantId +
                ", amount=" + amount +
                '}';
    }
}
